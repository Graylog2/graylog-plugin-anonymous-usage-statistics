/**
 * Copyright (C) 2015 Graylog, Inc. (hello@graylog.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.graylog.plugins.usagestatistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import com.google.common.primitives.Ints;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import org.graylog2.plugin.Version;
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

public abstract class UsageStatsPeriodical extends Periodical {
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/x-jackson-smile");
    private static final String USAGE_STATS_VERSION = UsageStatsMetaData.VERSION.toString();
    private static final String USER_AGENT = "Graylog " + Version.CURRENT_CLASSPATH;

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

    @Override
    public void doRun() {
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
        if (getUrl() == null) {
            log.error("Error while uploading anonymous usage statistics. "
                    + "Please check the 'usage_statistics_url' setting in your configuration.");
            return false;
        }

        final Request request = new Request.Builder()
                .url(getUrl())
                .headers(usageStatsRequest.headers())
                .post(RequestBody.create(CONTENT_TYPE, usageStatsRequest.body()))
                .build();

        final Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            log.error("Error while uploading anonymous usage statistics");
            log.debug("Error details", e);
            return false;
        }

        if (!response.isSuccessful()) {
            log.warn("Couldn't successfully upload anonymous usage statistics: {}", response);
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
        return 300;
    }

    @Override
    public int getPeriodSeconds() {
        return Ints.saturatedCast(config.getReportInterval().toSeconds());
    }
}