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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
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

    public UsageStatsOptOutState getOptOutState() {
        return clusterConfigService.getOrDefault(UsageStatsOptOutState.class, UsageStatsOptOutState.create(false));
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
            public void onFailure(Request request, IOException e) {
                LOG.error("Error while sending anonymous usage statistics opt-out");
                LOG.debug("Error details", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
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
