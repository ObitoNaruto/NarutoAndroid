package com.naruto.mobile.base.Router.andRouter.exception;

/**
 */
public class InvalidRoutePathException extends Exception{

    public InvalidRoutePathException(String routePath){
        super(String.format("Invalid route path %s", routePath));
    }
}
