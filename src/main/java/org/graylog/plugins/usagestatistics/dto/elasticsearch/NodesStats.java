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
package org.graylog.plugins.usagestatistics.dto.elasticsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class NodesStats {
    public static NodesStats create(int total,
                                    int masterOnly,
                                    int dataOnly,
                                    int masterData,
                                    int client) {
        return new AutoValue_NodesStats(total, masterOnly, dataOnly, masterData, client);
    }

    public static NodesStats fromNodesStats(org.graylog2.system.stats.elasticsearch.NodesStats nodesStats) {
        return create(
                nodesStats.total(),
                nodesStats.masterOnly(),
                nodesStats.dataOnly(),
                nodesStats.masterData(),
                nodesStats.client()
        );
    }

    @JsonProperty
    public abstract int total();

    @JsonProperty
    public abstract int masterOnly();

    @JsonProperty
    public abstract int dataOnly();

    @JsonProperty
    public abstract int masterData();

    @JsonProperty
    public abstract int client();
}
