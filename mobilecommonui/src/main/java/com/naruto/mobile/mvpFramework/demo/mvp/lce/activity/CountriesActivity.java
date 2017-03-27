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

package com.naruto.mobile.mvpFramework.demo.mvp.lce.activity;

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
import com.naruto.mobile.mvpFramework.mvp_lce.MvpLceActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountriesActivity
    extends MvpLceActivity<SwipeRefreshLayout, List<Country>, CountriesView, CountriesPresenter>
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
    loadData(false);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
  }

  /**
   * 请求数据
   * @param pullToRefresh true, if triggered by a pull to refresh. Otherwise false.
     */
  @Override
  public void loadData(boolean pullToRefresh) {
    presenter.loadCountries(pullToRefresh);
  }

  /**
   * showError回调发生时，自定义的异常信息
   * @param e
   * @param pullToRefresh
   * @return
     */
  @Override
  protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return CountriesErrorMessage.get(e, pullToRefresh, this);
  }

  @Override
  public CountriesPresenter createPresenter() {
    return new SimpleCountriesPresenter();
  }

  /**
   * 更新数据
   * @param data
     */
  @Override
  public void setData(List<Country> data) {
    adapter.setCountries(data);
    adapter.notifyDataSetChanged();
  }

  /**
   * 下拉刷新组件的下拉刷新回调
   */
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

  /**
   * 发生异常时的回调，这里处理UI
   * @param e             The Throwable that has caused this error
   * @param pullToRefresh true, if the exception was thrown during pull-to-refresh, otherwise false.
     */
  @Override
  public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
  }
}
