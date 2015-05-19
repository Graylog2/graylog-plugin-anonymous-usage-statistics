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
        checkArgument(PATTERN.matcher(macAddress).matches());

        return new AutoValue_MacAddress(macAddress.substring(0, 8));
    }

    @JsonValue
    public abstract String macAddress();
}
