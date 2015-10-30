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
import org.graylog2.plugin.cluster.ClusterId;
import org.graylog2.plugin.system.NodeId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsageStatsNodePeriodicalTest {
    @Mock
    private UsageStatsNodeService nodeService;
    @Mock
    private NodeId nodeId;
    @Mock
    private ServerStatus serverStatus;
    private UsageStatsConfiguration configuration;
    @Mock
    private OkHttpClient httpClient;

    private TestClusterConfigService clusterConfigService;
    private ObjectMapper objectMapper;
    private UsageStatsNodePeriodical periodical;

    @Before
    public void setUp() throws Exception {
        when(nodeId.anonymize()).thenReturn("test-node-id");
        configuration = spy(new UsageStatsConfiguration());

        clusterConfigService = new TestClusterConfigService();
        objectMapper = new SmileObjectMapperProvider().get();
        periodical = new UsageStatsNodePeriodical(
                nodeService,
                nodeId,
                serverStatus,
                configuration,
                clusterConfigService,
                httpClient,
                objectMapper
        );

        clusterConfigService.write(ClusterId.create("test-cluster-id"));
    }

    @Test
    public void testGetUrl() {
        assertThat(periodical.getUrl().toString())
                .isEqualTo("https://stats-collector.graylog.com/submit/cluster/test-cluster-id/node/test-node-id");
    }

    @Test
    public void testStartOnThisNode() throws Exception {
        when(configuration.isEnabled()).thenReturn(true);
        when(serverStatus.hasCapability(ServerStatus.Capability.LOCALMODE)).thenReturn(false);
        assertThat(periodical.startOnThisNode()).isTrue();

        when(configuration.isEnabled()).thenReturn(false);
        when(serverStatus.hasCapability(ServerStatus.Capability.LOCALMODE)).thenReturn(false);
        assertThat(periodical.startOnThisNode()).isFalse();

        when(configuration.isEnabled()).thenReturn(true);
        when(serverStatus.hasCapability(ServerStatus.Capability.LOCALMODE)).thenReturn(true);
        assertThat(periodical.startOnThisNode()).isFalse();

        when(configuration.isEnabled()).thenReturn(false);
        when(serverStatus.hasCapability(ServerStatus.Capability.LOCALMODE)).thenReturn(true);
        assertThat(periodical.startOnThisNode()).isFalse();
    }

    @Test
    public void testIsEnabled() throws Exception {
        when(configuration.isEnabled()).thenReturn(true);
        clusterConfigService.remove(AutoValue_UsageStatsOptOutState.class);
        assertThat(periodical.isEnabled()).isTrue();

        when(configuration.isEnabled()).thenReturn(true);
        clusterConfigService.write(UsageStatsOptOutState.create(false));
        assertThat(periodical.isEnabled()).isTrue();

        when(configuration.isEnabled()).thenReturn(false);
        clusterConfigService.remove(AutoValue_UsageStatsOptOutState.class);
        assertThat(periodical.isEnabled()).isFalse();

        when(configuration.isEnabled()).thenReturn(false);
        clusterConfigService.write(UsageStatsOptOutState.create(false));
        assertThat(periodical.isEnabled()).isFalse();

        when(configuration.isEnabled()).thenReturn(true);
        clusterConfigService.write(UsageStatsOptOutState.create(true));
        assertThat(periodical.isEnabled()).isFalse();

        when(configuration.isEnabled()).thenReturn(false);
        clusterConfigService.write(UsageStatsOptOutState.create(true));
        assertThat(periodical.isEnabled()).isFalse();
    }
}