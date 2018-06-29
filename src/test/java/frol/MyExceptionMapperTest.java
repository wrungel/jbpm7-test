package frol;

import javax.ejb.EJBTransactionRolledbackException;
import javax.persistence.OptimisticLockException;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class MyExceptionMapperTest {
    @Test
    public void notOptimisticLockException() {
        String entity = new MyExceptionMapper().getStackTraceAsString(new EJBTransactionRolledbackException("sss", new RuntimeException()));
        MatcherAssert.assertThat(entity, CoreMatchers.startsWith("javax.ejb.EJBTransactionRolledbackException: sss"));
    }
    @Test
    public void optimisticLockException() {
        String entity = new MyExceptionMapper().toEntity(new EJBTransactionRolledbackException("sss",
                new OptimisticLockException(new RuntimeException())));
        MatcherAssert.assertThat(entity, CoreMatchers.equalTo("OptimisticLockException"));
    }
}
