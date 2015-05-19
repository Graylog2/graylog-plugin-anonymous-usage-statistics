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
package org.graylog.plugins.usagestatistics.providers;

import com.github.joschi.jadconfig.util.Duration;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import org.graylog.plugins.usagestatistics.okhttp.GzipRequestInterceptor;
import org.graylog2.shared.bindings.providers.OkHttpClientProvider;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class CompressingOkHttpClientProviderTest {
    private final OkHttpClientProvider clientProvider = new OkHttpClientProvider(
            Duration.seconds(1L),
            Duration.seconds(1L),
            Duration.seconds(1L),
            null);

    @Test
    public void providerAddsGzipRequestInterceptorIfGzipIsEnabled() {
        CompressingOkHttpClientProvider provider = new CompressingOkHttpClientProvider(clientProvider, true);

        OkHttpClient client = provider.get();

        assertThat(client.interceptors()).isNotEmpty();

        boolean found = false;
        for (Interceptor interceptor : client.interceptors()) {
            if (interceptor instanceof GzipRequestInterceptor) {
                found = true;
                break;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    public void providerDoesNotAddGzipRequestInterceptorIfGzipIsDisabled() {
        CompressingOkHttpClientProvider provider = new CompressingOkHttpClientProvider(clientProvider, false);
        OkHttpClient client = provider.get();

        assertThat(client.interceptors()).isEmpty();
    }
}