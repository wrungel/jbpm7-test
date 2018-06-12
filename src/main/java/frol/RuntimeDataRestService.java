package frol;

import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryContext;
import org.kie.internal.query.QueryFilter;

import com.google.common.collect.Lists;

@Path("/runtimeDataService")
@Stateless
public class RuntimeDataRestService {

    @Inject
    private RuntimeDataService runtimeDataService;

    @GET
    @Path("/processInstance")
    @Produces({ "application/json" })
    public List<ProcessInstanceDesc> getProcessInstances() {
        Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstances(new QueryContext());
        return Lists.newArrayList(processInstances);
    }

    @GET
    @Path("/processInstance/{id}/tasks")
    @Produces({ "application/json" })
    public List<TaskSummary> getProcessInstances2(@PathParam("id") Long processInstanceId) {
        return runtimeDataService.getTasksByStatusByProcessInstanceId(
                processInstanceId, Lists.newArrayList(), new QueryFilter());
    }
}
