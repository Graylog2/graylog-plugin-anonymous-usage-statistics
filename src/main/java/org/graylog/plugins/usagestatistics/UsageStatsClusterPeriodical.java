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
import com.google.common.collect.EvictingQueue;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import org.graylog.plugins.usagestatistics.providers.CompressingHttpClient;
import org.graylog.plugins.usagestatistics.providers.SmileObjectMapper;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.cluster.ClusterId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;

@Singleton
public class UsageStatsClusterPeriodical extends UsageStatsPeriodical {
    private static final Logger LOG = LoggerFactory.getLogger(UsageStatsClusterPeriodical.class);

    private final ServerStatus serverStatus;
    private final UsageStatsClusterService usageStatsClusterService;

    @Inject
    public UsageStatsClusterPeriodical(UsageStatsClusterService usageStatsClusterService,
                                       ServerStatus serverStatus,
                                       UsageStatsConfiguration config,
                                       ClusterConfigService clusterConfigService,
                                       @CompressingHttpClient OkHttpClient httpClient,
                                       @SmileObjectMapper ObjectMapper objectMapper) {
        this(
                usageStatsClusterService,
                serverStatus,
                config,
                clusterConfigService,
                EvictingQueue.<UsageStatsRequest>create(config.getMaxQueueSize()),
                httpClient,
                objectMapper);
    }

    private UsageStatsClusterPeriodical(UsageStatsClusterService usageStatsClusterService,
                                        ServerStatus serverStatus,
                                        UsageStatsConfiguration config,
                                        ClusterConfigService clusterConfigService,
                                        EvictingQueue<UsageStatsRequest> evictingQueue,
                                        OkHttpClient httpClient,
                                        ObjectMapper objectMapper) {
        super(config, clusterConfigService, evictingQueue, httpClient, objectMapper, "cluster-%s.smile");
        this.serverStatus = serverStatus;
        this.usageStatsClusterService = usageStatsClusterService;
    }

    protected URL getUrl() {
        final ClusterId clusterId = clusterConfigService.get(ClusterId.class);
        if (clusterId != null) {
            return HttpUrl.get(config.getUrl()).newBuilder()
                    .addPathSegment("cluster")
                    .addPathSegment(clusterId.clusterId())
                    .build()
                    .url();
        } else {
            return null;
        }
    }

    @Override
    protected byte[] buildPayload() {
        try {
            return objectMapper.writeValueAsBytes(usageStatsClusterService.buildClusterDataSet());
        } catch (JsonProcessingException e) {
            LOG.error("Error while serializing usage statistics data", e);
            return null;
        }
    }

    @Override
    public boolean startOnThisNode() {
        return config.isEnabled()
                && serverStatus.hasCapability(ServerStatus.Capability.MASTER)
                && !serverStatus.hasCapability(ServerStatus.Capability.LOCALMODE);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}