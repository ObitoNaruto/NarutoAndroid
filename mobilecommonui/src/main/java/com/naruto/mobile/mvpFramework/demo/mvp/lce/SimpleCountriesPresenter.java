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

package com.naruto.mobile.mvpFramework.demo.mvp.lce;

import android.util.Log;

import java.util.List;

import com.naruto.mobile.mvpFramework.demo.mvp.CountriesPresenter;
import com.naruto.mobile.mvpFramework.demo.mvp.CountriesView;
import com.naruto.mobile.mvpFramework.demo.mvp.model.CountriesAsyncLoader;
import com.naruto.mobile.mvpFramework.demo.mvp.model.Country;
import com.naruto.mobile.mvpFramework.mvp.MvpBasePresenter;

/**
 * @author xuxinming
 */
public class SimpleCountriesPresenter extends MvpBasePresenter<CountriesView>
        implements CountriesPresenter {

    private static final String TAG = "CountriesPresenter";

    private int failingCounter = 0;

    private CountriesAsyncLoader countriesLoader;

    public SimpleCountriesPresenter() {
        Log.d(TAG, "constructor " + toString());
    }

    @Override
    public void loadCountries(final boolean pullToRefresh) {
        Log.d(TAG, "loadCountries(" + pullToRefresh + ")");
        Log.d(TAG, "showLoading(" + pullToRefresh + ")");
        getView().showLoading(pullToRefresh);
        if (countriesLoader != null && !countriesLoader.isCancelled()) {
            countriesLoader.cancel(true);
        }
        countriesLoader = new CountriesAsyncLoader(++failingCounter % 2 != 0,
                new CountriesAsyncLoader.CountriesLoaderListener() {

                    @Override
                    public void onSuccess(List<Country> countries) {
                        if (isViewAttached()) {
                            Log.d(TAG, "setData()");
                            getView().setData(countries);//设置数据
                            Log.d(TAG, "showContent()");
                            getView().showContent();//利用上面设置的数据更新布局
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (isViewAttached()) {
                            Log.d(TAG, "showError(" + e.getClass().getSimpleName() + " , " + pullToRefresh + ")");
                            getView().showError(e, pullToRefresh);
                        }
                    }
                });
        countriesLoader.execute();
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        StringBuilder builder = new StringBuilder("---- detachView(" + retainInstance + ") ");
        if (!retainInstance) {
            if (countriesLoader != null) {
                countriesLoader.cancel(true);
            }
            builder.append(" --> cancel Loader");
        }
        builder.append(" ----");
        Log.d(TAG, builder.toString());
    }

    @Override
    public void attachView(CountriesView view) {
        super.attachView(view);
        Log.d(TAG, "attach view " + view.toString());
    }
}
