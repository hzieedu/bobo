package com.dudu.bobo.client.support;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author liangy43
 *
 */
public class FutureImpl<T> implements Future<T> {

    private volatile T obj = null;

    public void signal(T obj) {
        synchronized (this) {
            this.obj = obj;
            this.notify();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return obj != null;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return get(-1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            return null;
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (this) {
            this.wait(timeout);
            return obj;
        }
    }

}
