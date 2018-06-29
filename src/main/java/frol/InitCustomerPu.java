package frol;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
public class InitCustomerPu {
    private static final Logger LOG = LoggerFactory.getLogger(InitCustomerPu.class);
    public static final String ENTITY_NAME = "Uschi";

    @PersistenceContext(unitName = "customerPU") // default type is PersistenceContextType.TRANSACTION
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {

        List<MyEntity> resultList = em.createQuery("select e from MyEntity e where e.name = :name", MyEntity.class)
                .setParameter("name", ENTITY_NAME)
                .getResultList();
        if (resultList.isEmpty()) {
            em.persist(new MyEntity(ENTITY_NAME));
            LOG.info("New entity created");
        } else {
            LOG.info("Entity exists");
        }

    }
}
