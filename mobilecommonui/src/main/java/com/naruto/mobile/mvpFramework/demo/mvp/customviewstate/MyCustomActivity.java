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

package com.naruto.mobile.mvpFramework.demo.mvp.customviewstate;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.naruto.mobile.R;
import com.naruto.mobile.mvpFramework.demo.mvp.model.custom.A;
import com.naruto.mobile.mvpFramework.demo.mvp.model.custom.B;
import com.naruto.mobile.mvpFramework.mvp_viewstate.MvpViewStateActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.naruto.mobile.R.layout.mvp_my_custom_view;

/**
 * @author xuxinming
 */
public class MyCustomActivity extends MvpViewStateActivity<MyCustomView, MyCustomPresenter, MyCustomViewState>
        implements MyCustomView {

    @BindView(R.id.textViewA)
    TextView aView;

    @BindView(R.id.textViewB)
    TextView bView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mvp_my_custom_view);
        ButterKnife.bind(this);
    }

    @Override
    public MyCustomViewState createViewState() {
        return new MyCustomViewState();
    }

    @Override
    public void onNewViewStateInstance() {
        presenter.doA();
    }

    @Override
    public MyCustomPresenter createPresenter() {
        return new MyCustomPresenter();
    }

    @Override
    public void showA(A a) {
        viewState.setShowingA(true);
        viewState.setData(a);
        aView.setText(a.getName());
        aView.setVisibility(View.VISIBLE);
        bView.setVisibility(View.GONE);
    }

    @Override
    public void showB(B b) {
        viewState.setShowingA(false);
        viewState.setData(b);
        bView.setText(b.getFoo());
        aView.setVisibility(View.GONE);
        bView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.loadA)
    public void onLoadAClicked() {
        presenter.doA();
    }

    @OnClick(R.id.loadB)
    public void onLoadBClicked() {
        presenter.doB();
    }
}
