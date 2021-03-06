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
import com.google.common.collect.EvictingQueue;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
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

    @Override
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
