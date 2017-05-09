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

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import org.graylog2.indexer.gson.GsonUtils;
import org.graylog2.system.stats.ClusterStatsService;
import org.graylog2.system.stats.elasticsearch.ElasticsearchStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ElasticsearchCollector {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchCollector.class);

    private final ClusterStatsService clusterStatsService;
    private final JestClient jestClient;

    @Inject
    public ElasticsearchCollector(ClusterStatsService clusterStatsService, JestClient jestClient) {
        this.clusterStatsService = clusterStatsService;
        this.jestClient = jestClient;
    }

    public Set<ElasticsearchNodeInfo> getNodeInfos() {
        final JsonObject nodesMap = fetchNodeInfos();
        if (nodesMap == null) {
            return Collections.emptySet();
        }
        final Set<ElasticsearchNodeInfo> elasticsearchNodeInfos = Sets.newHashSetWithExpectedSize(nodesMap.entrySet().size());
        Optional.of(nodesMap)
                .map(JsonObject::entrySet)
                .map(Iterable::spliterator)
                .map(splitr -> StreamSupport.stream(splitr, false))
                .orElse(Stream.empty())
                .forEach(entry -> {

                    // TODO remove these as soon as the backend service treats HostInfo as optional
                    // the host info details aren't available in Elasticsearch 2.x anymore, but we still report the empty
                    // bean because the backend service still expects some data (even if it is empty)
                    final MacAddress macAddress = MacAddress.EMPTY;
                    final HostInfo.Cpu cpu = null;
                    final HostInfo.Memory memory = null;
                    final HostInfo.Memory swap = null;
                    final HostInfo hostInfo = HostInfo.create(macAddress, cpu, memory, swap);

                    final Optional<JsonObject> jvm = Optional.of(entry.getValue())
                            .map(JsonElement::getAsJsonObject)
                            .map(nodeInfo -> GsonUtils.asJsonObject(nodeInfo.get("jvm")));

                    final List<String> garbageCollectors = jvm
                            .map(jvmInfo -> GsonUtils.asJsonArray(jvmInfo.get("gc_collectors")))
                            .map(Iterable::spliterator)
                            .map(splitr -> StreamSupport.stream(splitr, false))
                            .orElse(Stream.empty())
                            .map(String::valueOf)
                            .collect(Collectors.toList());

                    final Optional<JsonObject> memInfo = jvm.map(jvmInfo -> GsonUtils.asJsonObject(jvmInfo.get("mem")));

                    final JvmInfo.Memory jvmMemory = JvmInfo.Memory.create(
                            memInfo.map(mem -> GsonUtils.asLong(mem.get("heap_init_in_bytes"))).orElse(-1L),
                            memInfo.map(mem -> GsonUtils.asLong(mem.get("heap_max_in_bytes"))).orElse(-1L),
                            memInfo.map(mem -> GsonUtils.asLong(mem.get("non_heap_init_in_bytes"))).orElse(-1L),
                            memInfo.map(mem -> GsonUtils.asLong(mem.get("non_heap_max_in_bytes"))).orElse(-1L),
                            memInfo.map(mem -> GsonUtils.asLong(mem.get("direct_max_in_bytes"))).orElse(-1L)
                    );

                    final Optional<JsonObject> osInfo = Optional.of(entry.getValue())
                            .map(JsonElement::getAsJsonObject)
                            .map(nodeInfo -> GsonUtils.asJsonObject(nodeInfo.get("os")));

                    final JvmInfo.Os jvmOs = JvmInfo.Os.create(
                            osInfo.map(os -> GsonUtils.asString(os.get("name"))).orElse("<unknown>"),
                            osInfo.map(os -> GsonUtils.asString(os.get("version"))).orElse("<unknown>"),
                            osInfo.map(os -> GsonUtils.asString(os.get("arch"))).orElse("<unknown>")
                    );
                    final JvmInfo jvmInfo = JvmInfo.create(
                            jvm.map(j -> GsonUtils.asString(j.get("version"))).orElse("<unknown>"),
                            jvm.map(j -> GsonUtils.asString(j.get("vm_name"))).orElse("<unknown>"),
                            jvm.map(j -> GsonUtils.asString(j.get("vm_version"))).orElse("<unknown>"),
                            jvm.map(j -> GsonUtils.asString(j.get("vm_vendor"))).orElse("<unknown>"),
                            jvmOs,
                            jvmMemory,
                            garbageCollectors
                    );
                    final String esVersion = Optional.of(entry.getValue())
                            .map(JsonElement::getAsJsonObject)
                            .map(nodeInfo -> GsonUtils.asString(nodeInfo.get("version")))
                            .orElse("<unknown>");

                    final ElasticsearchNodeInfo elasticsearchNodeInfo = ElasticsearchNodeInfo.create(
                            esVersion,
                            hostInfo,
                            jvmInfo
                    );

                    elasticsearchNodeInfos.add(elasticsearchNodeInfo);
                });

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

    private JsonObject fetchNodeInfos() {
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
        return Optional.of(result.getJsonObject())
                .map(json -> GsonUtils.asJsonObject(json.get("nodes")))
                .orElseThrow(() -> new IllegalStateException(errorMessage + " Unable to parse reply: " + result.getJsonString()));
    }
}
