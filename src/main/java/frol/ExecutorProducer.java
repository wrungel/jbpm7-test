package frol;

import org.jbpm.executor.ExecutorServiceFactory;
import org.kie.api.executor.ExecutorService;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

public class ExecutorProducer {

    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;


    @Produces
    ExecutorService produceExecutorService() {
        ExecutorService executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.init();
        return executorService;
    }
}
