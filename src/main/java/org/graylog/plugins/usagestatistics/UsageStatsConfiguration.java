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

import com.github.joschi.jadconfig.Parameter;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.ValidatorMethod;
import com.github.joschi.jadconfig.util.Duration;
import com.github.joschi.jadconfig.validators.PositiveDurationValidator;
import com.github.joschi.jadconfig.validators.PositiveIntegerValidator;
import org.graylog2.plugin.PluginConfigBean;

import java.io.File;
import java.net.URI;

import static com.google.common.base.Strings.isNullOrEmpty;

public class UsageStatsConfiguration implements PluginConfigBean {
    private static final String PREFIX = "usage_statistics_";

    @Parameter(value = PREFIX + "enabled")
    private boolean enabled = true;

    @Parameter(value = PREFIX + "url", required = true)
    private URI url = URI.create("https://stats-collector.graylog.com/submit/");

    @Parameter(value = PREFIX + "cache_timeout", required = true, validator = PositiveDurationValidator.class)
    private Duration cacheTimeout = Duration.minutes(15L);

    @Parameter(value = PREFIX + "max_queue_size", required = true, validator = PositiveIntegerValidator.class)
    private int maxQueueSize = 10;

    @Parameter(value = PREFIX + "report_interval", required = true, validator = PositiveDurationValidator.class)
    private Duration reportInterval = Duration.hours(6L);

    @Parameter(value = PREFIX + "initial_delay", required = true, validator = PositiveDurationValidator.class)
    private Duration initialDelay = Duration.minutes(5L);

    @Parameter(value = PREFIX + "gzip_enabled")
    private boolean gzipEnabled = true;

    @Parameter(value = PREFIX + "offline_mode")
    private boolean offlineMode = false;

    @Parameter(value = PREFIX + "dir")
    private File directory = new File("data/usage-statistics");

    public boolean isEnabled() {
        return enabled;
    }

    public URI getUrl() {
        return url;
    }

    public Duration getCacheTimeout() {
        return cacheTimeout;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public Duration getReportInterval() {
        return reportInterval;
    }

    public Duration getInitialDelay() {
        return initialDelay;
    }

    public boolean isGzipEnabled() {
        return gzipEnabled;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public File getDirectory() {
        return directory;
    }

    @ValidatorMethod
    public void validate() throws ValidationException {
        if (!isEnabled()) {
            return;
        }

        if (!isOfflineMode()) {
            if (isNullOrEmpty(getUrl().getHost())) {
                throw new ValidationException("Service URL " + getDirectory() + " must contain a hostname.");
            }
        }

        if (isOfflineMode() && getDirectory().exists()) {
            if (!getDirectory().isDirectory()) {
                throw new ValidationException("Directory " + getDirectory() + " must be a directory.");
            }

            if (!getDirectory().canWrite()) {
                throw new ValidationException("Directory " + getDirectory() + " must be writable.");
            }
        }
    }
}
