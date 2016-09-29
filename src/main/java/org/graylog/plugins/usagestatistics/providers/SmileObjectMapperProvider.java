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
