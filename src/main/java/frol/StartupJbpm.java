package frol;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

@Singleton
@Startup
@DependsOn("InitCustomerPu")
public class StartupJbpm {
    private static final Logger LOG = LoggerFactory.getLogger(MyDeploymentService.class);

    private static final String PROCESS_ID = "frol.jbpm7test.SimpleAsyncProcessWithPotentionalOwner";
    private static final String BPMN_RESOURCE = "SimpleAsyncProcessWithPotentionalOwner.bpmn2";
    private static final String GROUP_ID = "frol";
    private static final String ARTIFACT_ID = "frol";
    private static final String VERSION = "1.0";

    @Inject
    private MyDeploymentService deploymentService;

    @Inject
    private DeploymentService jbpmDeploymentService;

    @Inject
    private ProcessService processService;

    @PersistenceContext(unitName = "customerPU") // default type is PersistenceContextType.TRANSACTION
    private EntityManager em;

    @PostConstruct
    void init() {
        LOG.info("starting up ...");

        em.persist(new MyEntity("uschi"));

        byte[] bpmnContent = getBpmnContent();
        try {
            DeploymentUnit deploymentUnit = deploymentService.deploy(GROUP_ID, ARTIFACT_ID, VERSION, BPMN_RESOURCE, bpmnContent);
            for (int i = 0; i < 2; i++) {
                startProcess(deploymentUnit.getIdentifier());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startProcess(String deploymentId) {
        LOG.info("*** Workflow {} has been deployed in unit {}", BPMN_RESOURCE, deploymentId);
        Long processInstanceId = processService.startProcess(deploymentId, PROCESS_ID);
        LOG.info("*** Process {} has been started, processInstanceId: {}", PROCESS_ID, processInstanceId);
    }

    private byte[] getBpmnContent() {
        try {
            String resourceName = "/assets/" + BPMN_RESOURCE;
            InputStream resource = StartupJbpm.class.getResourceAsStream(resourceName);
            if (resource == null) {
                throw new RuntimeException("No resource in classpath: " + resourceName);
            }
            return ByteStreams.toByteArray(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



