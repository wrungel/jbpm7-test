package frol;

import org.jbpm.services.api.UserTaskService;
import org.kie.api.task.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/userTaskService")
@Stateless
public class UserTaskRestService {

    public static final Logger LOG = LoggerFactory.getLogger(UserTaskService.class);

    @Inject
    private UserTaskService userTaskService;


    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;


    @POST
    @Path("/{taskId}/start")
    @Produces({ "application/json" })
    public Response getProcessInstances(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.start(taskId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/activate")
    public Response activate(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.activate(taskId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/setDescription")
    public Response setDescription(
            @PathParam("taskId") Long taskId,
            @QueryParam("description") String description,
            @QueryParam("sleep") Long sleep) {

        LOG.info("thread: {}, emf: {}", Thread.currentThread().getName(), Integer.toHexString(System.identityHashCode(emf)));

        LOG.info("Enter setDescrption({}, \"{}\")", taskId, description);
        userTaskService.setDescription(taskId, description);
        LOG.info("Sleep setDescrption({}, \"{}\")", taskId, description);
        if (sleep != null && sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        LOG.info("Retrn setDescrption({}, \"{}\")", taskId, description);
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/complete")
    public Response complete(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.complete(taskId, userId, new HashMap<>());
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/claim")
    public Response claim(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.claim(taskId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/release")
    public Response release(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.release(taskId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/resume")
    public Response resume(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.resume(taskId, userId);
        return Response.ok().build();
    }


    @POST
    @Path("/{taskId}/stop")
    public Response stop(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.stop(taskId, userId);
        return Response.ok().build();
    }


    @POST
    @Path("/{taskId}/exit")
    public Response exit(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.exit(taskId, userId);
        return Response.ok().build();
    }


    @POST
    @Path("/{taskId}/suspend")
    public Response suspend(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.suspend(taskId, userId);
        return Response.ok().build();
    }

    @POST
    @Path("/{taskId}/skip")
    public Response skip(
            @PathParam("taskId") Long taskId,
            @QueryParam("userId") String userId) {
        userTaskService.skip(taskId, userId);
        return Response.ok().build();
    }

    @GET
    @Path("/{taskId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Task getTask(
            @PathParam("taskId") Long taskId) {
        return userTaskService.getTask(taskId);
    }
}
