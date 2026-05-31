package com.glauber.voting.application.exception;

public class SessionException extends RuntimeException {

    private final String errorCode;
    private final Object[] args;

    public SessionException(String errorCode, String defaultMessage, Object... args) {
        super(defaultMessage);
        this.errorCode = errorCode;
        this.args = args;
    }

    public String getErrorCode() { return errorCode; }
    public Object[] getArgs() { return args; }
}

