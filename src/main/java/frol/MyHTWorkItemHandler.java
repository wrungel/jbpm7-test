package frol;

import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.TransactionSynchronizationRegistry;

public class MyHTWorkItemHandler extends LocalHTWorkItemHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MyHTWorkItemHandler.class);

    private final TransactionSynchronizationRegistry txSynchronizationRegistry;

    public MyHTWorkItemHandler(TransactionSynchronizationRegistry txSynchronizationRegistry) {
        this.txSynchronizationRegistry = txSynchronizationRegistry;
    }

    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Object txKey = txSynchronizationRegistry.getTransactionKey();
        LOG.info("txKey: " + txKey);
        super.executeWorkItem(workItem, manager);
    }
}
