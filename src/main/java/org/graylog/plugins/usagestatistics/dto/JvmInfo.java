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