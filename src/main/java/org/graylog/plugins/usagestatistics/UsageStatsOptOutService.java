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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.graylog.plugins.usagestatistics.providers.SmileObjectMapper;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.cluster.ClusterId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.URL;

import static org.graylog.plugins.usagestatistics.UsageStatsConstants.CONTENT_TYPE;
import static org.graylog.plugins.usagestatistics.UsageStatsConstants.USAGE_STATS_VERSION;
import static org.graylog.plugins.usagestatistics.UsageStatsConstants.USER_AGENT;

public class UsageStatsOptOutService {
    private static final Logger LOG = LoggerFactory.getLogger(UsageStatsOptOutService.class);

    private final ClusterConfigService clusterConfigService;
    private final UsageStatsConfiguration config;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Inject
    public UsageStatsOptOutService(ClusterConfigService clusterConfigService,
                                   UsageStatsConfiguration config,
                                   OkHttpClient httpClient,
                                   @SmileObjectMapper ObjectMapper objectMapper) {
        this.clusterConfigService = clusterConfigService;
        this.config = config;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Nullable
    public UsageStatsOptOutState getOptOutState() {
        return clusterConfigService.get(UsageStatsOptOutState.class);
    }

    public void setOptOutState(final UsageStatsOptOutState optOutState) {
        if (optOutState == null) {
            return;
        }

        LOG.debug("Writing opt-out state to cluster config: {}", optOutState);
        clusterConfigService.write(optOutState);

        if (optOutState.isOptOut()) {
            LOG.info("Transmission of anonymous usage stats: disabled (opt-out)");
        } else {
            LOG.info("Transmission of anonymous usage stats: enabled (opt-in)");
            LOG.debug("Not sending opt-in request.");
            return;
        }

        final URL url = getUrl();

        if (url == null) {
            LOG.debug("Not sending opt-out request, 'usage_statistics_url' is not set.");
            return;
        }

        final Headers headers = new Headers.Builder()
                .add(HttpHeaders.USER_AGENT, USER_AGENT)
                .add("X-Usage-Statistics-Version", USAGE_STATS_VERSION)
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .post(RequestBody.create(CONTENT_TYPE, buildPayload()))
                .build();

        // Run the opt-out request outside of the calling thread so it does not block.
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOG.error("Error while sending anonymous usage statistics opt-out");
                LOG.debug("Error details", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    LOG.warn("Couldn't successfully send usage statistics opt-out: {}", response);
                }
            }
        });
    }

    @Nullable
    protected URL getUrl() {
        final ClusterId clusterId = clusterConfigService.get(ClusterId.class);

        if (clusterId != null) {
            return HttpUrl.get(config.getUrl()).newBuilder()
                    .addPathSegment("cluster")
                    .addPathSegment(clusterId.clusterId())
                    .addPathSegment("optout")
                    .build()
                    .url();
        } else {
            return null;
        }
    }

    @Nullable
    protected byte[] buildPayload() {
        try {
            return objectMapper.writeValueAsBytes(ImmutableMap.<String, String>builder().build());
        } catch (JsonProcessingException e) {
            LOG.error("Error while serializing usage statistics data", e);
            return null;
        }
    }
}
