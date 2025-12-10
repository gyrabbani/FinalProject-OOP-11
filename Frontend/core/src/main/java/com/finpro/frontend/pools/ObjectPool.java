package com.finpro.frontend.pools;

import java.util.ArrayList;
import java.util.List;

public abstract class ObjectPool<T> {

    private final List<T> available = new ArrayList<>();
    private final List<T> inUse = new ArrayList<>();

    protected abstract T createObject();

    public T obtain() {
        T object;
        if (available.isEmpty()) {
            object = createObject();
        } else {
            object = available.remove(available.size() - 1);
        }
        inUse.add(object);
        return object;
    }

    public void free(T object) {
        if (inUse.remove(object)) {
            available.add(object);
        }
    }

    public List<T> getActiveObjects() {
        return inUse;
    }
}

