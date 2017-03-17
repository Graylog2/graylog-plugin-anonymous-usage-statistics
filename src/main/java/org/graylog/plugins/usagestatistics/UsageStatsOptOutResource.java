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

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.graylog.plugins.usagestatistics.audit.UsageStatsAuditEventTypes;
import org.graylog2.audit.jersey.AuditEvent;
import org.graylog2.plugin.rest.PluginRestResource;
import org.graylog2.shared.rest.resources.RestResource;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static org.graylog2.shared.security.RestPermissions.CLUSTER_CONFIG_ENTRY_CREATE;
import static org.graylog2.shared.security.RestPermissions.CLUSTER_CONFIG_ENTRY_READ;

@RequiresAuthentication
@Api(value = "UsageStatistics/Opt-Out", description = "Anonymous usage statistics opt-out state of this Graylog setup")
@Path("/opt-out")
public class UsageStatsOptOutResource extends RestResource implements PluginRestResource {
    private static final String CLUSTER_CONFIG_INSTANCE = UsageStatsOptOutState.class.getCanonicalName();

    private final UsageStatsOptOutService usageStatsOptOutService;

    @Inject
    public UsageStatsOptOutResource(UsageStatsOptOutService usageStatsOptOutService) {
        this.usageStatsOptOutService = usageStatsOptOutService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    @ApiOperation(value = "Get opt-out status")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Opt-out status does not exist"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UsageStatsOptOutState getOptOutState() {
        checkPermission(CLUSTER_CONFIG_ENTRY_READ, CLUSTER_CONFIG_INSTANCE);

        final UsageStatsOptOutState optOutState = usageStatsOptOutService.getOptOutState();

        if (optOutState == null) {
            throw new NotFoundException();
        }

        return optOutState;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    @ApiOperation(value = "Disable sending anonymous usage stats")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Missing or invalid opt-out state"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @AuditEvent(type = UsageStatsAuditEventTypes.OPT_OUT_UPDATE)
    public void setOptOutState(@Valid @NotNull UsageStatsOptOutState optOutState) {
        checkPermission(CLUSTER_CONFIG_ENTRY_CREATE, CLUSTER_CONFIG_INSTANCE);

        usageStatsOptOutService.setOptOutState(optOutState);
    }
}
