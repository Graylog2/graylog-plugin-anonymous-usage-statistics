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
import okhttp3.Request;
import okhttp3.mockwebserver.MockWebServer;
import org.graylog.plugins.usagestatistics.providers.SmileObjectMapperProvider;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.cluster.ClusterId;
import org.graylog2.plugin.system.NodeId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsageStatsNodePeriodicalTest {
    private final MockWebServer mockWebServer = new MockWebServer();
    @Mock
    private UsageStatsNodeService nodeService;
    @Mock
    private NodeId nodeId;
    @Mock
    private ServerStatus serverStatus;
    private UsageStatsConfiguration configuration;
    private OkHttpClient httpClient;

    private TestClusterConfigService clusterConfigService;
    private ObjectMapper objectMapper;
    private UsageStatsNodePeriodical periodical;

    @Before
    public void setUp() throws Exception {
        mockWebServer.start();

        when(nodeId.anonymize()).thenReturn("test-node-id");
        configuration = spy(new UsageStatsConfiguration());

        clusterConfigService = new TestClusterConfigService();
        objectMapper = new SmileObjectMapperProvider().get();
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    final Request request = chain.request().newBuilder()
                            .url(mockWebServer.url("/cluster/test-cluster-id/node/test-node-id"))
                            .build();
                    return chain.proceed(request);
                }).build();
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

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
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
