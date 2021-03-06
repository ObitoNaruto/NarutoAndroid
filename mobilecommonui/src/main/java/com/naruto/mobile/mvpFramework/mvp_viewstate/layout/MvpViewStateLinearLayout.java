/*
 * Copyright 2015 Hannes Dorfmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.naruto.mobile.mvpFramework.mvp_viewstate.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

import com.naruto.mobile.mvpFramework.mvp.delegate.ViewGroupMvpDelegate;
import com.naruto.mobile.mvpFramework.mvp.layout.MvpLinearLayout;
import com.naruto.mobile.mvpFramework.mvp_common.MvpPresenter;
import com.naruto.mobile.mvpFramework.mvp_common.MvpView;
import com.naruto.mobile.mvpFramework.mvp_viewstate.ViewState;
import com.naruto.mobile.mvpFramework.mvp_viewstate.delegate.ViewGroupMvpViewStateDelegateCallback;
import com.naruto.mobile.mvpFramework.mvp_viewstate.delegate.ViewGroupMvpViewStateDelegateImpl;

/**
 * A {@link MvpLinearLayout} with viewstate support
 *
 * @author xuxinming
 * @since 1.1
 */
public abstract class MvpViewStateLinearLayout<V extends MvpView, P extends MvpPresenter<V>, VS extends ViewState<V>>
        extends MvpLinearLayout<V, P> implements ViewGroupMvpViewStateDelegateCallback<V, P, VS> {

    private boolean restoringViewState = false;

    protected VS viewState;

    public MvpViewStateLinearLayout(Context context) {
        super(context);
    }

    public MvpViewStateLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MvpViewStateLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public MvpViewStateLinearLayout(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected ViewGroupMvpDelegate<V, P> getMvpDelegate() {
        if (mvpDelegate == null) {
            mvpDelegate = new ViewGroupMvpViewStateDelegateImpl<V, P, VS>(this, true);
        }
        return mvpDelegate;
    }

    @Override
    public VS getViewState() {
        return viewState;
    }

    @Override
    public void setViewState(VS viewState) {
        this.viewState = viewState;
    }

    @Override
    public void setRestoringViewState(boolean retstoringViewState) {
        this.restoringViewState = retstoringViewState;
    }

    @Override
    public boolean isRestoringViewState() {
        return restoringViewState;
    }

    @Override
    public void onViewStateInstanceRestored(boolean instanceStateRetained) {
        // can be overridden in subclass
    }

}
