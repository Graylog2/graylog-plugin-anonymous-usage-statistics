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
