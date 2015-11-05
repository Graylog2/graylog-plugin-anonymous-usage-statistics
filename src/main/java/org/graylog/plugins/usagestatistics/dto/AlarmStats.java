package org.graylog.plugins.usagestatistics.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Map;

@JsonAutoDetect
@AutoValue
public abstract class AlarmStats {
    @JsonProperty
    public abstract long alertCount();

    @JsonProperty
    public abstract Map<String, Long> alarmcallbackCountByType();

    public static AlarmStats create(long alertCount, Map<String, Long> alarmcallbackCountByType) {
        return new AutoValue_AlarmStats(alertCount, alarmcallbackCountByType);
    }
}
