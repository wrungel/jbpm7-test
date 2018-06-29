package frol;

import org.jbpm.services.api.model.DeploymentUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.List;

@Path("/deploymentService")
@Stateless
public class DeploymentRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentRestService.class);

    @Inject
    MyDeploymentService deploymentService;

    @Context
    private UriInfo uriInfo;
    @Context
    private HttpHeaders httpHeaders;

    @GET
    @Path("/deployedUnits")
    @Produces({ "application/json" })
    public List<DeployedUnitDto> deployedUnits() {
        return deploymentService.deployedUnitsInternal();
    }

//    /**
//     * <a href="https://stackoverflow.com/questions/5738665/how-to-get-source-file-in-httprequest-for-uploading-file">https://stackoverflow.com/questions/5738665/how-to-get-source-file-in-httprequest-for-uploading-file</a>
//     */
//    @PUT
//    @Path("/deploy/{groupId}/{artifactId}/{version}")
//    @Consumes({"application/zip"})
//    public Response deployZip(@PathParam("groupId") String groupId,
//                              @PathParam("artifactId") String artifactId,
//                              @PathParam("version") String version,
//                              byte[] zipContents) throws IOException {
//
//        DeploymentUnit deploymentUnit = deploymentService.deployZip(groupId, artifactId, version, zipContents);
//        return Response.created(uriInfo.getAbsolutePath()).build();
//    }

    @PUT
    @Path("/deploy/{groupId}/{artifactId}/{version}/{assetName}")
    @Consumes({"text/plain"})
    public Response deploy(@PathParam("groupId") String groupId,
                           @PathParam("artifactId") String artifactId,
                           @PathParam("version") String version,
                           @PathParam("assetName") String assetName,
                           byte[] contents) throws IOException {

        DeploymentUnit deploymentUnit = deploymentService.deploy(groupId, artifactId, version, assetName, contents);
        return Response.created(uriInfo.getAbsolutePath()).build();
    }

}