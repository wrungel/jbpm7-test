package frol;

import javax.ejb.Stateless;
import javax.inject.Named;

@Named("AsyncTask2")
@Stateless
public class AsyncTask2 extends AbstractAsyncTask {

    @Override
    String getTaskName() {
        return AsyncTask2.class.getSimpleName();
    }
}
