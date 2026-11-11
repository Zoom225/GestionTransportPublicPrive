package com.gestion.GestionTransport.sitetransport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Traduction des erreurs de l'API des sites, sans divulguer les causes techniques. */
@RestControllerAdvice(assignableTypes = SiteTransportController.class)
public class SiteTransportHttpExceptionHandler {
    @ExceptionHandler(SiteTransportException.class)
    ResponseEntity<ProblemDetail> metier(SiteTransportException exception) {
        var status = exception.getCode() == SiteTransportException.Code.SITE_TRANSPORT_INTROUVABLE
                ? HttpStatus.NOT_FOUND : HttpStatus.CONFLICT;
        return erreur(status, exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    ResponseEntity<ProblemDetail> requeteInvalide(Exception exception) {
        return erreur(HttpStatus.BAD_REQUEST, "La requête contient des données invalides.");
    }

    private ResponseEntity<ProblemDetail> erreur(HttpStatus status, String detail) {
        var problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        return ResponseEntity.status(status).body(problem);
    }
}
