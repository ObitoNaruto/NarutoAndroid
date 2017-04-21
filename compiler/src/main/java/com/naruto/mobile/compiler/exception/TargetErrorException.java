package com.naruto.mobile.compiler.exception;

public class TargetErrorException extends Exception {
    public TargetErrorException(){
        super("Annotated target error, it should annotate only class");
    }
}