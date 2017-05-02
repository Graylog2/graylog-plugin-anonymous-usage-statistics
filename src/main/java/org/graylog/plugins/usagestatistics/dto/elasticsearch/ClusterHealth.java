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
