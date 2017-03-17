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

import com.github.joschi.jadconfig.util.Duration;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
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
