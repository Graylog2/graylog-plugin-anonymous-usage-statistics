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
package org.graylog.plugins.usagestatistics.dto;

import com.codahale.metrics.Snapshot;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class Histogram {
    public static Histogram create(long count, long min, long max, double mean,
                                   double p50, double p75, double p95,
                                   double p98, double p99, double p999,
                                   double stddev) {
        return new AutoValue_Histogram(count, min, max, mean, p50, p75, p95, p98, p99, p999, stddev);
    }

    public static Histogram fromMetricsTimer(com.codahale.metrics.Timer timer) {
        final Snapshot snapshot = timer.getSnapshot();
        return create(
                timer.getCount(),
                snapshot.getMin(),
                snapshot.getMax(),
                snapshot.getMean(),
                snapshot.getMedian(),
                snapshot.get75thPercentile(),
                snapshot.get95thPercentile(),
                snapshot.get98thPercentile(),
                snapshot.get99thPercentile(),
                snapshot.get999thPercentile(),
                snapshot.getStdDev()
        );
    }

    public static Histogram fromMetricsHistogram(com.codahale.metrics.Histogram histogram) {
        final Snapshot snapshot = histogram.getSnapshot();
        return create(
                histogram.getCount(),
                snapshot.getMin(),
                snapshot.getMax(),
                snapshot.getMean(),
                snapshot.getMedian(),
                snapshot.get75thPercentile(),
                snapshot.get95thPercentile(),
                snapshot.get98thPercentile(),
                snapshot.get99thPercentile(),
                snapshot.get999thPercentile(),
                snapshot.getStdDev()
        );
    }

    @JsonProperty
    public abstract long count();

    @JsonProperty
    public abstract long min();

    @JsonProperty
    public abstract long max();

    @JsonProperty
    public abstract double mean();

    @JsonProperty
    public abstract double p50();

    @JsonProperty
    public abstract double p75();

    @JsonProperty
    public abstract double p95();

    @JsonProperty
    public abstract double p98();

    @JsonProperty
    public abstract double p99();

    @JsonProperty
    public abstract double p999();

    @JsonProperty
    public abstract double stddev();
}
