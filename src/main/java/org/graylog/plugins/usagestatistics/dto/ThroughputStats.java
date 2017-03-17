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

import java.util.Map;

@JsonAutoDetect
@AutoValue
public abstract class ThroughputStats {
    public static ThroughputStats create(Throughput input, Throughput output, Map<String, Throughput> perInputType) {
        return new AutoValue_ThroughputStats(input, output, perInputType);
    }

    @JsonProperty
    public abstract Throughput input();

    @JsonProperty
    public abstract Throughput output();

    @JsonProperty
    public abstract Map<String, Throughput> perInputType();

    @JsonAutoDetect
    @AutoValue
    public static abstract class Throughput {
        public static Throughput create(long count, double lastSecond, long size) {
            return new AutoValue_ThroughputStats_Throughput(count, lastSecond, size);
        }

        @JsonProperty
        public abstract long count();

        @JsonProperty
        public abstract double lastSecond();

        @JsonProperty
        public abstract long size();
    }
}
