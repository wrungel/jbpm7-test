package frol;

import javax.transaction.TransactionSynchronizationRegistry;

import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.jbpm.services.task.wih.util.PeopleAssignmentHelper;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyHTWorkItemHandler extends LocalHTWorkItemHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MyHTWorkItemHandler.class);

    private final TransactionSynchronizationRegistry txSynchronizationRegistry;

    public MyHTWorkItemHandler(TransactionSynchronizationRegistry txSynchronizationRegistry) {
        this.txSynchronizationRegistry = txSynchronizationRegistry;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        // alternative to <potentialOwner> XML bpmn tag:
        // workItem.getParameters().put(PeopleAssignmentHelper.ACTOR_ID, "foo, frol");
        Object txKey = txSynchronizationRegistry.getTransactionKey();
        LOG.info("txKey: " + txKey);
        super.executeWorkItem(workItem, manager);
    }
}
