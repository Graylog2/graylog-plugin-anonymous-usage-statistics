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
package org.graylog.plugins.usagestatistics;

import com.github.joschi.jadconfig.util.Duration;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.graylog.plugins.usagestatistics.collectors.ClusterCollector;
import org.graylog.plugins.usagestatistics.dto.ClusterDataSet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class UsageStatsClusterService {
    private final Supplier<ClusterDataSet> cachedDataSet;

    @Inject
    public UsageStatsClusterService(ClusterCollector clusterCollector,
                                    @Named("usage_statistics_cache_timeout") Duration cacheTimeout) {
        this(supplier(clusterCollector), cacheTimeout);
    }

    @VisibleForTesting
    UsageStatsClusterService(Supplier<ClusterDataSet> clusterDataSetSupplier, Duration cacheTimeout) {
        this.cachedDataSet = Suppliers.memoizeWithExpiration(
                clusterDataSetSupplier,
                cacheTimeout.getQuantity(),
                cacheTimeout.getUnit()
        );
    }

    private static Supplier<ClusterDataSet> supplier(final ClusterCollector clusterCollector) {
        return new Supplier<ClusterDataSet>() {
            @Override
            public ClusterDataSet get() {
                return clusterCollector.getClusterDataSet();
            }
        };
    }

    public ClusterDataSet buildClusterDataSet() {
        return cachedDataSet.get();
    }

}
