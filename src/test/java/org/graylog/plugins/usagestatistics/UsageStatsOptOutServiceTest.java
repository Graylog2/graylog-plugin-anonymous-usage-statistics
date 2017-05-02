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
import com.github.joschi.jadconfig.util.Duration;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.MoreExecutors;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.graylog.plugins.usagestatistics.providers.SmileObjectMapperProvider;
import org.graylog2.plugin.cluster.ClusterId;
import org.graylog2.shared.bindings.providers.OkHttpClientProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.HttpHeaders;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.graylog.plugins.usagestatistics.UsageStatsConstants.USAGE_STATS_VERSION;
import static org.graylog.plugins.usagestatistics.UsageStatsConstants.USER_AGENT;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UsageStatsOptOutServiceTest {
    public static final MockResponse OPT_OUT_RESPONSE = new MockResponse().setResponseCode(202);

    private TestClusterConfigService clusterConfigService;
    private UsageStatsConfiguration configSpy = spy(new UsageStatsConfiguration());
    private ObjectMapper objectMapper = new SmileObjectMapperProvider().get();
    private UsageStatsOptOutService optOutService;
    private MockWebServer webserver = new MockWebServer();
    private final OkHttpClientProvider clientProvider = new OkHttpClientProvider(
            Duration.seconds(1L),
            Duration.seconds(1L),
            Duration.seconds(1L),
            null);

    @Before
    public void setUp() throws Exception {
        // Use a direct executor for the Dispatcher to avoid async calls during tests.
        final OkHttpClient httpClient = clientProvider.get().newBuilder()
                .dispatcher(new Dispatcher(MoreExecutors.newDirectExecutorService()))
                .build();

        webserver.start();

        when(configSpy.getUrl()).thenReturn(webserver.url("/submit/").uri());

        this.clusterConfigService = new TestClusterConfigService();
        this.optOutService = new UsageStatsOptOutService(clusterConfigService,
                configSpy, httpClient, objectMapper);

        clusterConfigService.write(ClusterId.create("test-cluster-id"));
    }

    @After
    public void tearDown() throws Exception {
        webserver.shutdown();
    }

    @Test
    public void testGetOptOutState() throws Exception {
        clusterConfigService.clear();
        assertThat(optOutService.getOptOutState()).isNull();

        clusterConfigService.write(UsageStatsOptOutState.create(false));
        assertThat(optOutService.getOptOutState().isOptOut()).isFalse();

        clusterConfigService.write(UsageStatsOptOutState.create(true));
        assertThat(optOutService.getOptOutState().isOptOut()).isTrue();
    }

    @Test
    public void testCreateOptOut() throws Exception {
        webserver.enqueue(OPT_OUT_RESPONSE);

        optOutService.setOptOutState(UsageStatsOptOutState.create(true));

        assertThat(webserver.getRequestCount()).isEqualTo(1);

        final RecordedRequest request = webserver.takeRequest(10, TimeUnit.SECONDS);

        assertThat(request.getBody().readByteArray())
                .isEqualTo(objectMapper.writeValueAsBytes(ImmutableMap.<String, String>builder().build()));
        assertThat(request.getHeader("X-Usage-Statistics-Version")).isEqualTo(USAGE_STATS_VERSION);
        assertThat(request.getHeader(HttpHeaders.USER_AGENT)).isEqualTo(USER_AGENT);

        assertThat(clusterConfigService.get(UsageStatsOptOutState.class)).isEqualTo(UsageStatsOptOutState.create(true));
    }

    @Test
    public void testCreateOptOutWithOptIn() throws Exception {
        clusterConfigService.write(UsageStatsOptOutState.create(true));

        webserver.enqueue(OPT_OUT_RESPONSE);

        optOutService.setOptOutState(UsageStatsOptOutState.create(false));

        assertThat(webserver.getRequestCount()).isEqualTo(0);
        assertThat(clusterConfigService.get(UsageStatsOptOutState.class)).isEqualTo(UsageStatsOptOutState.create(false));
    }

    @Test
    public void testCreateOptOutWithNullParam() throws Exception {
        webserver.enqueue(OPT_OUT_RESPONSE);

        optOutService.setOptOutState(null);

        assertThat(webserver.getRequestCount()).isEqualTo(0);
        assertThat(clusterConfigService.get(UsageStatsOptOutState.class)).isNull();
    }

    @Test
    public void testCreateOptOutWithNullClusterId() throws Exception {
        clusterConfigService.remove(ClusterId.class);

        optOutService.setOptOutState(UsageStatsOptOutState.create(true));

        assertThat(webserver.getRequestCount()).isEqualTo(0);
    }

    @Test
    public void testGetUrlDefault() throws Exception {
        // Make sure we get the real default URL.
        Mockito.reset(configSpy);

        assertThat(optOutService.getUrl().toString())
                .isEqualTo("https://stats-collector.graylog.com/submit/cluster/test-cluster-id/optout");
    }

    @Test
    public void testGetUrlWithoutClusterId() throws Exception {
        clusterConfigService.remove(ClusterId.class);

        assertThat(optOutService.getUrl()).isNull();
    }
}
