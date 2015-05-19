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

import static com.google.common.base.Objects.firstNonNull;

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
    private static final Gauge<Number> EMPTY_GAUGE = new Gauge<Number>() {
        @Override
        public Number getValue() {
            return 0;
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
    public static <T extends Number> Gauge<T> safeGetNumericGauge(MetricRegistry registry, String name) {
        return firstNonNull(registry.getGauges().get(name), EMPTY_GAUGE);
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
