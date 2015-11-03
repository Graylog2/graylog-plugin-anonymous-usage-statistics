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

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
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

@RequiresAuthentication
@Api(value = "Usage Statistics Opt-Out", description = "Anonymous usage statistics opt-out state of this Graylog setup")
@Path("/opt-out")
public class UsageStatsOptOutResource extends RestResource implements PluginRestResource {
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
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public UsageStatsOptOutState getOptOutState() {
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
    public void setOptOutState(@Valid @NotNull UsageStatsOptOutState optOutState) {
        usageStatsOptOutService.setOptOutState(optOutState);
    }
}
