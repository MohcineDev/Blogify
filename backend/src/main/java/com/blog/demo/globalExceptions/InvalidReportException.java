package com.blog.demo.globalExceptions;

public class InvalidReportException extends RuntimeException{
    public  InvalidReportException(String msg){
        super(msg);
    }
}
