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
