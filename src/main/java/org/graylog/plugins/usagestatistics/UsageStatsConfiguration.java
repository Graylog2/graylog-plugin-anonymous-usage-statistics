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
