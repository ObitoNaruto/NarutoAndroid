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

package com.naruto.mobile.mvpFramework.mvp_viewstate;

import android.os.Bundle;
import android.view.View;

import com.naruto.mobile.mvpFramework.mvp.MvpFragment;
import com.naruto.mobile.mvpFramework.mvp.delegate.FragmentMvpDelegate;
import com.naruto.mobile.mvpFramework.mvp_common.MvpPresenter;
import com.naruto.mobile.mvpFramework.mvp_common.MvpView;
import com.naruto.mobile.mvpFramework.mvp_viewstate.delegate.FragmentMvpViewStateDelegateImpl;
import com.naruto.mobile.mvpFramework.mvp_viewstate.delegate.MvpViewStateDelegateCallback;

/**
 * This is a enhancement of {@link MvpFragment} that introduces the support of {@link ViewState}. <p> You can change the
 * behaviour of what to do if the viewstate is empty (usually if the fragment creates the viewState for the very first
 * time and therefore has no state / data to restore) by overriding {@link #onNewViewStateInstance()} </p>
 *
 * @author xuxinming
 * @since 1.0.0
 */
public abstract class MvpViewStateFragment<V extends MvpView, P extends MvpPresenter<V>, VS extends ViewState<V>>
        extends MvpFragment<V, P> implements MvpViewStateDelegateCallback<V, P, VS> {

    /**
     * The viewstate will be instantiated by calling {@link #createViewState()} in {@link #onViewCreated(View, Bundle)}.
     * Don't instantiate it by hand.
     */
    protected VS viewState;

    /**
     * A simple flag that indicates if the restoring ViewState  is in progress right now.
     */
    private boolean restoringViewState = false;

    @Override
    protected FragmentMvpDelegate<V, P> getMvpDelegate() {
        if (mvpDelegate == null) {
            mvpDelegate = new FragmentMvpViewStateDelegateImpl<>(this, this, true, true);
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
    public void setRestoringViewState(boolean restoringViewState) {
        this.restoringViewState = restoringViewState;
    }

    @Override
    public boolean isRestoringViewState() {
        return restoringViewState;
    }

    @Override
    public void onViewStateInstanceRestored(boolean instanceStateRetained) {
        // not needed. You could override this is subclasses if needed
    }
}
