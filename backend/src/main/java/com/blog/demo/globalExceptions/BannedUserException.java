package com.blog.demo.globalExceptions;

public class BannedUserException extends RuntimeException {

    public BannedUserException() {
        super("Account Banned!");
    }

}
