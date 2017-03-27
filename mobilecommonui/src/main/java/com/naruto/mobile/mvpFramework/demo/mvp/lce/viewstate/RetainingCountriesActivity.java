package com.naruto.mobile.mvpFramework.demo.mvp.lce.viewstate;

import android.os.Bundle;

import java.util.List;

import com.naruto.mobile.mvpFramework.demo.mvp.CountriesView;
import com.naruto.mobile.mvpFramework.demo.mvp.model.Country;
import com.naruto.mobile.mvpFramework.mvp_lce_viewstate.LceViewState;
import com.naruto.mobile.mvpFramework.mvp_lce_viewstate.data.RetainingLceViewState;

/**
 * @author Hannes Dorfmann
 */
public class RetainingCountriesActivity extends NotRetainingCountriesActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public LceViewState<List<Country>, CountriesView> createViewState() {
    return new RetainingLceViewState<>();
  }
}
