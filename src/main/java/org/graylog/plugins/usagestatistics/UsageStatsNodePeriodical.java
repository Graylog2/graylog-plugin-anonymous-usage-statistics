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
import com.squareup.okhttp.OkHttpClient;
import org.graylog.plugins.usagestatistics.providers.CompressingHttpClient;
import org.graylog.plugins.usagestatistics.providers.SmileObjectMapper;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.cluster.ClusterId;
import org.graylog2.plugin.system.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URL;

@Singleton
public class UsageStatsNodePeriodical extends UsageStatsPeriodical {
    private static final Logger LOG = LoggerFactory.getLogger(UsageStatsNodePeriodical.class);

    private final ServerStatus serverStatus;
    private final NodeId nodeId;
    private final UsageStatsNodeService usageStatsNodeService;
    private URL url = null;

    @Inject
    public UsageStatsNodePeriodical(UsageStatsNodeService usageStatsNodeService,
                                    NodeId nodeId,
                                    ServerStatus serverStatus,
                                    UsageStatsConfiguration config,
                                    ClusterConfigService clusterConfigService,
                                    @CompressingHttpClient OkHttpClient httpClient,
                                    @SmileObjectMapper ObjectMapper objectMapper) {
        this(
                usageStatsNodeService,
                nodeId,
                serverStatus,
                config,
                clusterConfigService,
                EvictingQueue.<UsageStatsRequest>create(config.getMaxQueueSize()),
                httpClient,
                objectMapper);
    }

    private UsageStatsNodePeriodical(UsageStatsNodeService usageStatsNodeService,
                                     NodeId nodeId,
                                     ServerStatus serverStatus,
                                     UsageStatsConfiguration config,
                                     ClusterConfigService clusterConfigService,
                                     EvictingQueue<UsageStatsRequest> evictingQueue,
                                     OkHttpClient httpClient,
                                     ObjectMapper objectMapper) {
        super(config, clusterConfigService, evictingQueue, httpClient, objectMapper,
                "node-" + nodeId.anonymize() + "-%s.smile");
        this.serverStatus = serverStatus;
        this.nodeId = nodeId;
        this.usageStatsNodeService = usageStatsNodeService;
    }

    protected URL getUrl() {
        if (url == null) {
            try {
                ClusterId clusterId = clusterConfigService.getOrDefault(ClusterId.class, ClusterId.create("unknown"));
                url = config.getUrl().resolve("cluster/" + clusterId.clusterId() + "/node/" + nodeId.anonymize()).toURL();
            } catch (MalformedURLException e) {
                LOG.debug("Couldn't build service URL", e);
            }
        }

        return url;
    }

    @Override
    protected byte[] buildPayload() {
        try {
            return objectMapper.writeValueAsBytes(usageStatsNodeService.buildNodeDataSet());
        } catch (JsonProcessingException e) {
            LOG.error("Error while serializing usage statistics data", e);
            return null;
        }
    }

    @Override
    public boolean startOnThisNode() {
        return config.isEnabled() && !serverStatus.hasCapability(ServerStatus.Capability.LOCALMODE);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}