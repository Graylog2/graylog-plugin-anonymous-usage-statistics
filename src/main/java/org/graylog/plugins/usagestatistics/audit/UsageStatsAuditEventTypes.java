package org.graylog.plugins.usagestatistics.audit;

import com.google.common.collect.ImmutableSet;
import org.graylog2.audit.PluginAuditEventTypes;

import java.util.Set;

public class UsageStatsAuditEventTypes implements PluginAuditEventTypes {
    private static final String NAMESPACE = "usage_statistics:";

    public static final String OPT_OUT_UPDATE = NAMESPACE + "opt_out:update";

    private static final Set<String> EVENT_TYPES = ImmutableSet.<String>builder()
            .add(OPT_OUT_UPDATE)
            .build();

    @Override
    public Set<String> auditEventTypes() {
        return EVENT_TYPES;
    }
}
