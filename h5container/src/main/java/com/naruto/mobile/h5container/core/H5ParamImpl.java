
package com.naruto.mobile.h5container.core;

import android.os.Bundle;

import com.naruto.mobile.h5container.api.H5Param.ParamType;
import com.naruto.mobile.h5container.env.H5Container;
import com.naruto.mobile.h5container.util.H5Utils;

public class H5ParamImpl {

    private String longName;
    private String shortName;
    private ParamType type;
    private Object defaultValue;

    public H5ParamImpl(String ln, String sn, ParamType type, Object dv) {
        this.longName = ln;
        this.shortName = sn;
        this.type = type;
        this.defaultValue = dv;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public Object getDefaulValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object value) {
        this.defaultValue = value;
    }

    public Bundle unify(Bundle bundle, boolean fillDefault) {
        if (!fillDefault && !H5Utils.contains(bundle, longName)
                && !H5Utils.contains(bundle, shortName)) {
            return bundle;
        }

        // short name value priority first
        if (ParamType.BOOLEAN == type) {
            boolean value = (Boolean) defaultValue;

            Object obj = null;
            if (bundle.containsKey(shortName)) {
                obj = bundle.get(shortName);
            } else if (bundle.containsKey(longName)) {
                obj = bundle.get(longName);
            }

            if (obj instanceof String) {
                String valueStr = (String) obj;
                if (H5Container.KEY_YES.equalsIgnoreCase(valueStr)) {
                    value = true;
                } else if (H5Container.KEY_NO.equalsIgnoreCase(valueStr)) {
                    value = false;
                }
            } else if (obj instanceof Boolean) {
                value = (Boolean) obj;
            }

            bundle.putBoolean(longName, value);
        } else if (ParamType.STRING == type) {
            String df = (String) defaultValue;
            String value = df;
            if (H5Utils.contains(bundle, shortName)) {
                value = H5Utils.getString(bundle, shortName, df);
            } else if (H5Utils.contains(bundle, longName)) {
                value = H5Utils.getString(bundle, longName, df);
            }
            bundle.putString(longName, value);
        } else if (ParamType.INT.equals(type)) {
            int df = (Integer) defaultValue;
            int value = df;
            if (H5Utils.contains(bundle, shortName)) {
                value = H5Utils.getInt(bundle, shortName, df);
            } else if (H5Utils.contains(bundle, longName)) {
                value = H5Utils.getInt(bundle, longName, df);
            }
            bundle.putInt(longName, value);
        } else if (ParamType.DOUBLE.equals(type)) {
            double df = (Double) defaultValue;
            double value = df;
            if (H5Utils.contains(bundle, shortName)) {
                value = H5Utils.getDouble(bundle, shortName, df);
            } else if (H5Utils.contains(bundle, longName)) {
                value = H5Utils.getDouble(bundle, longName, df);
            }
            bundle.putDouble(longName, value);
        }

        bundle.remove(shortName);
        return bundle;
    }
}
