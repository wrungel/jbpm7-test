package frol;

import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/taskService")
@Stateless
public class TaskRestService {

    @Inject
    private TaskService taskService;


    @GET
    @Path("/{taskId}")
    @Produces({ "application/json" })
    public Task get(
            @PathParam("taskId") Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @POST
    @Path("/{taskId}/claim")
    @Produces({ "application/json" })
    public Response claim(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        taskService.claim(taskId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/start")
    @Produces({ "application/json" })
    public Response start(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        taskService.start(taskId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/complete")
    @Produces({ "application/json" })
    public Response complete(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        taskService.complete(taskId, userId, new HashMap<>());
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/exit")
    @Produces({ "application/json" })
    public Response exit(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        taskService.exit(taskId, userId);
        return Response.ok().build();
    }
}
