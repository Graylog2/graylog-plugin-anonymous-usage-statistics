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

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
public abstract class HostStats {
    public static HostStats create(double[] loadAvg, @Nullable Memory memory, @Nullable Memory swap) {
        return new AutoValue_HostStats(loadAvg, memory, swap);
    }

    @JsonProperty
    @SuppressWarnings("mutable")
    public abstract double[] loadAvg();

    @JsonProperty
    @Nullable
    public abstract Memory memory();

    @JsonProperty
    @Nullable
    public abstract Memory swap();

    @JsonAutoDetect
    @AutoValue
    public static abstract class Memory {
        public static Memory create(long free, long used) {
            return new AutoValue_HostStats_Memory(free, used);
        }

        @JsonProperty
        public abstract long free();

        @JsonProperty
        public abstract long used();
    }
}
