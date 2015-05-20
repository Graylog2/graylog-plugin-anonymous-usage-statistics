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

@JsonAutoDetect
@AutoValue
public abstract class NodeStats {
    public static NodeStats create(long uptime,
                                   long inputCount,
                                   ThroughputStats throughputStats,
                                   BufferStats bufferStats,
                                   JournalStats journalStats,
                                   Histogram searchTimings,
                                   Histogram searchRanges) {
        return new AutoValue_NodeStats(uptime, inputCount, throughputStats, bufferStats, journalStats, searchTimings, searchRanges);
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
}
