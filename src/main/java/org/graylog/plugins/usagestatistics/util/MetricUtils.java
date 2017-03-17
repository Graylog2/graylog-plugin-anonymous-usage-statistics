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
package org.graylog.plugins.usagestatistics.util;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Reservoir;
import com.codahale.metrics.Timer;
import com.codahale.metrics.UniformReservoir;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.MoreObjects.firstNonNull;

public class MetricUtils {
    private static final Counter EMPTY_COUNTER = new Counter() {
        @Override
        public void inc(long n) {
            // NOP
        }

        @Override
        public void dec(long n) {
            // NOP
        }
    };
    private static final Gauge<Integer> EMPTY_INTEGER_GAUGE = new Gauge<Integer>() {
        @Override
        public Integer getValue() {
            return 0;
        }
    };
    private static final Gauge<Long> EMPTY_LONG_GAUGE = new Gauge<Long>() {
        @Override
        public Long getValue() {
            return 0L;
        }
    };
    private static final Gauge<Double> EMPTY_DOUBLE_GAUGE = new Gauge<Double>() {
        @Override
        public Double getValue() {
            return 0.0D;
        }
    };
    private static final Reservoir EMPTY_RESERVOIR = new UniformReservoir(0) {
        @Override
        public void update(long value) {
            // NOP
        }
    };
    private static final Histogram EMPTY_HISTOGRAM = new Histogram(EMPTY_RESERVOIR) {
        @Override
        public void update(long value) {
            // NOP
        }
    };
    private static final Meter EMPTY_METER = new Meter() {
        @Override
        public void mark(long n) {
            // NOP
        }
    };
    private static final Timer EMPTY_TIMER = new Timer() {
        @Override
        public void update(long duration, TimeUnit unit) {
            // NOP
        }

        @Override
        public <T> T time(Callable<T> event) throws Exception {
            return null;
        }

        @Override
        public Context time() {
            return null;
        }
    };

    private MetricUtils() {
    }

    public static Counter safeGetCounter(MetricRegistry registry, String name) {
        return firstNonNull(registry.getCounters().get(name), EMPTY_COUNTER);
    }

    @SuppressWarnings("unchecked")
    public static Gauge<Integer> safeGetIntegerGauge(MetricRegistry registry, String name) {
        return firstNonNull(registry.getGauges().get(name), EMPTY_INTEGER_GAUGE);
    }

    @SuppressWarnings("unchecked")
    public static Gauge<Long> safeGetLongGauge(MetricRegistry registry, String name) {
        return firstNonNull(registry.getGauges().get(name), EMPTY_LONG_GAUGE);
    }

    @SuppressWarnings("unchecked")
    public static Gauge<Double> safeGetDoubleGauge(MetricRegistry registry, String name) {
        return firstNonNull(registry.getGauges().get(name), EMPTY_DOUBLE_GAUGE);
    }

    public static Histogram safeGetHistogram(MetricRegistry registry, String name) {
        return firstNonNull(registry.getHistograms().get(name), EMPTY_HISTOGRAM);
    }

    public static Meter safeGetMeter(MetricRegistry registry, String name) {
        return firstNonNull(registry.getMeters().get(name), EMPTY_METER);
    }

    public static Timer safeGetTimer(MetricRegistry registry, String name) {
        return firstNonNull(registry.getTimers().get(name), EMPTY_TIMER);
    }
}
