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
import org.graylog.plugins.usagestatistics.dto.ClusterDataSet;
import org.graylog.plugins.usagestatistics.dto.NodeDataSet;
import org.graylog2.plugin.rest.PluginRestResource;
import org.graylog2.shared.rest.resources.RestResource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequiresAuthentication
@Api(value = "UsageStatistics", description = "Anonymous usage statistics of this Graylog setup")
@Path("/")
public class UsageStatsResource extends RestResource implements PluginRestResource {
    private final UsageStatsNodeService usageStatsNodeService;
    private final UsageStatsClusterService usageStatsClusterService;
    private final UsageStatsConfiguration configuration;

    @Inject
    public UsageStatsResource(UsageStatsNodeService usageStatsNodeService,
                              UsageStatsClusterService usageStatsClusterService,
                              UsageStatsConfiguration configuration) {
        this.usageStatsNodeService = usageStatsNodeService;
        this.usageStatsClusterService = usageStatsClusterService;
        this.configuration = configuration;
    }

    @GET
    @Path("config")
    @Timed
    @ApiOperation(value = "Show configuration for the anonymous usage statistics plugin")
    @Produces(MediaType.APPLICATION_JSON)
    public UsageStatsConfigurationResponse showConfig() {
        return UsageStatsConfigurationResponse.create(configuration.isEnabled());
    }

    @GET
    @Path("node")
    @Timed
    @ApiOperation(value = "Show the collected anonymous usage statistics of this Graylog node")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public NodeDataSet showNodeDataSet() {
        return usageStatsNodeService.buildNodeDataSet();
    }

    @GET
    @Path("cluster")
    @Timed
    @ApiOperation(value = "Show the collected anonymous usage statistics of this Graylog cluster")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public ClusterDataSet showClusterDataSet() {
        return usageStatsClusterService.buildClusterDataSet();
    }
}
