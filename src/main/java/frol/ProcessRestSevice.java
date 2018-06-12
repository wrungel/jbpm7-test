package frol;

import org.jbpm.services.api.ProcessService;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/processService")
@Stateless
public class ProcessRestSevice {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentRestService.class);
    @Inject
    ProcessService processService;

    @POST
    @Path("/{deploymentId}/{processId}/start")
    @Produces({ "application/json" })
    public Long startProcess(@PathParam("deploymentId") String deploymentId, @PathParam("processId") String processId) {
        LOG.info("Stating process deploymentId={}, processId={}", deploymentId, processId);
        return processService.startProcess(deploymentId, processId);
    }

    @GET
    @Path("/getProcessInstance/{deploymentId}/{processInstanceId}")
    @Produces({ "application/json" })
    public ProcessInstance getProcessInstance(@PathParam("deploymentId") String deploymentId, @PathParam("processInstanceId") Long processInstanceId) {
        ProcessInstance processInstance = processService.getProcessInstance(deploymentId, processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException("ProcessInstance deploymentId=" + deploymentId +
                    "processInstanceId=" + processInstanceId + " not found.");
        }
        return processInstance;
    }
}
