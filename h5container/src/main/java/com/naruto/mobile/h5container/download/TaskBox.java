
package com.naruto.mobile.h5container.download;

import java.util.List;

public interface TaskBox<T> {
    public boolean addTask(T t);

    public boolean hasTask(String url);

    public T removeTask(String url);

    public boolean clearTask();

    public T getTask(String url);

    public T doTask();

    public List<T> tasks();

    public int size();
}
