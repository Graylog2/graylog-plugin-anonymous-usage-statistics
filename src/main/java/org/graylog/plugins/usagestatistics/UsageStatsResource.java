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
