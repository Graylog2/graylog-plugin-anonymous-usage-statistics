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
public abstract class NodeStats {
    public static NodeStats create(long uptime,
                                   long inputCount,
                                   ThroughputStats throughputStats,
                                   BufferStats bufferStats,
                                   JournalStats journalStats,
                                   Histogram searchTimings,
                                   Histogram searchRanges,
                                   SearchStats searchStats,
                                   SessionStats sessionStats) {
        return new AutoValue_NodeStats(uptime,
                                       inputCount,
                                       throughputStats,
                                       bufferStats,
                                       journalStats,
                                       searchTimings,
                                       searchRanges,
                                       searchStats,
                                       sessionStats);
    }

    @JsonProperty
    public abstract long uptime();

    @JsonProperty
    public abstract long inputCount();

    @JsonProperty
    public abstract ThroughputStats throughput();

    @JsonProperty
    public abstract BufferStats buffers();

    @JsonProperty
    public abstract JournalStats journal();

    @JsonProperty
    public abstract Histogram searchTimings();

    @JsonProperty
    public abstract Histogram searchRanges();

    @JsonProperty
    public abstract SearchStats searchStats();

    @JsonProperty
    public abstract SessionStats sessionStats();
}
