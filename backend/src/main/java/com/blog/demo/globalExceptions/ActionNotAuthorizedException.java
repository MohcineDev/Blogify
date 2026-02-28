package com.blog.demo.globalExceptions;

public class ActionNotAuthorizedException extends RuntimeException {

    public ActionNotAuthorizedException(String errorMsg) {
        super(errorMsg);
    }
}
