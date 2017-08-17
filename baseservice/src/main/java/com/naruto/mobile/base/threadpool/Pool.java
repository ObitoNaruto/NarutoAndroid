package com.naruto.mobile.base.threadpool;

import java.util.ArrayDeque;
import java.util.List;

public abstract class Pool<T> {

    /**
     * The maximum number of objects that will be pooled.
     */
    public final int max;

    /**
     * The highest number of free objects. Can be reset any time.
     */
    public int peak;

    /**
     * The free Objects
     */
    protected final ArrayDeque<T> freeObjects;

    /**
     * Creates a pool with an initial capacity of 16 and no maximum.
     */
    public Pool() {
        this(16, Integer.MAX_VALUE);
    }

    /**
     * Creates a pool with the specified initial capacity and no maximum.
     */
    public Pool(int initialCapacity) {
        this(initialCapacity, Integer.MAX_VALUE);
    }

    /**
     * @param max The maximum number of free objects to store in this pool.
     */
    public Pool(int initialCapacity, int max) {
        freeObjects = new ArrayDeque<T>(initialCapacity);
        this.max = max;
    }

    abstract protected T newObject();

    /**
     * Returns an object from this pool. The object may be new (from {@link #newObject()}) or reused (previously {@link
     * #free(Object) freed}).
     */
    public T obtain() {
        return freeObjects.size() == 0 ? newObject() : freeObjects.pop();
    }

    /**
     * Puts the specified object in the pool, making it eligible to be returned by {@link #obtain()}. If the pool
     * already contains {@link #max} free objects, the specified object is reset but not added to the pool.
     */
    public void free(T object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        if (freeObjects.size() < max) {
            freeObjects.add(object);
            peak = Math.max(peak, freeObjects.size());
        }
        if (object instanceof Poolable) {
            ((Poolable) object).reset();
        }
    }

    /**
     * Puts the specified objects in the pool. Null objects within the array are silently ignored.
     *
     * @see #free(Object)
     */
    public void freeAll(List<T> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }
        ArrayDeque<T> freeObjects = this.freeObjects;
        int max = this.max;
        for (int i = 0; i < objects.size(); i++) {
            T object = objects.get(i);
            if (object == null) {
                continue;
            }
            if (freeObjects.size() < max) {
                freeObjects.add(object);
            }
            if (object instanceof Poolable) {
                ((Poolable) object).reset();
            }
        }
        peak = Math.max(peak, freeObjects.size());
    }

    /**
     * Removes all free objects from this pool.
     */
    public void clear() {
        freeObjects.clear();
    }

    /**
     * The number of objects available to be obtained.
     */
    public int getFree() {
        return freeObjects.size();
    }

    /**
     * Objects implementing this interface will have {@link #reset()} called when passed to {@link #free(Object)}.
     */
    static public interface Poolable {

        /**
         * Resets the object for reuse. Object references should be nulled and fields may be set to default values.
         */
        public void reset();
    }
}
