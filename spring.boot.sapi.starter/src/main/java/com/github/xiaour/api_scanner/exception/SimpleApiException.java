package com.github.xiaour.api_scanner.exception;

public class SimpleApiException extends  Exception{

    public SimpleApiException(String errMsg) {
        super(errMsg);
    }

    public SimpleApiException() {
        super();
    }

}
