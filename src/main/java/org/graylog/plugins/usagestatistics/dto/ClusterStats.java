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
                                      Map<String, Long> streamThroughput
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
                streamThroughput);
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
}