
package com.naruto.mobile.h5container.service;

import android.os.Bundle;

import com.naruto.mobile.base.serviceaop.service.CommonService;
import com.naruto.mobile.base.serviceaop.service.ext.ExternalService;
import com.naruto.mobile.h5container.api.H5Bundle;

public abstract class H5Service extends ExternalService{

    public abstract void startPage(Bundle bundle);

}
