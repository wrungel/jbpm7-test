package frol;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

@Path("/taskService")
@Stateless
public class TaskRestService {

    @Inject
    private TaskService taskService;


    @GET
    @Path("/")
    @Produces({ "application/json" })
    public List<TaskSummary> get(
            @QueryParam("userId") String userId) {
        return taskService.getTasksAssignedAsPotentialOwner(userId, null);
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


}
