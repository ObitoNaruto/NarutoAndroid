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

package com.naruto.mobile.mvpFramework.demo.mvp.lce.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.naruto.mobile.R;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesAdapter;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesErrorMessage;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesPresenter;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesView;
import com.naruto.mobile.mvpFramework.demo.mvp.lce.SimpleCountriesPresenter;
import com.naruto.mobile.mvpFramework.demo.mvp.model.Country;
import com.naruto.mobile.mvpFramework.mvp_lce.MvpLceFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author xuxinming
 */
public class CountriesFragment
        extends MvpLceFragment<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
        implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Unbinder unbinder;

    CountriesAdapter adapter;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//    SampleApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        unbinder = ButterKnife.bind(this, view);
        // Setup contentView == SwipeRefreshView
        contentView.setOnRefreshListener(this);
        // Setup recycler view
        adapter = new CountriesAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        loadData(false);
    }

    /**
     * 请求数据
     * @param pullToRefresh true, if triggered by a pull to refresh. Otherwise false.
     */
    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadCountries(pullToRefresh);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return CountriesErrorMessage.get(e, pullToRefresh, getActivity());
    }

    @Override
    public CountriesPresenter createPresenter() {
        return new SimpleCountriesPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mvp_countries_list, container, false);
    }

    /**
     * rpc请求数据返回
     * @param data
     */
    @Override
    public void setData(List<Country> data) {
        adapter.setCountries(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    /**
     * 更新UI
     */
    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        contentView.setRefreshing(false);
    }
}
