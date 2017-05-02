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

@JsonAutoDetect
@AutoValue
public abstract class NodeDataSet {
    public static NodeDataSet create(String version,
                                     String clusterId,
                                     String nodeId,
                                     long timestamp,
                                     long reportIntervalMs,
                                     NodeInfo nodeInfo,
                                     NodeStats nodeStats,
                                     HostInfo hostInfo,
                                     HostStats hostStats,
                                     JvmInfo jvmInfo) {
        return new AutoValue_NodeDataSet(
                version, clusterId, nodeId, timestamp, reportIntervalMs,
                nodeInfo, nodeStats, hostInfo, hostStats, jvmInfo);
    }

    @JsonProperty
    public abstract String version();

    @JsonProperty
    public abstract String clusterId();

    @JsonProperty
    public abstract String nodeId();

    @JsonProperty
    public abstract long timestamp();

    @JsonProperty
    public abstract long reportIntervalMs();

    @JsonProperty
    public abstract NodeInfo nodeInfo();

    @JsonProperty
    public abstract NodeStats nodeStats();

    @JsonProperty
    public abstract HostInfo hostInfo();

    @JsonProperty
    public abstract HostStats hostStats();

    @JsonProperty
    public abstract JvmInfo jvm();
}
