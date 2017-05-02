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

import com.github.joschi.jadconfig.util.Duration;
import com.google.common.collect.ImmutableMap;
import org.graylog.plugins.usagestatistics.UsageStatsMetaData;
import org.graylog.plugins.usagestatistics.dto.AlarmStats;
import org.graylog.plugins.usagestatistics.dto.ClusterDataSet;
import org.graylog.plugins.usagestatistics.dto.ClusterStats;
import org.graylog.plugins.usagestatistics.dto.LdapStats;
import org.graylog2.indexer.counts.Counts;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.cluster.ClusterId;
import org.graylog2.plugin.inputs.Extractor;
import org.graylog2.system.stats.ClusterStatsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ClusterCollector {
    private final ClusterStatsService clusterStatsService;
    private final ElasticsearchCollector elasticsearchCollector;
    private final MongoCollector mongoCollector;
    private final CollectorCollector collectorCollector;
    private final Counts counts;
    private final ClusterConfigService clusterConfigService;
    private final long reportIntervalMs;

    @Inject
    public ClusterCollector(ClusterStatsService clusterStatsService,
                            ElasticsearchCollector elasticsearchCollector,
                            MongoCollector mongoCollector,
                            CollectorCollector collectorCollector,
                            Counts counts,
                            @Named("usage_statistics_report_interval") Duration reportInterval,
                            ClusterConfigService clusterConfigService) {
        this.clusterStatsService = checkNotNull(clusterStatsService);
        this.elasticsearchCollector = checkNotNull(elasticsearchCollector);
        this.mongoCollector = checkNotNull(mongoCollector);
        this.collectorCollector = checkNotNull(collectorCollector);
        this.counts = checkNotNull(counts);
        this.reportIntervalMs = checkNotNull(reportInterval).toMilliseconds();
        this.clusterConfigService = checkNotNull(clusterConfigService);
    }

    public ClusterDataSet getClusterDataSet() {
        final ClusterId clusterId = clusterConfigService.getOrDefault(ClusterId.class, ClusterId.create(""));

        return ClusterDataSet.create(
                String.valueOf(UsageStatsMetaData.VERSION),
                clusterId.clusterId(),
                System.currentTimeMillis(),
                reportIntervalMs,
                buildClusterStats()
        );
    }

    private ClusterStats buildClusterStats() {
        final org.graylog2.system.stats.ClusterStats clusterStats = clusterStatsService.clusterStats();

        return ClusterStats.create(
                elasticsearchCollector.getClusterStats(),
                elasticsearchCollector.getNodeInfos(),
                mongoCollector.getMongoStats(),
                collectorCollector.getCollectorInfos(),
                clusterStats.streamCount(),
                clusterStats.streamRuleCount(),
                clusterStats.streamRuleCountByStream(),
                clusterStats.userCount(),
                clusterStats.outputCount(),
                clusterStats.outputCountByType(),
                clusterStats.dashboardCount(),
                clusterStats.inputCount(),
                clusterStats.globalInputCount(),
                clusterStats.inputCountByType(),
                clusterStats.extractorCount(),
                buildExtractorCountByType(),
                clusterStats.contentPackCount(),
                counts.total(),
                buildStreamThroughput(),
                buildLdapStats(),
                buildAlarmStats()
        );
    }

    private Map<String, Long> buildStreamThroughput() {
        return Collections.emptyMap();
    }

    private Map<String, Long> buildExtractorCountByType() {
        final ImmutableMap.Builder<String, Long> builder = ImmutableMap.builder();
        for (Map.Entry<Extractor.Type, Long> entry : clusterStatsService.clusterStats().extractorCountByType().entrySet()) {
            builder.put(entry.getKey().name(), entry.getValue());
        }
        return builder.build();
    }

    private LdapStats buildLdapStats() {
        final org.graylog2.system.stats.LdapStats ldapStats = clusterStatsService.ldapStats();
        return LdapStats.create(ldapStats.enabled(),
                                ldapStats.activeDirectory(),
                                ldapStats.roleMappingCount(),
                                ldapStats.roleCount());
    }

    private AlarmStats buildAlarmStats() {
        final org.graylog2.system.stats.AlarmStats stats = clusterStatsService.alarmStats();
        return AlarmStats.create(stats.alertCount(), stats.alarmcallbackCountByType());
    }
}
