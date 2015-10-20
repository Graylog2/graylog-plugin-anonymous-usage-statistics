package org.graylog.plugins.usagestatistics.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@JsonAutoDetect
@AutoValue
public abstract class LdapStats {

    @JsonProperty
    public abstract boolean enabled();

    @JsonProperty
    public abstract boolean activeDirectory();

    @JsonProperty
    public abstract int roleMappingCount();

    @JsonProperty
    public abstract int roleCount();

    @JsonCreator
    public static LdapStats create(@JsonProperty("enabled") boolean enabled,
                                   @JsonProperty("active_directory") boolean activeDirectory,
                                   @JsonProperty("role_mappings") int roleMappingCount,
                                   @JsonProperty("role_count") int roleCount) {
        return new AutoValue_LdapStats(enabled, activeDirectory, roleMappingCount, roleCount);
    }
}
