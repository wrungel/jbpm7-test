package frol;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.ejb.EJBTransactionRolledbackException;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;

@Provider
public class MyExceptionMapper implements ExceptionMapper<EJBTransactionRolledbackException> {
    @Override
    public Response toResponse(EJBTransactionRolledbackException exception) {
        String entity = toEntity(exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(entity).build();
    }

    String toEntity(EJBTransactionRolledbackException exception) {
        String entity;
        if (containsOptimisticLockException(exception)) {
            entity = "OptimisticLockException";
        } else {
            entity = getStackTraceAsString(exception);
        }
        return entity;
    }

    String getStackTraceAsString(EJBTransactionRolledbackException exception) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(byteArrayOutputStream));
        return byteArrayOutputStream.toString();
    }

    private boolean containsOptimisticLockException(Throwable e) {
        return ExceptionUtils.indexOfType(e, OptimisticLockException.class) != -1;
    }
}
