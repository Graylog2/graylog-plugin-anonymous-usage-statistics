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
package org.graylog.plugins.usagestatistics;

import okhttp3.MediaType;
import org.graylog2.plugin.Version;

public class UsageStatsConstants {
    public static final MediaType CONTENT_TYPE = MediaType.parse("application/x-jackson-smile");
    public static final String USAGE_STATS_VERSION = UsageStatsMetaData.VERSION.toString();
    public static final String USER_AGENT = "Graylog " + Version.CURRENT_CLASSPATH;
}
