package com.naruto.mobile.base.threadpool;

import android.util.Log;

import java.util.ArrayDeque;

/**
 * ArrayDeque demo:http://cakin24.iteye.com/blog/2324030
 */
public class TransactionExecutor {

    static final String TAG = TransactionExecutor.class.getSimpleName();

    /**
     *  ArrayDeque集合是Deque接口的实现类，它是一个基于数组的双端队列，创建Deque时同样可以指定一个numElements参数，该参数用于指定Object[]数组的长度；如果不指定该参数，Deque底层数组长度为16。
     ArrayDeque集合既可当队列使用，也可当栈使用
     eg:使用push可以当栈使用；使用offer当队列使用
     */
    final ArrayDeque<Transaction> mTransactions = new ArrayDeque<Transaction>();

    /**
     * 当前正在执行的AsyncTask任务(其实就是一个Runnable)
     */
    volatile Transaction mActive;

    /**
     * 添加一个任务到Tape执行器中。Tape执行器将按照添加的先后顺序执行任务。
     *
     * @param transaction
     * @return 任务的ID
     */
    public String addTransaction(Transaction transaction) {
        synchronized (mTransactions) {
            mTransactions.offer(transaction);//入队列
        }
        if (mActive == null) {
            scheduleNext();//执行下一个AsyncTask
        } else {
            Log.v(TAG, "TransactionExecutor.execute(a transaction is running, so don't call scheduleNext())");
        }
        return transaction.id;
    }

    /**
     *
     *
     * @param id
     */
    public void removeTransaction(String id) {
        for (Transaction transaction : mTransactions) {
            if (transaction.id.equals(id)) {
                synchronized (mTransactions) {
                    mTransactions.remove(transaction);
                }
                break;
            }
        }
        if (null != mActive && mActive.id.equals(id)) {
            mActive = null;
        }
        if (mActive == null) {
            scheduleNext();//执行下一个AsyncTask
        } else {
            Log.v(TAG, "TransactionExecutor.execute(a transaction is running, so don't call scheduleNext())");
        }
    }

    /**
     * 执行下一个AsyncTask
     */
    private void scheduleNext() {
        Transaction transaction;
        synchronized (mTransactions) {
            mActive = mTransactions.poll();//出队列
            transaction = mActive;
        }
        if (mActive != null) {
            Log.d(TAG, "TransactionExecutor.scheduleNext()");
            transaction.run();
        } else {
            Log.d(TAG, "TransactionExecutor.scheduleNext(mTransactions is empty)");
        }
    }

    /**
     * 关闭执行器
     */
    public void shutdown() {
        mTransactions.clear();
    }
}
