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
package org.graylog.plugins.usagestatistics.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.plugins.usagestatistics.dto.elasticsearch.ElasticsearchClusterStats;
import org.graylog.plugins.usagestatistics.dto.elasticsearch.ElasticsearchNodeInfo;

import java.util.Map;
import java.util.Set;

@JsonAutoDetect
@AutoValue
public abstract class ClusterStats {
    public static ClusterStats create(ElasticsearchClusterStats elasticsearchCluster,
                                      Set<ElasticsearchNodeInfo> elasticsearchNodes,
                                      MongoStats mongo,
                                      Set<CollectorInfo> collectors,
                                      long streamCount,
                                      long streamRuleCount,
                                      Map<String, Long> streamRuleCountByStream,
                                      long userCount,
                                      long outputCount,
                                      Map<String, Long> outputCountByType,
                                      long dashboardCount,
                                      long inputCount,
                                      long globalInputCount,
                                      Map<String, Long> inputCountByType,
                                      long extractorCount,
                                      Map<String, Long> extractorCountByType,
                                      long contentPackCount,
                                      long totalMessages,
                                      Map<String, Long> streamThroughput,
                                      LdapStats ldapStats,
                                      AlarmStats alarmStats
    ) {
        return new AutoValue_ClusterStats(
                elasticsearchCluster,
                elasticsearchNodes,
                mongo,
                collectors,
                streamCount,
                streamRuleCount,
                streamRuleCountByStream,
                userCount,
                outputCount,
                outputCountByType,
                dashboardCount,
                inputCount,
                globalInputCount,
                inputCountByType,
                extractorCount,
                extractorCountByType,
                contentPackCount,
                totalMessages,
                streamThroughput,
                ldapStats,
                alarmStats);
    }

    @JsonProperty
    public abstract ElasticsearchClusterStats elasticsearchCluster();

    @JsonProperty
    public abstract Set<ElasticsearchNodeInfo> elasticsearchNodes();

    @JsonProperty
    public abstract MongoStats mongo();

    @JsonProperty
    public abstract Set<CollectorInfo> collectors();

    @JsonProperty
    public abstract long streamCount();

    @JsonProperty
    public abstract long streamRuleCount();

    @JsonProperty
    public abstract Map<String, Long> streamRuleCountByStream();

    @JsonProperty
    public abstract long userCount();

    @JsonProperty
    public abstract long outputCount();

    @JsonProperty
    public abstract Map<String, Long> outputCountByType();

    @JsonProperty
    public abstract long dashboardCount();

    @JsonProperty
    public abstract long inputCount();

    @JsonProperty
    public abstract long globalInputCount();

    @JsonProperty
    public abstract Map<String, Long> inputCountByType();

    @JsonProperty
    public abstract long extractorCount();

    @JsonProperty
    public abstract Map<String, Long> extractorCountByType();

    @JsonProperty
    public abstract long contentPackCount();

    @JsonProperty
    public abstract long totalMessages();

    @JsonProperty
    public abstract Map<String, Long> streamThroughput();

    @JsonProperty
    public abstract LdapStats ldapStats();

    @JsonProperty
    public abstract AlarmStats alarmStats();
}
