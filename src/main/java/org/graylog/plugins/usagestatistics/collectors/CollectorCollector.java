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
package org.graylog.plugins.usagestatistics.collectors;

import org.graylog.plugins.usagestatistics.dto.CollectorInfo;

import java.util.Collections;
import java.util.Set;

// This is just a dummy because the collector classes have been removed from Graylog core.
// If this gets removed, the usage-stats collector needs an update!
public class CollectorCollector {
    public Set<CollectorInfo> getCollectorInfos() {
        return Collections.emptySet();
    }
}
