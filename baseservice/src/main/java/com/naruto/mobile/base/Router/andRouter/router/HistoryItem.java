package com.naruto.mobile.base.Router.andRouter.router;

/**
 */
public class HistoryItem {

    Class<?> from;

    Class<?> to;

    public HistoryItem(Class<?> from , Class<?> to){
        this.from = from;
        this.to = to;
    }

    public Class<?> getFrom(){
        return from;
    }

    public Class<?> getTo(){
        return to;
    }
}
