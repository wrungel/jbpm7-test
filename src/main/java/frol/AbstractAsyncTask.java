package frol;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAsyncTask implements Command {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractAsyncTask.class);

    @PersistenceContext(unitName = "customerPU")
    private EntityManager em;

    @Resource
    private TransactionSynchronizationRegistry tx;

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        WorkItem workItem = (WorkItem) ctx.getData("workItem");
        LOG.info("execute task {}, processInstanceId {} in tx: {}",
                getTaskName(), workItem.getProcessInstanceId(), tx());
        MyEntity myEntity = em.createQuery("select t from MyEntity t where t.name = :name", MyEntity.class)
                .setParameter("name", "Uschi")
                .getSingleResult();
        Thread.sleep(7000);
        myEntity.setDescription(
                String.format("set by %s on %s", getTaskName(), LocalDateTime.now().toString()));
        return success();
    }

    private String tx() {
        return String.format("{key: %s, status: %s}",
                tx.getTransactionKey(),
                toTxString(tx.getTransactionStatus()));
    }


    private String toTxString(int transactionStatus) {
        switch (transactionStatus) {
            case Status.STATUS_ACTIVE: return "STATUS_ACTIVE";
            case Status.STATUS_MARKED_ROLLBACK: return "STATUS_MARKED_ROLLBACK";
            case Status.STATUS_PREPARED: return "STATUS_PREPARED";
            case Status.STATUS_COMMITTED: return "STATUS_COMMITTED";
            case Status.STATUS_ROLLEDBACK: return "STATUS_ROLLEDBACK";
            case Status.STATUS_UNKNOWN: return "STATUS_UNKNOWN";
            case Status.STATUS_NO_TRANSACTION: return "STATUS_NO_TRANSACTION";
            case Status.STATUS_PREPARING: return "STATUS_PREPARING";
            case Status.STATUS_COMMITTING: return "STATUS_COMMITTING";
            case Status.STATUS_ROLLING_BACK: return "STATUS_ROLLING_BACK";
            default: return null;
        }
    }

    private ExecutionResults success() {
        ExecutionResults executionResults = new ExecutionResults();

        HashMap<String, Object> data = new HashMap<>();
        data.put("success", true);
        executionResults.setData(data);

        return executionResults;
    }

    abstract String getTaskName();
}
