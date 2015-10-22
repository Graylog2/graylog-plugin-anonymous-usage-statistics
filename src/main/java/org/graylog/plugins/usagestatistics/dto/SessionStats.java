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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class SessionStats {
    @JsonProperty
    public abstract long authenticated();

    @JsonProperty
    public abstract long extended();

    @JsonProperty
    public abstract long expired();

    @JsonProperty
    public abstract long logins();

    @JsonProperty
    public abstract long logouts();

    @JsonCreator
    public static SessionStats create(long authenticated,
                                      long extended,
                                      long expired,
                                      long logins,
                                      long logouts) {
        return new AutoValue_SessionStats(authenticated,
                                          extended,
                                          expired,
                                          logins,
                                          logouts);
    }
}
