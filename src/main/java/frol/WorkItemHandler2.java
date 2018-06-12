package frol;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkItemHandler2 implements WorkItemHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WorkItemHandler2.class);

    private final EntityManager em;
    private final TransactionSynchronizationRegistry txSynchronizationRegistry;

    public WorkItemHandler2(EntityManager em, TransactionSynchronizationRegistry txSynchronizationRegistry) {
        this.em = em;
        this.txSynchronizationRegistry = txSynchronizationRegistry;
    }


    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        LOG.info("executeWorkItem ...");

        Object txKey = txSynchronizationRegistry.getTransactionKey();

        List<MyEntity> myEntities = em.createQuery("select t from MyEntity t", MyEntity.class).getResultList();
        LOG.info("Entities found: {}, txKey: {}", myEntities, txKey);

        Map<String, Object> results = new HashMap<>();
        manager.completeWorkItem(workItem.getId(), results);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        LOG.info("executeWorkItem ...");
    }
}


