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

import okhttp3.OkHttpClient;
import org.graylog.plugins.usagestatistics.okhttp.GzipRequestInterceptor;
import org.graylog2.shared.bindings.providers.OkHttpClientProvider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class CompressingOkHttpClientProvider implements Provider<OkHttpClient> {
    private final OkHttpClient client;

    @Inject
    public CompressingOkHttpClientProvider(OkHttpClientProvider okHttpClientProvider,
                                           @Named("usage_statistics_gzip_enabled") boolean gzipEnabled) {
        final OkHttpClient.Builder clientBuilder = okHttpClientProvider.get().newBuilder();

        if (gzipEnabled) {
            clientBuilder.addInterceptor(new GzipRequestInterceptor());
        }

        client = clientBuilder.build();
    }

    @Override
    public OkHttpClient get() {
        return client;
    }
}
