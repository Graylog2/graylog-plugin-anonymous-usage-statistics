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
package org.graylog.plugins.usagestatistics.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class ClusterHealth {
    public static ClusterHealth create(int numberOfNodes,
                                       int numberOfDataNodes,
                                       int activeShards,
                                       int relocatingShards,
                                       int activePrimaryShards,
                                       int initializingShards,
                                       int unassignedShards,
                                       int pendingTasks) {
        return new AutoValue_ClusterHealth(numberOfNodes, numberOfDataNodes, activeShards, relocatingShards,
                activePrimaryShards, initializingShards, unassignedShards, pendingTasks);
    }

    public static ClusterHealth fromClusterHealth(org.graylog2.system.stats.elasticsearch.ClusterHealth clusterHealth) {
        return create(
                clusterHealth.numberOfNodes(),
                clusterHealth.numberOfDataNodes(),
                clusterHealth.activeShards(),
                clusterHealth.relocatingShards(),
                clusterHealth.activePrimaryShards(),
                clusterHealth.initializingShards(),
                clusterHealth.unassignedShards(),
                clusterHealth.pendingTasks()
        );
    }

    @JsonProperty
    public abstract int numberOfNodes();

    @JsonProperty
    public abstract int numberOfDataNodes();

    @JsonProperty
    public abstract int activeShards();

    @JsonProperty
    public abstract int relocatingShards();

    @JsonProperty
    public abstract int activePrimaryShards();

    @JsonProperty
    public abstract int initializingShards();

    @JsonProperty
    public abstract int unassignedShards();

    @JsonProperty
    public abstract int pendingTasks();
}
