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
package org.graylog.plugins.usagestatistics.collectors;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.github.joschi.jadconfig.util.Duration;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.SystemUtils;
import org.graylog.plugins.usagestatistics.UsageStatsMetaData;
import org.graylog.plugins.usagestatistics.dto.BufferStats;
import org.graylog.plugins.usagestatistics.dto.Histogram;
import org.graylog.plugins.usagestatistics.dto.HostInfo;
import org.graylog.plugins.usagestatistics.dto.HostStats;
import org.graylog.plugins.usagestatistics.dto.JournalStats;
import org.graylog.plugins.usagestatistics.dto.JvmInfo;
import org.graylog.plugins.usagestatistics.dto.MacAddress;
import org.graylog.plugins.usagestatistics.dto.NodeDataSet;
import org.graylog.plugins.usagestatistics.dto.NodeInfo;
import org.graylog.plugins.usagestatistics.dto.NodeRole;
import org.graylog.plugins.usagestatistics.dto.NodeStats;
import org.graylog.plugins.usagestatistics.dto.Os;
import org.graylog.plugins.usagestatistics.dto.PluginInfo;
import org.graylog.plugins.usagestatistics.dto.SessionStats;
import org.graylog.plugins.usagestatistics.dto.ThroughputStats;
import org.graylog.plugins.usagestatistics.util.MetricUtils;
import org.graylog2.inputs.Input;
import org.graylog2.inputs.InputService;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.cluster.ClusterId;
import org.graylog2.plugin.system.NodeId;
import org.graylog2.rest.resources.system.SessionsResource;
import org.graylog2.security.realm.SessionAuthenticator;
import org.graylog2.shared.system.stats.StatsService;
import org.graylog2.shared.system.stats.jvm.JvmStats;
import org.graylog2.shared.system.stats.network.NetworkStats;
import org.graylog2.shared.system.stats.os.OsStats;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;

import static com.codahale.metrics.MetricRegistry.name;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.graylog.plugins.usagestatistics.util.MetricUtils.safeGetCounter;
import static org.graylog.plugins.usagestatistics.util.MetricUtils.safeGetMeter;

@Singleton
public class NodeCollector {
    private static final long JVM_START = System.nanoTime();

    private final NodeId nodeId;
    private final ServerStatus serverStatus;
    private final MetricRegistry metricRegistry;
    private final StatsService statsService;
    private final InputService inputService;
    private final ClusterConfigService clusterConfigService;
    private final Set<PluginMetaData> plugins;
    private final long reportIntervalMs;
    private final String installationSource;

    @Inject
    public NodeCollector(NodeId nodeId,
                         ServerStatus serverStatus,
                         MetricRegistry metricRegistry,
                         StatsService statsService,
                         InputService inputService,
                         ClusterConfigService clusterConfigService,
                         Set<PluginMetaData> plugins,
                         @Named("usage_statistics_report_interval") Duration reportInterval,
                         @Named("installation_source") String installationSource) {
        this.nodeId = checkNotNull(nodeId);
        this.serverStatus = checkNotNull(serverStatus);
        this.metricRegistry = checkNotNull(metricRegistry);
        this.statsService = checkNotNull(statsService);
        this.inputService = checkNotNull(inputService);
        this.clusterConfigService = checkNotNull(clusterConfigService);
        this.plugins = checkNotNull(plugins);
        this.reportIntervalMs = checkNotNull(reportInterval).toMilliseconds();
        this.installationSource = checkNotNull(installationSource);
    }

    public NodeDataSet getNodeDataSet() {
        final ClusterId clusterId = clusterConfigService.getOrDefault(ClusterId.class, ClusterId.create(""));

        return NodeDataSet.create(
                String.valueOf(UsageStatsMetaData.VERSION),
                clusterId.clusterId(),
                nodeId.anonymize(),
                System.currentTimeMillis(),
                reportIntervalMs,
                buildNodeInfo(),
                buildNodeStats(),
                buildHostInfo(),
                buildHostStats(),
                buildJvmSpecs()
        );
    }

