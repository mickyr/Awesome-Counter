package com.github.mickyr.acounter.core;

public enum CounterResetPolicy{
    YEARLY("yyyy"),
    MONTHLY("yyyy/MM"),
    DAILY("yyyy/MM/dd"),
    HOURLY("yyyy/MM/dd hh"),
    PERMINUTE("yyyy/MM/dd hh:mm"),
    PERSECOND("yyyy/MM/dd hh:mm:ss"),
    MANUAL("");

    private String format;

    private CounterResetPolicy(String format) {
        this.format = format;
    }

    public String getFormat(){
        return this.format;
    }
}