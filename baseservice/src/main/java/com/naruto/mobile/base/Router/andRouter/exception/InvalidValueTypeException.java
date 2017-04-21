package com.naruto.mobile.base.Router.andRouter.exception;

/**
 */
public class InvalidValueTypeException extends RuntimeException{
    public InvalidValueTypeException(String path, String value){
        super(String.format("The type of the value is not match witch the path, Path : %s, value: %s", path, value));
    }

}
