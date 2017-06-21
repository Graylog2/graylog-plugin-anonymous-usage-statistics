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
package org.graylog.plugins.usagestatistics.collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.cluster.NodesInfo;
import org.graylog.plugins.usagestatistics.dto.HostInfo;
import org.graylog.plugins.usagestatistics.dto.JvmInfo;
import org.graylog.plugins.usagestatistics.dto.MacAddress;
import org.graylog.plugins.usagestatistics.dto.elasticsearch.ClusterHealth;
import org.graylog.plugins.usagestatistics.dto.elasticsearch.ElasticsearchClusterStats;
import org.graylog.plugins.usagestatistics.dto.elasticsearch.ElasticsearchNodeInfo;
import org.graylog.plugins.usagestatistics.dto.elasticsearch.IndicesStats;
import org.graylog.plugins.usagestatistics.dto.elasticsearch.NodesStats;
import org.graylog2.indexer.cluster.jest.JestUtils;
import org.graylog2.system.stats.ClusterStatsService;
import org.graylog2.system.stats.elasticsearch.ElasticsearchStats;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ElasticsearchCollector {
    private final ClusterStatsService clusterStatsService;
    private final JestClient jestClient;

    @Inject
    public ElasticsearchCollector(ClusterStatsService clusterStatsService, JestClient jestClient) {
        this.clusterStatsService = clusterStatsService;
        this.jestClient = jestClient;
    }

    public Set<ElasticsearchNodeInfo> getNodeInfos() {
        final JsonNode nodesMap = fetchNodeInfos();
        if (nodesMap.isMissingNode()) {
            return Collections.emptySet();
        }
        final Set<ElasticsearchNodeInfo> elasticsearchNodeInfos = Sets.newHashSet();
        final Iterator<Map.Entry<String, JsonNode>> fields = nodesMap.fields();
        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> entry = fields.next();
            // TODO remove these as soon as the backend service treats HostInfo as optional
            // the host info details aren't available in Elasticsearch 2.x anymore, but we still report the empty
            // bean because the backend service still expects some data (even if it is empty)
            final MacAddress macAddress = MacAddress.EMPTY;
            final HostInfo.Cpu cpu = null;
            final HostInfo.Memory memory = null;
            final HostInfo.Memory swap = null;
            final HostInfo hostInfo = HostInfo.create(macAddress, cpu, memory, swap);

            final JsonNode jvm = entry.getValue().path("jvm");
            final List<String> garbageCollectors = new ArrayList<>();
            for(JsonNode jsonNode :jvm.path("gc_collectors")) {
                if(jsonNode.isTextual()) {
                    garbageCollectors.add(jsonNode.asText());
                }
            }
            final JsonNode memInfo = jvm.path("mem");

            final JvmInfo.Memory jvmMemory = JvmInfo.Memory.create(
                    memInfo.path("heap_init_in_bytes").asLong(-1L),
                    memInfo.path("heap_max_in_bytes").asLong(-1L),
                    memInfo.path("non_heap_init_in_bytes").asLong(-1L),
                    memInfo.path("non_heap_max_in_bytes").asLong(-1L),
                    memInfo.path("direct_max_in_bytes").asLong(-1L)
            );

            final JsonNode osInfo = entry.getValue().path("os");

            final JvmInfo.Os jvmOs = JvmInfo.Os.create(
                    osInfo.path("name").asText("<unknown>"),
                    osInfo.path("version").asText("<unknown>"),
                    osInfo.path("arch").asText("<unknown>")
            );
            final JvmInfo jvmInfo = JvmInfo.create(
                    jvm.path("version").asText("<unknown>"),
                    jvm.path("vm_name").asText("<unknown>"),
                    jvm.path("vm_version").asText("<unknown>"),
                    jvm.path("vm_vendor").asText("<unknown>"),
                    jvmOs,
                    jvmMemory,
                    garbageCollectors
            );
            final String esVersion = entry.getValue().path("version").asText("<unknown>");

            final ElasticsearchNodeInfo elasticsearchNodeInfo = ElasticsearchNodeInfo.create(
                    esVersion,
                    hostInfo,
                    jvmInfo
            );

            elasticsearchNodeInfos.add(elasticsearchNodeInfo);
        }

        return elasticsearchNodeInfos;
    }

    public ElasticsearchClusterStats getClusterStats() {
        final ElasticsearchStats stats = clusterStatsService.elasticsearchStats();
        return ElasticsearchClusterStats.create(
                ClusterHealth.fromClusterHealth(stats.clusterHealth()),
                NodesStats.fromNodesStats(stats.nodesStats()),
                IndicesStats.fromIndicesStats(stats.indicesStats())
        );
    }

    private JsonNode fetchNodeInfos() {
        final String errorMessage = "Unable to fetch node infos.";
        final NodesInfo.Builder requestBuilder = new NodesInfo.Builder()
                .withHttp()
                .withJvm()
                .withNetwork()
                .withOs()
                .withPlugins()
                .withProcess()
                .withSettings()
                .withThreadPool()
                .withTransport();
        final JestResult result = JestUtils.execute(jestClient, requestBuilder.build(), () -> errorMessage);
        final JsonNode nodeInfos = result.getJsonObject().path("nodes");
        if (nodeInfos.isMissingNode()) {
            throw new IllegalStateException(errorMessage + " Unable to parse reply: " + result.getJsonString());
        }

        return nodeInfos;
    }
}
