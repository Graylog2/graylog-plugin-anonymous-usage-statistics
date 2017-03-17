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

import com.github.joschi.jadconfig.JadConfig;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.repositories.InMemoryRepository;
import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Map;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class UsageStatsConfigurationTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void validateSucceedsIfDirectoryIsWritable() throws Exception {
        File directory = temporaryFolder.newFolder();
        Map<String, String> properties = ImmutableMap.of(
                "usage_statistics_enabled", "true",
                "usage_statistics_offline_mode", "true",
                "usage_statistics_dir", directory.getAbsolutePath()
        );

        UsageStatsConfiguration configuration = new UsageStatsConfiguration();
        new JadConfig(new InMemoryRepository(properties), configuration).process();

        configuration.validate();
    }

    @Test
    public void validateSucceedsIfDirectoryDoesNotExist() throws Exception {
        File parent = temporaryFolder.newFolder();
        final File directory = new File(parent, "test");
        assumeFalse(directory.exists());

        Map<String, String> properties = ImmutableMap.of(
                "usage_statistics_enabled", "true",
                "usage_statistics_offline_mode", "true",
                "usage_statistics_dir", directory.getAbsolutePath()
        );

        UsageStatsConfiguration configuration = new UsageStatsConfiguration();
        new JadConfig(new InMemoryRepository(properties), configuration).process();

        configuration.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateFailsIfDirectoryIsNotWritable() throws Exception {
        File directory = temporaryFolder.newFolder();
        assumeTrue(directory.setWritable(false));

        Map<String, String> properties = ImmutableMap.of(
                "usage_statistics_enabled", "true",
                "usage_statistics_offline_mode", "true",
                "usage_statistics_dir", directory.getAbsolutePath()
        );

        UsageStatsConfiguration configuration = new UsageStatsConfiguration();
        new JadConfig(new InMemoryRepository(properties), configuration).process();

        configuration.validate();
    }

    @Test(expected = ValidationException.class)
    public void validateFailsIfPathIsNotADirectory() throws Exception {
        File file = temporaryFolder.newFile();

        Map<String, String> properties = ImmutableMap.of(
                "usage_statistics_enabled", "true",
                "usage_statistics_offline_mode", "true",
                "usage_statistics_dir", file.getAbsolutePath()
        );

        UsageStatsConfiguration configuration = new UsageStatsConfiguration();
        new JadConfig(new InMemoryRepository(properties), configuration).process();

        configuration.validate();
    }
}
