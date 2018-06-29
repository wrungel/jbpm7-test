package frol;

import javax.ejb.Stateless;
import javax.inject.Named;

@Named("AsyncTask1")
@Stateless
public class AsyncTask1 extends AbstractAsyncTask {

    @Override
    String getTaskName() {
        return AsyncTask1.class.getSimpleName();
    }
}