    private NodeInfo buildNodeInfo() {
        return NodeInfo.create(
                NodeRole.fromCapabilities(serverStatus),
                Version.CURRENT_CLASSPATH.toString(),
                buildPluginInfo(plugins),
                buildOsInfo(),
                installationSource
        );
    }

    private Os buildOsInfo() {
        org.jsoftbiz.utils.OS os = org.jsoftbiz.utils.OS.getOs();
        return Os.create(
                os.getName(),
                os.getVersion(),
                os.getArch(),
                os.getPlatformName());
    }

    private Set<PluginInfo> buildPluginInfo(Set<PluginMetaData> plugins) {
        final Set<PluginInfo> pluginInfos = Sets.newHashSetWithExpectedSize(plugins.size());
        for (PluginMetaData pluginMetaData : plugins) {
            pluginInfos.add(PluginInfo.create(
                            pluginMetaData.getUniqueId(),
                            pluginMetaData.getName(),
                            pluginMetaData.getVersion().toString()
                    )
            );
        }

        return pluginInfos;
    }

    private HostInfo buildHostInfo() {
        final MacAddress macAddress = findMacAddress();
        final OsStats osStats = statsService.osStats();
        final HostInfo.Cpu cpu = HostInfo.Cpu.create(
                osStats.processor().model(),
                osStats.processor().vendor(),
                osStats.processor().mhz(),
                osStats.processor().totalCores(),
                osStats.processor().totalSockets(),
                osStats.processor().coresPerSocket(),
                osStats.processor().cacheSize()
        );
        final HostInfo.Memory memory = HostInfo.Memory.create(osStats.memory().total());
        final HostInfo.Memory swap = HostInfo.Memory.create(osStats.swap().total());

        return HostInfo.create(macAddress, cpu, memory, swap);
    }

    private MacAddress findMacAddress() {
        final NetworkStats networkStats = statsService.networkStats();
        final NetworkStats.Interface networkInterface = networkStats.interfaces().get(networkStats.primaryInterface());
        final MacAddress macAddress;
        if (networkInterface == null || isNullOrEmpty(networkInterface.macAddress())) {
            macAddress = MacAddress.EMPTY;
        } else {
            macAddress = MacAddress.create(networkInterface.macAddress());
        }

        return macAddress;
    }

    private JvmInfo buildJvmSpecs() {
        final JvmStats jvmStats = statsService.jvmStats();
        final JvmInfo.Os os = JvmInfo.Os.create(
                SystemUtils.OS_NAME,
                SystemUtils.OS_VERSION,
                SystemUtils.OS_ARCH
        );
        final JvmInfo.Memory jvmMemory = JvmInfo.Memory.create(
                jvmStats.mem().heapInit(),
                jvmStats.mem().heapMax(),
                jvmStats.mem().nonHeapInit(),
                jvmStats.mem().nonHeapMax(),
                jvmStats.mem().directMemoryMax()
        );

        return JvmInfo.create(
                jvmStats.version(),
                jvmStats.vmName(),
                jvmStats.vmVersion(),
                jvmStats.vmVendor(),
                os,
                jvmMemory,
                jvmStats.garbageCollectors()
        );
    }

