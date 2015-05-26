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
import com.squareup.okhttp.OkHttpClient;
import org.graylog.plugins.usagestatistics.providers.SmileObjectMapperProvider;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.plugin.cluster.ClusterId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsageStatsClusterPeriodicalTest {
    @Mock
    private UsageStatsClusterService clusterService;
    @Mock
    private ServerStatus serverStatus;
    private UsageStatsConfiguration configuration;
    @Mock
    private ClusterConfigService clusterConfigService;
    @Mock
    private OkHttpClient httpClient;
    private ObjectMapper objectMapper;
    private UsageStatsClusterPeriodical periodical;

    @Before
    public void setUp() throws Exception {
        configuration = new UsageStatsConfiguration();
        when(clusterConfigService.get(ClusterId.class)).thenReturn(ClusterId.create("test-cluster-id"));
        objectMapper = new SmileObjectMapperProvider().get();
        periodical = new UsageStatsClusterPeriodical(
                clusterService,
                serverStatus,
                configuration,
                clusterConfigService,
                httpClient,
                objectMapper
        );
    }

    @Test
    public void testGetUrl() {
        assertThat(periodical.getUrl().toString())
                .isEqualTo("https://stats-collector.graylog.com/submit/cluster/test-cluster-id");
    }
}