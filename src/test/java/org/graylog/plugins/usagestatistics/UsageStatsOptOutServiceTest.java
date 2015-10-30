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
import com.github.joschi.jadconfig.util.Duration;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.MoreExecutors;
import com.squareup.okhttp.Dispatcher;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
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
        final OkHttpClient httpClient = clientProvider.get();

        // Use a direct executor for the Dispatcher to avoid async calls during tests.
        httpClient.setDispatcher(new Dispatcher(MoreExecutors.newDirectExecutorService()));

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
        assertThat(optOutService.getOptOutState().isOptOut()).isFalse();

        clusterConfigService.write(UsageStatsOptOutState.create(false));
        assertThat(optOutService.getOptOutState().isOptOut()).isFalse();

        clusterConfigService.write(UsageStatsOptOutState.create(true));
        assertThat(optOutService.getOptOutState().isOptOut()).isTrue();
    }

    @Test
    public void testCreateOptOut() throws Exception {
        webserver.enqueue(OPT_OUT_RESPONSE);

        optOutService.createOptOut(UsageStatsOptOutState.create(true));

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

        optOutService.createOptOut(UsageStatsOptOutState.create(false));

        assertThat(webserver.getRequestCount()).isEqualTo(0);
        assertThat(clusterConfigService.get(UsageStatsOptOutState.class)).isEqualTo(UsageStatsOptOutState.create(false));
    }

    @Test
    public void testCreateOptOutWithNullParam() throws Exception {
        webserver.enqueue(OPT_OUT_RESPONSE);

        optOutService.createOptOut(null);

        assertThat(webserver.getRequestCount()).isEqualTo(0);
        assertThat(clusterConfigService.get(UsageStatsOptOutState.class)).isNull();
    }

    @Test
    public void testCreateOptOutWithNullClusterId() throws Exception {
        clusterConfigService.remove(ClusterId.class);

        optOutService.createOptOut(UsageStatsOptOutState.create(true));

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
