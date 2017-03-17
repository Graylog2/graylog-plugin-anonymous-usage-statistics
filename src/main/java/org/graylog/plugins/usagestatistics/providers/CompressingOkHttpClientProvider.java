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
