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
public abstract class HostInfo {
    public static HostInfo create(MacAddress macAddress,
                                  @Nullable Cpu cpu,
                                  @Nullable Memory memory,
                                  @Nullable Memory swap) {
        return new AutoValue_HostInfo(macAddress, cpu, memory, swap);
    }

    @JsonProperty
    public abstract MacAddress macAddress();

    @JsonProperty
    @Nullable
    public abstract Cpu cpu();

    @JsonProperty
    @Nullable
    public abstract Memory memory();

    @JsonProperty
    @Nullable
    public abstract Memory swap();

    @JsonAutoDetect
    @AutoValue
    public static abstract class Cpu {
        public static Cpu create(String model,
                                 String vendor,
                                 int mhz,
                                 int totalCores,
                                 int totalSockets,
                                 int coresPerSocket,
                                 long cacheSize) {
            return new AutoValue_HostInfo_Cpu(model, vendor, mhz, totalCores, totalSockets, coresPerSocket, cacheSize);
        }

        @JsonProperty
        public abstract String model();

        @JsonProperty
        public abstract String vendor();

        @JsonProperty
        public abstract int mhz();

        @JsonProperty
        public abstract int totalCores();

        @JsonProperty
        public abstract int totalSockets();

        @JsonProperty
        public abstract int coresPerSocket();

        @JsonProperty
        public abstract long cacheSize();
    }

    @JsonAutoDetect
    @AutoValue
    public static abstract class Memory {
        public static Memory create(long total) {
            return new AutoValue_HostInfo_Memory(total);
        }

        @JsonProperty
        public abstract long total();
    }
}
