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

import okhttp3.MediaType;
import org.graylog2.plugin.Version;

public class UsageStatsConstants {
    public static final MediaType CONTENT_TYPE = MediaType.parse("application/x-jackson-smile");
    public static final String USAGE_STATS_VERSION = UsageStatsMetaData.VERSION.toString();
    public static final String USER_AGENT = "Graylog " + Version.CURRENT_CLASSPATH;
}
