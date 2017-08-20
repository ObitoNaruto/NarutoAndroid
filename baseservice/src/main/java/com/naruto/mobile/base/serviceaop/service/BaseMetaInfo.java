package com.naruto.mobile.base.serviceaop.service;

import android.content.BroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import com.naruto.mobile.base.serviceaop.app.ApplicationDescription;
import com.naruto.mobile.base.serviceaop.broadcast.BroadcastReceiverDescription;
import com.naruto.mobile.base.serviceaop.task.ValueDescription;

/**
 * Created by xinming.xxm on 2016/5/15.
 */
public abstract class BaseMetaInfo {

    public List<ApplicationDescription> applications = new ArrayList<ApplicationDescription>();
    public List<ServiceDescription> services = new ArrayList<ServiceDescription>();
    public List<BroadcastReceiverDescription> broadcastReceivers = new ArrayList<>();
    public List<ValueDescription> mValueDescriptions = new ArrayList<>();
    public String entry = null;

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public List<ApplicationDescription> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationDescription> applications) {
        this.applications = applications;
    }

    public void addApplication(ApplicationDescription applicationDescription) {
        if(applications == null) {
            applications = new ArrayList<>();
        }
        applications.add(applicationDescription);
    }


    public List<ServiceDescription> getServices() {
        return services;
    }

    public void setServices(List<ServiceDescription> services) {
        this.services = services;
    }

    public void addService(ServiceDescription serviceDescription) {
        if (null == services) {
            services = new ArrayList<>();
        }
        services.add(serviceDescription);
    }

    public List<BroadcastReceiverDescription> getBroadcastReceivers() {
        return broadcastReceivers;
    }

    public void setBroadcastReceivers(List<BroadcastReceiverDescription> broadcastReceivers) {
        this.broadcastReceivers = broadcastReceivers;
    }

    public void addBroadcastReceiver(BroadcastReceiverDescription broadcastReceiverDescription) {
        if (broadcastReceivers == null) {
            broadcastReceivers = new ArrayList<>();
        }
        broadcastReceivers.add(broadcastReceiverDescription);
    }

    public List<ValueDescription> getValueDescriptions() {
        return mValueDescriptions;
    }

    public void setValueDescriptions(
            List<ValueDescription> valueDescriptions) {
        mValueDescriptions = valueDescriptions;
    }

    public void addValueDescription(ValueDescription valueDescription) {
        if (mValueDescriptions == null) {
            mValueDescriptions = new ArrayList<>();
        }
        mValueDescriptions.add(valueDescription);
    }
}
