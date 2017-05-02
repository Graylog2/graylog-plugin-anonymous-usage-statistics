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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import javax.inject.Provider;

public class SmileObjectMapperProvider implements Provider<ObjectMapper> {
    private final ObjectMapper objectMapper;

    public SmileObjectMapperProvider() {
        final SmileFactory smileFactory = new SmileFactory()
                .disable(SmileGenerator.Feature.ENCODE_BINARY_AS_7BIT)
                .enable(SmileGenerator.Feature.WRITE_END_MARKER);

        objectMapper = new ObjectMapper(smileFactory)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .registerModule(new JodaModule())
                .registerModule(new GuavaModule());
    }

    @Override
    public ObjectMapper get() {
        return objectMapper;
    }
}
