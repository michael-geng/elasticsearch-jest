package com.zhuanche.es.jest;

public class ESServiceException extends RuntimeException{
    private String status = "0";
    private static final long serialVersionUID = 1401593546385403720L;

    public ESServiceException(String message) {
        super(message);
    }

    public ESServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
