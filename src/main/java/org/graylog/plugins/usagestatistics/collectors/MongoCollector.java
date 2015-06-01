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

import org.graylog.plugins.usagestatistics.dto.MongoDatabaseStats;
import org.graylog.plugins.usagestatistics.dto.MongoStats;
import org.graylog2.system.stats.ClusterStatsService;
import org.graylog2.system.stats.mongo.DatabaseStats;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class MongoCollector {
    private final ClusterStatsService clusterStatsService;

    @Inject
    public MongoCollector(ClusterStatsService clusterStatsService) {
        this.clusterStatsService = checkNotNull(clusterStatsService);
    }

    public MongoStats getMongoStats() {
        final org.graylog2.system.stats.mongo.MongoStats stats = clusterStatsService.mongoStats();
        final DatabaseStats databaseStats = stats.databaseStats();

        final MongoDatabaseStats mongoDatabaseStats;
        if (databaseStats != null) {
            mongoDatabaseStats = MongoDatabaseStats.create(
                    databaseStats.collections(),
                    databaseStats.objects(),
                    databaseStats.avgObjSize(),
                    databaseStats.dataSize(),
                    databaseStats.storageSize(),
                    databaseStats.numExtents(),
                    databaseStats.indexes(),
                    databaseStats.indexSize(),
                    databaseStats.fileSize(),
                    databaseStats.nsSizeMB()
            );
        } else {
            mongoDatabaseStats = null;
        }

        return MongoStats.create(
                stats.buildInfo().version(),
                stats.servers().size(),
                stats.hostInfo() == null ? "unknown" : stats.hostInfo().system().cpuArch(),
                mongoDatabaseStats
        );
    }
}
