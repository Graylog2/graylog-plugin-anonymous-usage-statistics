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
public abstract class ElasticsearchClusterStats {
    public static ElasticsearchClusterStats create(ClusterHealth clusterHealth,
                                                   NodesStats nodesStats,
                                                   IndicesStats indicesStats) {
        return new AutoValue_ElasticsearchClusterStats(clusterHealth, nodesStats, indicesStats);
    }

    @JsonProperty
    public abstract ClusterHealth clusterHealth();

    @JsonProperty
    public abstract NodesStats nodesStats();

    @JsonProperty
    public abstract IndicesStats indicesStats();
}
