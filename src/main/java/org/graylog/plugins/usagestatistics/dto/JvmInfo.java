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

import java.util.List;

@JsonAutoDetect
@AutoValue
public abstract class JvmInfo {
    public static JvmInfo create(String version,
                                 String vmName,
                                 String vmVersion,
                                 String vmVendor,
                                 Os os,
                                 JvmInfo.Memory mem,
                                 List<String> garbageCollectors) {
        return new AutoValue_JvmInfo(version, vmName, vmVersion, vmVendor, os, mem, garbageCollectors);
    }

    @JsonProperty
    public abstract String version();

    @JsonProperty
    public abstract String vmName();

    @JsonProperty
    public abstract String vmVersion();

    @JsonProperty
    public abstract String vmVendor();

    @JsonProperty
    public abstract Os os();

    @JsonProperty
    public abstract Memory mem();

    @JsonProperty
    public abstract List<String> garbageCollectors();

    @JsonAutoDetect
    @AutoValue
    public abstract static class Os {
        public static Os create(String name,
                                String version,
                                String arch) {
            return new AutoValue_JvmInfo_Os(name, version, arch);
        }

        @JsonProperty
        public abstract String name();

        @JsonProperty
        public abstract String version();

        @JsonProperty
        public abstract String arch();
    }

    @JsonAutoDetect
    @AutoValue
    public abstract static class Memory {
        public static Memory create(long heapInit,
                                    long heapMax,
                                    long nonHeapInit,
                                    long nonHeapMax,
                                    long directMemoryMax) {
            return new AutoValue_JvmInfo_Memory(heapInit, heapMax, nonHeapInit, nonHeapMax, directMemoryMax);
        }

        @JsonProperty
        public abstract long heapInit();

        @JsonProperty
        public abstract long heapMax();

        @JsonProperty
        public abstract long nonHeapInit();

        @JsonProperty
        public abstract long nonHeapMax();

        @JsonProperty
        public abstract long directMemoryMax();
    }
}
