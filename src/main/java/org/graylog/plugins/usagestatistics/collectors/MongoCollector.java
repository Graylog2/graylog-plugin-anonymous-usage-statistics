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
package org.graylog.plugins.usagestatistics.collectors;

import org.graylog.plugins.usagestatistics.dto.MongoDatabaseStats;
import org.graylog.plugins.usagestatistics.dto.MongoStats;
import org.graylog2.system.stats.ClusterStatsService;
import org.graylog2.system.stats.mongo.DatabaseStats;
import org.graylog2.system.stats.mongo.HostInfo;

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

        final HostInfo hostInfo = stats.hostInfo();
        return MongoStats.create(
                stats.buildInfo().version(),
                stats.servers().size(),
                hostInfo == null || hostInfo.system() == null ? "unknown" : hostInfo.system().cpuArch(),
                mongoDatabaseStats
        );
    }
}
