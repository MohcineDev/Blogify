package com.blog.demo.globalExceptions;

public class EntityNotFoundException extends RuntimeException{
    public  EntityNotFoundException(String msg){
        super(msg);
    }
}
