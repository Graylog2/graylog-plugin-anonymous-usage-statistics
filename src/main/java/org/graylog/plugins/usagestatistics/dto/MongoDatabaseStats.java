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

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
public abstract class MongoDatabaseStats {
    public static MongoDatabaseStats create(long collections,
                                            long objects,
                                            double avgObjSize,
                                            long dataSize,
                                            long storageSize,
                                            long numExtents,
                                            long indexes,
                                            long indexSize,
                                            @Nullable Long fileSize,
                                            @Nullable Long nsSizeMB) {
        return new AutoValue_MongoDatabaseStats(collections, objects, avgObjSize, dataSize, storageSize, numExtents,
                indexes, indexSize, fileSize, nsSizeMB);
    }

    @JsonProperty
    public abstract long collections();

    @JsonProperty
    public abstract long objects();

    @JsonProperty
    public abstract double avgObjSize();

    @JsonProperty
    public abstract long dataSize();

    @JsonProperty
    public abstract long storageSize();

    @JsonProperty
    public abstract long numExtents();

    @JsonProperty
    public abstract long indexes();

    @JsonProperty
    public abstract long indexSize();

    @JsonProperty
    @Nullable
    public abstract Long fileSize();

    @JsonProperty
    @Nullable
    public abstract Long nsSizeMB();
}