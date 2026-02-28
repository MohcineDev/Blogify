package com.blog.demo.globalExceptions;

public class HandleFileException extends RuntimeException{
    public  HandleFileException(String msg){
        super(msg);
    }
}
