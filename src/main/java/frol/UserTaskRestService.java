package frol;

import org.jbpm.services.api.UserTaskService;
import org.kie.api.task.model.Task;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/userTaskService")
@Stateless
public class UserTaskRestService {

    @Inject
    private UserTaskService userTaskService;

    @GET
    @Path("/{taskId}/start")
    @Produces({ "application/json" })
    public Response getProcessInstances(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.start(taskId, userId);
        return Response.ok().build();
    }

    @GET
    @Path("/{taskId}/activate")
    public Response activate(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.activate(taskId, userId);
        return Response.ok().build();
    }

    @GET
    @Path("/{taskId}")
    public Task getTask(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        Task task = userTaskService.getTask(taskId);
        return task;
    }
}
