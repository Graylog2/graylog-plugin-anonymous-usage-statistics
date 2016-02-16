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

import com.google.common.collect.Maps;
import org.graylog2.plugin.cluster.ClusterConfigService;
import org.graylog2.shared.utilities.AutoValueUtils;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;

public class TestClusterConfigService implements ClusterConfigService {
    private final Map<String, Object> data = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        return (T) data.get(type.getCanonicalName());
    }

    @Override
    public <T> T getOrDefault(Class<T> type, T defaultValue) {
        return firstNonNull(get(type), defaultValue);
    }

    @Override
    public <T> void write(T payload) {
        data.put(AutoValueUtils.getCanonicalName(payload.getClass()), payload);
    }

    public <T> int remove(Class<T> type) {
        return data.remove(type.getCanonicalName()) == null ? 0 : 1;
    }

    @Override
    public Set<Class<?>> list() {
        return data.keySet()
                .stream()
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException ignore) {
                        return null;
                    }
                })
                .filter(aClass -> aClass != null)
                .collect(Collectors.toSet());
    }

    public void clear() {
        data.clear();
    }
}
