package com.naruto.mobile.mvpFramework.mvp_viewstate.delegate;

import com.naruto.mobile.mvpFramework.mvp.delegate.MvpDelegateCallback;
import com.naruto.mobile.mvpFramework.mvp.delegate.ViewGroupDelegateCallback;
import com.naruto.mobile.mvpFramework.mvp_common.MvpPresenter;
import com.naruto.mobile.mvpFramework.mvp_common.MvpView;
import com.naruto.mobile.mvpFramework.mvp_viewstate.ViewState;

/**
 * * An enhanced version of {@link MvpDelegateCallback} that adds {@link ViewState} support. This interface must be
 * implemented by all (subclasses of) android.view.View like FrameLayout that want to support {@link ViewState} and
 * mvp.
 *
 * @author xuxinming
 * @since 3.0
 */
public interface ViewGroupMvpViewStateDelegateCallback<V extends MvpView, P extends MvpPresenter<V>, VS extends ViewState<V>>
        extends MvpViewStateDelegateCallback<V, P, VS>, ViewGroupDelegateCallback<V, P> {

}
