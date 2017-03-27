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

package com.naruto.mobile.mvpFramework.demo.mvp.lce.viewstate;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import com.naruto.mobile.R;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesAdapter;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesErrorMessage;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesPresenter;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesView;
import com.naruto.mobile.mvpFramework.demo.mvp.lce.SimpleCountriesPresenter;
import com.naruto.mobile.mvpFramework.demo.mvp.model.Country;
import com.naruto.mobile.mvpFramework.mvp_lce_viewstate.LceViewState;
import com.naruto.mobile.mvpFramework.mvp_lce_viewstate.MvpLceViewStateActivity;
import com.naruto.mobile.mvpFramework.mvp_lce_viewstate.data.CastedArrayListLceViewState;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is an example of a NOT retaining activity. It serializes and deserializes the viewstate into a bundle
 *
 * @author xuxinming
 */
public class NotRetainingCountriesActivity extends
        MvpLceViewStateActivity<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
        implements CountriesView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    CountriesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mvp_countries_list);
        ButterKnife.bind(this);
        // Setup contentView == SwipeRefreshView
        contentView.setOnRefreshListener(this);
        // Setup recycler view
        adapter = new CountriesAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public LceViewState<List<Country>, CountriesView> createViewState() {
        return new CastedArrayListLceViewState<>();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadCountries(pullToRefresh);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return CountriesErrorMessage.get(e, pullToRefresh, this);
    }

    @Override
    public CountriesPresenter createPresenter() {
        return new SimpleCountriesPresenter();
    }

    @Override
    public void setData(List<Country> data) {
        adapter.setCountries(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

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

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        if (pullToRefresh && !contentView.isRefreshing()) {
            // Workaround for measure bug: https://code.google.com/p/android/issues/detail?id=77712
            contentView.post(new Runnable() {
                @Override
                public void run() {
                    contentView.setRefreshing(true);
                }
            });
        }
    }

    /**
     * Create the view state object of this class
     */
    @Override
    public List<Country> getData() {
        return adapter == null ? null : adapter.getCountries();
    }
}
