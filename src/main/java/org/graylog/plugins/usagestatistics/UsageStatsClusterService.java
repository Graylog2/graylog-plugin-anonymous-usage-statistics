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
