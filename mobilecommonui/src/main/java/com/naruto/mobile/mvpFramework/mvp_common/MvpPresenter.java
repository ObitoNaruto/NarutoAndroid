package com.naruto.mobile.mvpFramework.mvp_common;

import android.support.annotation.UiThread;

/**
 * The base interface for each mvp presenter.
 *
 * <p>
 * assumes that all interaction (i.e. updating the View) between Presenter and View is
 * executed on android's main UI thread.
 * </p>
 * @author xuxinming
 * @since 1.0.0
 */
public interface MvpPresenter<V extends MvpView> {

    /**
     * Set or attach the view to this presenter
     */
    @UiThread
    void attachView(V view);

    /**
     * Will be called if the view has been destroyed. Typically this method will be invoked from
     * <code>Activity.detachView()</code> or <code>Fragment.onDestroyView()</code>
     */
    @UiThread
    void detachView(boolean retainInstance);
}