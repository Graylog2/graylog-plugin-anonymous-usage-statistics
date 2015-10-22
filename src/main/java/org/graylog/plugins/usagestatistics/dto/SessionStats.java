package org.graylog.plugins.usagestatistics.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class SessionStats {
    @JsonProperty
    public abstract long authenticated();

    @JsonProperty
    public abstract long extended();

    @JsonProperty
    public abstract long expired();

    @JsonProperty
    public abstract long logins();

    @JsonProperty
    public abstract long logouts();

    @JsonCreator
    public static SessionStats create(long authenticated,
                                      long extended,
                                      long expired,
                                      long logins,
                                      long logouts) {
        return new AutoValue_SessionStats(authenticated,
                                          extended,
                                          expired,
                                          logins,
                                          logouts);
    }
}