    private NodeStats buildNodeStats() {
        final long uptime = (System.nanoTime() - JVM_START) / 1_000_000L;

        // break down throughput numbers by input type
        final Map<String, ThroughputStats.Throughput> perInputThroughput = Maps.newHashMap();
        for (Input input : inputService.allOfThisNode(nodeId.toString())) {
            final Meter incomingMessages = MetricUtils.safeGetMeter(metricRegistry,
                                                                    name(input.getType(),
                                                                         input.getId(),
                                                                         "incomingMessages"));
            final Meter rawSize = MetricUtils.safeGetMeter(metricRegistry,
                                                           name(input.getType(), input.getId(), "rawSize"));
            final ThroughputStats.Throughput throughput = firstNonNull(perInputThroughput.get(input.getType()),
                                                                       ThroughputStats.Throughput.create(0, 0, 0));

            perInputThroughput.put(input.getType(), ThroughputStats.Throughput.create(
                    incomingMessages.getCount() + throughput.count(),
                    0d /* not available at the moment */,
                    rawSize.getCount() + throughput.size()));
        }

        final ThroughputStats throughputStats = ThroughputStats.create(
                ThroughputStats.Throughput.create(
                        MetricUtils.safeGetCounter(metricRegistry, "org.graylog2.throughput.input").getCount(),
                        MetricUtils.safeGetDoubleGauge(metricRegistry, "org.graylog2.throughput.input.1-sec-rate").getValue(),
                        MetricUtils.safeGetCounter(metricRegistry, "org.graylog2.throughput.input.size").getCount()
                ),
                ThroughputStats.Throughput.create(
                        MetricUtils.safeGetCounter(metricRegistry, "org.graylog2.throughput.output").getCount(),
                        MetricUtils.safeGetDoubleGauge(metricRegistry, "org.graylog2.throughput.output.1-sec-rate").getValue(),
                        0L /* we cannot track this number at the moment */
                ),
                perInputThroughput

        );
        final BufferStats bufferStats = BufferStats.create(
                BufferStats.Buffer.create(
                        MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.buffers.input.size").getValue(),
                        MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.buffers.input.usage").getValue()
                ), BufferStats.Buffer.create(
                        MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.buffers.process.size").getValue(),
                        MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.buffers.process.usage").getValue()
                ), BufferStats.Buffer.create(
                        MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.buffers.output.size").getValue(),
                        MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.buffers.output.usage").getValue()
                )
        );
        final JournalStats journalStats = JournalStats.create(
                MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.journal.size").getValue(),
                MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.journal.size-limit").getValue(),
                MetricUtils.safeGetIntegerGauge(metricRegistry, "org.graylog2.journal.segments").getValue(),
                MetricUtils.safeGetLongGauge(metricRegistry, "org.graylog2.journal.entries-uncommitted").getValue(),
                MetricUtils.safeGetMeter(metricRegistry, "org.graylog2.shared.journal.KafkaJournal.readMessages").getCount(),
                MetricUtils.safeGetMeter(metricRegistry, "org.graylog2.shared.journal.KafkaJournal.writtenMessages").getCount()
        );
        final Histogram searchTimings = Histogram.fromMetricsTimer(
                MetricUtils.safeGetTimer(metricRegistry, "org.graylog2.indexer.searches.Searches.elasticsearch.requests")
        );
        final Histogram searchRanges = Histogram.fromMetricsHistogram(
                MetricUtils.safeGetHistogram(metricRegistry, "org.graylog2.indexer.searches.Searches.elasticsearch.ranges")
        );

        final SessionStats sessionStats = SessionStats.create(
                safeGetCounter(metricRegistry, name(SessionAuthenticator.class, "sessions-authenticated")).getCount(),
                safeGetCounter(metricRegistry, name(SessionAuthenticator.class, "sessions-extended")).getCount(),
                safeGetCounter(metricRegistry, name(SessionAuthenticator.class, "sessions-expired")).getCount(),
                safeGetMeter(metricRegistry, name(SessionsResource.class, "newSession")).getCount(),
                safeGetMeter(metricRegistry, name(SessionsResource.class, "terminateSession")).getCount()
        );

        return NodeStats.create(
                uptime,
                inputService.totalCountForNode(nodeId.toString()),
                throughputStats,
                bufferStats,
                journalStats,
                searchTimings,
                searchRanges,
                sessionStats
        );
    }

    private HostStats buildHostStats() {
        final OsStats osStats = statsService.osStats();
        final HostStats.Memory memory = osStats.memory() == null ? null : HostStats.Memory.create(
                osStats.memory().free(),
                osStats.memory().used()
        );
        final HostStats.Memory swap = osStats.swap() == null ? null : HostStats.Memory.create(
                osStats.swap().free(),
                osStats.swap().used()
        );

        return HostStats.create(osStats.loadAverage(), memory, swap);
    }
}
