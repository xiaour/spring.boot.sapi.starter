package com.github.xiaour.api_scanner.exception;

/**
 * @Author: Xiaour
 * @Description:
 * @Date: 2018/6/1 16:56
 */
public class SimpleApiException extends  Exception{

    public SimpleApiException(String errMsg) {
        super(errMsg);
    }

    public SimpleApiException() {
        super();
    }

}
