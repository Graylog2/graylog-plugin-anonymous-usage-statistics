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
