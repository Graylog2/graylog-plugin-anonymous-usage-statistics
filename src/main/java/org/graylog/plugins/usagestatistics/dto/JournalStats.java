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

@JsonAutoDetect
@AutoValue
public abstract class JournalStats {
    public static JournalStats create(long size,
                                      long sizeLimit,
                                      long segments,
                                      long uncommittedEntries,
                                      long readMessages,
                                      long writtenMessages) {
        return new AutoValue_JournalStats(size, sizeLimit, segments, uncommittedEntries, readMessages, writtenMessages);
    }

    @JsonProperty
    public abstract long size();

    @JsonProperty
    public abstract long sizeLimit();

    @JsonProperty
    public abstract long segments();

    @JsonProperty
    public abstract long uncommittedEntries();

    @JsonProperty
    public abstract long readMessages();

    @JsonProperty
    public abstract long writtenMessages();
}
