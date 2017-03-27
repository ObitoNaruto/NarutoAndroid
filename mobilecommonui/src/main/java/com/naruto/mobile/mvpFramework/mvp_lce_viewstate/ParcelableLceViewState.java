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

package com.naruto.mobile.mvpFramework.mvp_lce_viewstate;

import com.naruto.mobile.mvpFramework.mvp_lce.MvpLceActivity;
import com.naruto.mobile.mvpFramework.mvp_lce.MvpLceView;
import com.naruto.mobile.mvpFramework.mvp_viewstate.RestorableParcelableViewState;

/**
 * A common interface for {@link LceViewState} and {@link RestorableParcelableViewState}. This one is used for {@link
 * MvpLceActivity}
 *
 * @author xuxinming
 * @since 1.0.0
 */
public interface ParcelableLceViewState<D, V extends MvpLceView<D>>
        extends RestorableParcelableViewState<V>, LceViewState<D, V> {

}
