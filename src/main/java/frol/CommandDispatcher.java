package frol;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.process.WorkItem;

public class CommandDispatcher implements Command {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        WorkItem workItem = (WorkItem) ctx.getData("workItem");
        Command command = resolveCommand(workItem.getName());
        return command.execute(ctx);
    }

    private Command resolveCommand(String workItemName) {
        BeanManager beanManager = getBeanManager();
        CreationalContext<Object> creationalContext = beanManager.createCreationalContext(null);
        Set<Bean<?>> beans = beanManager.getBeans(workItemName);
        Bean<?> bean = beanManager.resolve(beans);
        return (Command) beanManager.getReference(bean, bean.getBeanClass(), creationalContext);
    }

    private BeanManager getBeanManager() {
        try {
            return (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
        } catch (NamingException e) {
            throw new RuntimeException();
        }
    }
}
