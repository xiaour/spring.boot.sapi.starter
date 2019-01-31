package com.github.xiaour.sapi.exception;

public class SimpleApiException extends  Exception{

    public SimpleApiException(String errMsg) {
        super(errMsg);
    }

    public SimpleApiException() {
        super();
    }

}
