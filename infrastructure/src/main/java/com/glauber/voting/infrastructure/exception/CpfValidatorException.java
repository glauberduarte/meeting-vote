package com.glauber.voting.infrastructure.exception;

public class CpfValidatorException extends RuntimeException {

    private final String errorCode;
    private final Object[] args;

    public CpfValidatorException(String errorCode, String defaultMessage, Object... args) {
        super(defaultMessage); // Mensagem padrão em inglês/português caso falte tradução
        this.errorCode = errorCode;
        this.args = args;
    }

    public String getErrorCode() { return errorCode; }
    public Object[] getArgs() { return args; }

}