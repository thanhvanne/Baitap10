package vn.hcmute.jwtnimbus.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {

        // 401
        if (exception instanceof BadCredentialsException ||
            exception instanceof UsernameNotFoundException ||
            exception instanceof JwtAuthenticationException) {

            ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.getMessage());
            error.setProperty("description", "Authentication failed / Invalid JWT");
            return error;
        }

        // 403
        if (exception instanceof AccountStatusException ||
            exception instanceof AccessDeniedException ||
            exception instanceof AuthorizationDeniedException) {

            ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
            error.setProperty("description", "Access denied");
            return error;
        }

        // 500
        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        error.setProperty("description", "Unknown internal server error.");
        return error;
    }
}