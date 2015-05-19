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

import com.google.common.collect.ImmutableSet;
import org.graylog.plugins.usagestatistics.dto.CollectorInfo;
import org.graylog2.collectors.Collector;
import org.graylog2.collectors.CollectorService;

import javax.inject.Inject;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class CollectorCollector {
    private final CollectorService collectorService;

    @Inject
    public CollectorCollector(CollectorService collectorService) {
        this.collectorService = checkNotNull(collectorService);
    }

    public Set<CollectorInfo> getCollectorInfos() {
        final ImmutableSet.Builder<CollectorInfo> collectorInfos = ImmutableSet.builder();
        for (Collector collector : collectorService.all()) {
            collectorInfos.add(CollectorInfo.create(collector.getCollectorVersion(), collector.getNodeDetails().operatingSystem()));
        }

        return collectorInfos.build();
    }
}
