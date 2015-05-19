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

import autovalue.shaded.com.google.common.common.collect.ImmutableMap;
import com.github.joschi.jadconfig.JadConfig;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.repositories.InMemoryRepository;
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