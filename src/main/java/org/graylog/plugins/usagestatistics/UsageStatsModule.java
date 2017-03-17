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

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.graylog.plugins.usagestatistics.audit.UsageStatsAuditEventTypes;
import org.graylog.plugins.usagestatistics.collectors.ClusterCollector;
import org.graylog.plugins.usagestatistics.collectors.CollectorCollector;
import org.graylog.plugins.usagestatistics.collectors.ElasticsearchCollector;
import org.graylog.plugins.usagestatistics.collectors.MongoCollector;
import org.graylog.plugins.usagestatistics.collectors.NodeCollector;
import org.graylog.plugins.usagestatistics.providers.CompressingHttpClient;
import org.graylog.plugins.usagestatistics.providers.CompressingOkHttpClientProvider;
import org.graylog.plugins.usagestatistics.providers.SmileObjectMapper;
import org.graylog.plugins.usagestatistics.providers.SmileObjectMapperProvider;
import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

import java.util.Collections;
import java.util.Set;

public class UsageStatsModule extends PluginModule {
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.singleton(new UsageStatsConfiguration());
    }

    @Override
    protected void configure() {
        bind(ObjectMapper.class).annotatedWith(SmileObjectMapper.class).toProvider(SmileObjectMapperProvider.class);
        bind(OkHttpClient.class).annotatedWith(CompressingHttpClient.class).toProvider(CompressingOkHttpClientProvider.class);

        bind(NodeCollector.class).asEagerSingleton();
        bind(UsageStatsNodeService.class).asEagerSingleton();

        bind(ElasticsearchCollector.class).asEagerSingleton();
        bind(MongoCollector.class).asEagerSingleton();
        bind(CollectorCollector.class).asEagerSingleton();
        bind(ClusterCollector.class).asEagerSingleton();
        bind(UsageStatsClusterService.class).asEagerSingleton();

        addPeriodical(UsageStatsNodePeriodical.class);
        addPeriodical(UsageStatsClusterPeriodical.class);
        addRestResource(UsageStatsResource.class);
        addRestResource(UsageStatsOptOutResource.class);

        addConfigBeans();

        addAuditEventTypes(UsageStatsAuditEventTypes.class);
    }
}
