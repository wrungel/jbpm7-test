package frol;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.HashMap;
import java.util.Map;

public class WorkItemHandler1 implements WorkItemHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WorkItemHandler1.class);

    private final EntityManager em;
    private final TransactionSynchronizationRegistry txSynchronizationRegistry;

    public WorkItemHandler1(EntityManager em, TransactionSynchronizationRegistry txSynchronizationRegistry) {
        this.em = em;
        this.txSynchronizationRegistry = txSynchronizationRegistry;
    }


    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        LOG.info("executeWorkItem ...");

        Object txKey = txSynchronizationRegistry.getTransactionKey();
        MyEntity m1 = new MyEntity("M" + workItem.getProcessInstanceId());
        em.persist(m1);
        LOG.info("Entity persisted with id = " + m1.getId() + ", txKey: " + txKey);

        Map<String, Object> results = new HashMap<>();
        manager.completeWorkItem(workItem.getId(), results);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        LOG.info("executeWorkItem ...");
    }
}
