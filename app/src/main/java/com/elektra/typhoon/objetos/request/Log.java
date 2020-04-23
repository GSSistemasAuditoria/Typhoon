package com.elektra.typhoon.objetos.request;

public class Log {
    private String method;
    private int tType;
    private String trace;

    public Log() {
    }

    public Log(String method, int tType, String trace) {
        this.method = method;
        this.tType = tType;
        this.trace = trace;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int gettType() {
        return tType;
    }

    public void settType(int tType) {
        this.tType = tType;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }
}
