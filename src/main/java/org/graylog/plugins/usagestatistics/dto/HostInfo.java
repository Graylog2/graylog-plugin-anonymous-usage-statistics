/**
 * Copyright (C) 2015 Graylog, Inc. (hello@graylog.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
public abstract class HostInfo {
    public static HostInfo create(MacAddress macAddress, Cpu cpu, Memory memory, Memory swap) {
        return new AutoValue_HostInfo(macAddress, cpu, memory, swap);
    }

    @JsonProperty
    public abstract MacAddress macAddress();

    @JsonProperty
    public abstract Cpu cpu();

    @JsonProperty
    public abstract Memory memory();

    @JsonProperty
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
