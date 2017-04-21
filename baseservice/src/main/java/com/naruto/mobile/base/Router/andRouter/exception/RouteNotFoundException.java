package com.naruto.mobile.base.Router.andRouter.exception;

/**
 */
public class RouteNotFoundException extends Exception {
    public RouteNotFoundException(String routePath){
        super(String.format("The route not found: %s", routePath));
    }
}
