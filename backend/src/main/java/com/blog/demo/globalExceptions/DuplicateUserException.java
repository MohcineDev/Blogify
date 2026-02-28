package com.blog.demo.globalExceptions;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String msg) {

        super(msg);
    }
}
