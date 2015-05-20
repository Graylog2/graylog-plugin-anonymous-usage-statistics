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
public abstract class BufferStats {
    public static BufferStats create(Buffer input, Buffer process, Buffer output) {
        return new AutoValue_BufferStats(input, process, output);
    }

    @JsonProperty
    public abstract Buffer input();

    @JsonProperty
    public abstract Buffer process();

    @JsonProperty
    public abstract Buffer output();

    @JsonAutoDetect
    @AutoValue
    public static abstract class Buffer {
        public static Buffer create(long size, long usage) {
            return new AutoValue_BufferStats_Buffer(size, usage);
        }

        @JsonProperty
        public abstract long size();

        @JsonProperty
        public abstract long usage();
    }
}
