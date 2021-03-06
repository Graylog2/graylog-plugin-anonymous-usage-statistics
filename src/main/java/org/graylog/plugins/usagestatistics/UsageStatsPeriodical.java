/**
 * This file is part of Graylog Anonymous Usage Statistics Plugin.
 *
 * Graylog Anonymous Usage Statistics Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog Anonymous Usage Statistics Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog Anonymous Usage Statistics Plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.usagestatistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.google.common.primitives.Ints;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.periodical.Periodical;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.graylog.plugins.usagestatistics.UsageStatsConstants.CONTENT_TYPE;
import static org.graylog.plugins.usagestatistics.UsageStatsConstants.USAGE_STATS_VERSION;
import static org.graylog.plugins.usagestatistics.UsageStatsConstants.USER_AGENT;

public abstract class UsageStatsPeriodical extends Periodical {
    protected final UsageStatsConfiguration config;
    protected final ClusterConfigService clusterConfigService;
    protected final EvictingQueue<UsageStatsRequest> cachedRequestsQueue;
    protected final ObjectMapper objectMapper;
    protected final OkHttpClient httpClient;
    protected final String filenamePattern;

    private final Logger log = getLogger();

    protected UsageStatsPeriodical(UsageStatsConfiguration config,
                                   ClusterConfigService clusterConfigService,
                                   EvictingQueue<UsageStatsRequest> usageStatsRequestsQueue,
                                   OkHttpClient httpClient,
                                   ObjectMapper objectMapper,
                                   String filenamePattern) {
        this.config = checkNotNull(config);
        this.clusterConfigService = checkNotNull(clusterConfigService);
        this.cachedRequestsQueue = checkNotNull(usageStatsRequestsQueue);
        this.httpClient = checkNotNull(httpClient);
        this.objectMapper = checkNotNull(objectMapper);
        this.filenamePattern = checkNotNull(filenamePattern);
    }

    protected abstract byte[] buildPayload();

    protected abstract URL getUrl();

    protected boolean isEnabled() {
        if (!config.isEnabled()) {
            return false;
        }

        final UsageStatsOptOutState state = clusterConfigService.getOrDefault(UsageStatsOptOutState.class,
                UsageStatsOptOutState.create(false));

        return !state.isOptOut();
    }

    @Override
    public void doRun() {
        if (!isEnabled()) {
            log.debug("Anonymous usage statistics disabled: Not transmitting statistics");
            return;
        }

        log.debug("Anonymous usage statistics activated: Transmitting node statistics.");
        final byte[] requestBody = buildPayload();

        if (config.isOfflineMode()) {
            final String filename = String.format(filenamePattern, DateTime.now(DateTimeZone.UTC).getMillis());

            if (!config.getDirectory().exists()) {
                boolean success = config.getDirectory().mkdirs();
                if (!success) {
                    log.error("Couldn't create directory {}", config.getDirectory().getAbsolutePath());
                    return;
                }
            }

            final File file = new File(config.getDirectory(), filename);

            log.debug("Anonymous usage statistics are in offline mode. Writing data into {}", file);
            try (final OutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(requestBody);
            } catch (IOException e) {
                log.warn("Couldn't write usage statistics into " + file, e);
            }
        } else {
            final Headers headers = new Headers.Builder()
                    .add(HttpHeaders.USER_AGENT, USER_AGENT)
                    .add("X-Usage-Statistics-Version", USAGE_STATS_VERSION)
                    .build();

            final UsageStatsRequest request = UsageStatsRequest.create(headers, requestBody);

            boolean success = uploadDataSet(request);
            if (!success) {
                cachedRequestsQueue.add(request);
            } else if (!cachedRequestsQueue.isEmpty()) {
                log.debug("Trying to upload {} queued data sets", cachedRequestsQueue.size());
                uploadQueuedDataSets();
            }
        }
    }

    protected void uploadQueuedDataSets() {
        final List<UsageStatsRequest> unsuccessfulRequests = Lists.newArrayListWithCapacity(cachedRequestsQueue.size());

        while (!cachedRequestsQueue.isEmpty()) {
            final UsageStatsRequest request = cachedRequestsQueue.poll();
            boolean success = uploadDataSet(request);

            if (!success) {
                log.debug("Couldn't successfully upload anonymous usage statistics, re-queueing data");
                unsuccessfulRequests.add(request);
            }
        }

        cachedRequestsQueue.addAll(unsuccessfulRequests);
    }

    protected boolean uploadDataSet(UsageStatsRequest usageStatsRequest) {
        final URL url = getUrl();
        if (url == null) {
            log.warn("Error while uploading anonymous usage statistics. "
                    + "Please check the 'usage_statistics_url' setting in your configuration.");
            return false;
        }

        final Request request = new Request.Builder()
                .url(url)
                .headers(usageStatsRequest.headers())
                .post(RequestBody.create(CONTENT_TYPE, usageStatsRequest.body()))
                .build();

        try (final Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.warn("Couldn't successfully upload anonymous usage statistics: {}", response);
                return false;
            }
        } catch (IOException e) {
            log.error("Error while uploading anonymous usage statistics");
            log.debug("Error details", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean masterOnly() {
        return false;
    }

    @Override
    public boolean isDaemon() {
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        return Ints.saturatedCast(config.getInitialDelay().toSeconds());
    }

    @Override
    public int getPeriodSeconds() {
        return Ints.saturatedCast(config.getReportInterval().toSeconds());
    }
}
