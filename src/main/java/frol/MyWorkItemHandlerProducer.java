package frol;

import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.WorkItemHandlerProducer;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.HashMap;
import java.util.Map;

public class MyWorkItemHandlerProducer implements WorkItemHandlerProducer {

    @PersistenceContext(unitName = "customerPU") // default type is PersistenceContextType.TRANSACTION
    private EntityManager entityManager;

    @Resource
    private TransactionSynchronizationRegistry txSynchronizationRegistry;


    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(String identifier, Map<String, Object> params) {
        HashMap<String, WorkItemHandler> result = new HashMap<>();
        RuntimeManager runtimeManager = (RuntimeManager) params.get("runtimeManager");
        result.put("Task1", new WorkItemHandler1(entityManager, txSynchronizationRegistry));
        result.put("Task2", new WorkItemHandler2(entityManager, txSynchronizationRegistry));
        MyHTWorkItemHandler myHTWorkItemHandler = new MyHTWorkItemHandler(txSynchronizationRegistry);
        myHTWorkItemHandler.setRuntimeManager(runtimeManager);
        result.put("Human Task", myHTWorkItemHandler);
        return result;
    }
}
