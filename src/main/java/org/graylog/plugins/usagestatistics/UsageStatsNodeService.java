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
import org.graylog.plugins.usagestatistics.collectors.NodeCollector;
import org.graylog.plugins.usagestatistics.dto.NodeDataSet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class UsageStatsNodeService {
    private final Supplier<NodeDataSet> cachedDataSet;

    @Inject
    public UsageStatsNodeService(NodeCollector nodeCollector,
                                 @Named("usage_statistics_cache_timeout") Duration cacheTimeout) {
        this(supplier(nodeCollector), cacheTimeout);
    }

    @VisibleForTesting
    UsageStatsNodeService(Supplier<NodeDataSet> nodeDataSetSupplier, Duration cacheTimeout) {
        this.cachedDataSet = Suppliers.memoizeWithExpiration(
                nodeDataSetSupplier,
                cacheTimeout.getQuantity(),
                cacheTimeout.getUnit());
    }

    private static Supplier<NodeDataSet> supplier(final NodeCollector nodeCollector) {
        return new Supplier<NodeDataSet>() {
            @Override
            public NodeDataSet get() {
                return nodeCollector.getNodeDataSet();
            }
        };
    }

    public NodeDataSet buildNodeDataSet() {
        return cachedDataSet.get();
    }

}
