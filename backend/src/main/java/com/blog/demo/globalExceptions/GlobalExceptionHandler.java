package com.blog.demo.globalExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice // This tells Spring that this class will handle exceptions for all your
// Controllers
public class GlobalExceptionHandler {

    // This targets the specific error thrown by the @Valid annotation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        // reason
        String errMsg = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errMsg);
        detail.setTitle("validaiton failed");
        return detail;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        detail.setProperty("description", "id not found");
        return detail;
    }

    @ExceptionHandler({ BadCredentialsException.class, DisabledException.class })
    public ProblemDetail handleAuthenticationError(Exception ex) {
        // 401 login failed
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ProblemDetail handleSecurityException(SecurityException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        detail.setProperty("description", "you are not AUTHORIZED to do this process");

        return detail;
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ProblemDetail handleSecurityException(AuthorizationDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        detail.setProperty("description", "you are not AUTHORIZED to do this action");

        return detail;
    }

    @ExceptionHandler(BannedUserException.class)
    public ProblemDetail handleForbidenException(BannedUserException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        return detail;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleUserNNOtFUOndException(EntityNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return detail;
    }
 

    @ExceptionHandler(ActionNotAuthorizedException.class)
    public ProblemDetail handleActionNotAuthorizedException(ActionNotAuthorizedException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        return detail;
    }

    @ExceptionHandler(InvalidReportException.class)
    public ProblemDetail handleInvalidReportException(InvalidReportException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return detail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleActionNotAuthorizedException(MethodArgumentTypeMismatchException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return detail;
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ProblemDetail handleduplicateUserException(DuplicateUserException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        return detail;
    }
 

    @ExceptionHandler(HandleFileException.class)
    public ProblemDetail handleHandleFileException(HandleFileException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return detail;
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRunTimeException(RuntimeException ex) {
        // for report reason post / user

        if (ex.getMessage() != null
                && (ex.getMessage().contains("reason")
                        || ex.getMessage().equals("user not found")
                        || ex.getMessage().contains("notif not found")
                        || ex.getMessage().contains("subscribe") 
                    )
                    ) {
           return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
             
        }

        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setProperty("detail", " error");
        return detail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMissingBody(HttpMessageNotReadableException ex) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        // myMap.put("error", "req body is missing or invalid ");
        return detail;
    }

}
