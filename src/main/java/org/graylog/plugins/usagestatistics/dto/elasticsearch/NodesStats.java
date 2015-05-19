/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.usagestatistics.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class NodesStats {
    public static NodesStats create(int total,
                                    int masterOnly,
                                    int dataOnly,
                                    int masterData,
                                    int client) {
        return new AutoValue_NodesStats(total, masterOnly, dataOnly, masterData, client);
    }

    public static NodesStats fromNodesStats(org.graylog2.system.stats.elasticsearch.NodesStats nodesStats) {
        return create(
                nodesStats.total(),
                nodesStats.masterOnly(),
                nodesStats.dataOnly(),
                nodesStats.masterData(),
                nodesStats.client()
        );
    }

    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract int masterOnly();

    @JsonProperty
    public abstract int dataOnly();

    @JsonProperty
    public abstract int masterData();

    @JsonProperty
    public abstract int client();
}
