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

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.EnumSet;
import java.util.Set;

public class UsageStatsMetaData implements PluginMetaData {
    public static final Version VERSION = new Version(2, 1, 0, "beta.2");

    @Override
    public String getUniqueId() {
        return UsageStatsPlugin.class.getCanonicalName();
    }

    @Override
    public String getName() {
        return "Anonymous Usage Statistics";
    }


    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return EnumSet.of(ServerStatus.Capability.SERVER);
    }

    @Override
    public String getAuthor() {
        return "Graylog, Inc.";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.graylog.org/");
    }

    @Override
    public Version getVersion() {
        return VERSION;
    }

    @Override
    public String getDescription() {
        return "A plugin for collecting anonymous usages statistics about Graylog nodes and clusters.";
    }

    @Override
    public Version getRequiredVersion() {
        return new Version(2, 1, 0);
    }
}
