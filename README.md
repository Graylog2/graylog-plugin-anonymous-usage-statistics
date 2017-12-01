# DEPRECATION NOTICE

As of Graylog 2.4.0/3.0.0, this plugin is deprecated and will not be developed any further.

----

# Graylog Anonymous Usage Statistics Plugin
[![Build Status](https://travis-ci.org/Graylog2/graylog-plugin-anonymous-usage-statistics.svg?branch=master)](https://travis-ci.org/Graylog2/graylog-plugin-anonymous-usage-statistics)

A plugin for collecting anonymous usage statistics of Graylog nodes and clusters.


## Configuration Options

The following configuration options can be added to your `graylog.conf`.

| Name                                | Default                                       | Description                                           |
|-------------------------------------|-----------------------------------------------|-------------------------------------------------------|
| `usage_statistics_enabled`          | `true`                                        | Enable publishing usage statistics.                   |
| `usage_statistics_url`              | `https://stats-collector.graylog.com/submit/` | Base URL of the usage statistics service.             |
| `usage_statistics_cache_timeout`    | `15m`                                         | TTL for usage statistics in local cache.              |
| `usage_statistics_max_queue_size`   | `10`                                          | Number of usage statistics data sets to store locally |
|                                     |                                               | if the connection to the web service fails.           |
| `usage_statistics_report_interval`  | `6h`                                          | How often the usage statistics should be reported.    |
| `usage_statistics_initial_delay`    | `5m`                                          | How long to wait until the first report.              |
| `usage_statistics_gzip_enabled`     | `true`                                        | Enable gzip compression for HTTP requests.            |
| `usage_statistics_offline_mode`     | `false`                                       | Enable offline mode (data is stored on local disk).   |
| `usage_statistics_dir`              | `data/usage-stats`                            | Directory in which data is stored in offline mode.    |


## Development

This project is using Maven 3 and requires Java 7 or higher. The plugin will require Graylog 1.1.0 or higher.

* Clone this repository.
* Run `mvn package` to build a JAR file.
* Optional: Run `mvn jdeb:jdeb` and `mvn rpm:rpm` to create a DEB and RPM package respectively.
* Copy generated JAR file in target directory to your Graylog server plugin directory.
* Restart the Graylog server.


## License

Copyright (c) 2015 Graylog, Inc.

This library is licensed under the Apache License, Version 2.0.

See http://www.apache.org/licenses/LICENSE-2.0.html or the LICENSE file in this repository for the full license text.
