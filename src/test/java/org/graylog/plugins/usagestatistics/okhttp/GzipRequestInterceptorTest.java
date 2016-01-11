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
package org.graylog.plugins.usagestatistics.okhttp;

import com.github.joschi.jadconfig.util.Duration;
import com.google.common.net.HttpHeaders;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import okio.Source;
import org.graylog.plugins.usagestatistics.providers.CompressingOkHttpClientProvider;
import org.graylog2.shared.bindings.providers.OkHttpClientProvider;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class GzipRequestInterceptorTest {
    public final MockWebServer mockWebServer = new MockWebServer();

    private final OkHttpClientProvider clientProvider = new OkHttpClientProvider(
            Duration.seconds(1L),
            Duration.seconds(1L),
            Duration.seconds(1L),
            null);

    @Test
    public void httpClientUsesGzipInRequests() throws Exception {
        CompressingOkHttpClientProvider provider = new CompressingOkHttpClientProvider(clientProvider, true);
        OkHttpClient client = provider.get();
        mockWebServer.enqueue(new MockResponse().setResponseCode(202));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/test"))
                .post(RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), "Test"))
                .build();
        Response response = client.newCall(request).execute();

        assertThat(response.isSuccessful()).isTrue();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_ENCODING)).isEqualTo("gzip");
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/plain; charset=utf-8");

        try (Source source = Okio.source(new ByteArrayInputStream(recordedRequest.getBody().readByteArray()));
             BufferedSource gzipSource = Okio.buffer(new GzipSource(source))) {
            assertThat(gzipSource.readString(StandardCharsets.UTF_8)).isEqualTo("Test");
        }
    }
}