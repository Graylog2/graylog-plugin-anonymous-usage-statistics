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
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

@JsonAutoDetect
@AutoValue
public abstract class MacAddress {
    public static final MacAddress EMPTY = new AutoValue_MacAddress("");
    private static final Pattern PATTERN = Pattern.compile("[0-9a-f]{2}([:-][0-9a-f]{2}){5}", Pattern.CASE_INSENSITIVE);

    public static MacAddress create(String macAddress) {
        if(macAddress == null || macAddress.isEmpty()) {
            return EMPTY;
        }
        checkArgument(PATTERN.matcher(macAddress).matches());

        return new AutoValue_MacAddress(macAddress.substring(0, 8));
    }

    @JsonValue
    public abstract String macAddress();
}
