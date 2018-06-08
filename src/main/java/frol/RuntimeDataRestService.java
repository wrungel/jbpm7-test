package frol;

import com.google.common.collect.Lists;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.kie.internal.query.QueryContext;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Collection;
import java.util.List;

@Path("/runtimeDataService")
@Stateless
public class RuntimeDataRestService {

    @Inject
    private RuntimeDataService runtimeDataService;

    @GET
    @Path("/getProcessInstances")
    @Produces({ "application/json" })
    public List<ProcessInstanceDesc> getProcessInstances() {
        Collection<ProcessInstanceDesc> processInstances = runtimeDataService.getProcessInstances(new QueryContext());
        return Lists.newArrayList(processInstances);
    }
}
