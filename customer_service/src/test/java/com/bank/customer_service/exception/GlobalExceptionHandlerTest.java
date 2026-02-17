package com.bank.customer_service.exception;

import com.bank.customer_service.constants.AppConstants;
import com.bank.customer_service.dto.response.BaseResponse;
import feign.FeignException;
import feign.Request;
import feign.Response;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private org.springframework.validation.method.MethodValidationResult methodValidationResult;

    @Mock
    private ConstraintViolation<Object> constraintViolation;

    @Mock
    private Path path;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleBusinessException_withErrorCode() {
        BusinessException ex = BusinessException.badRequest("Custom error with code");

        ResponseEntity<BaseResponse<Void>> response = handler.handleBusiness(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).contains("Custom error with code");
    }

    @Test
    void handleBusinessException_fallbackToStatusCode() {
        BusinessException ex = new BusinessException("No code provided", HttpStatus.BAD_REQUEST, null);

        ResponseEntity<BaseResponse<Void>> response = handler.handleBusiness(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo("No code provided");
    }

    @Test
    void handleNoResourceFound() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/test");
        ResponseEntity<BaseResponse<Void>> response = handler.handleNoResource(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo(AppConstants.NOT_FOUND_MSG);
    }


    @Test
    void handleConstraintViolation_withViolation() {
        when(constraintViolation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("user.name");
        when(constraintViolation.getMessage()).thenReturn("must not be blank");

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(constraintViolation));

        ResponseEntity<BaseResponse<Void>> response = handler.handleConstraint(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg())
                .contains("user.name: must not be blank"); // covers map(...) branch
    }

    @Test
    void handleConstraintViolation_empty() {
        ConstraintViolationException ex = new ConstraintViolationException(Set.of());

        ResponseEntity<BaseResponse<Void>> response = handler.handleConstraint(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo("Invalid input");
    }

    @Test
    void handleMethodValidation() {
        HandlerMethodValidationException ex = new HandlerMethodValidationException(methodValidationResult);

        ResponseEntity<BaseResponse<Void>> response = handler.handleMethodValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo(AppConstants.BAD_REQUEST_MSG);
    }

    @Test
    void handleMethodArgumentNotValid_withErrors() {
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("dto", "name", "must not be blank")));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<BaseResponse<Void>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg())
                .contains("must not be blank"); // covers errors.toString() branch
    }

    @Test
    void handleMethodArgumentNotValid_noErrors() {
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList());

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<BaseResponse<Void>> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo("Invalid request data"); // empty branch
    }

    @Test
    void handleMissingRequestParam() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("id", "String");
        ResponseEntity<BaseResponse<Void>> response = handler.handleMissingParam(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo(AppConstants.BAD_REQUEST_MSG);
    }
    @Test
    void handleNoHandlerFound() {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/missing", null);
        ResponseEntity<BaseResponse<Void>> response = handler.handleNoHandler(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo(AppConstants.NOT_FOUND_MSG);
    }

    @Test
    void handleFeignException() {
        Response feignResponse = Response.builder()
                .status(503)
                .reason("Service Down")
                .request(Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(), null, StandardCharsets.UTF_8, null))
                .build();

        FeignException ex = FeignException.errorStatus("feign", feignResponse);

        ResponseEntity<BaseResponse<Void>> response = handler.handleFeign(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo(AppConstants.SERVICE_UNAVAILABLE_MSG);
    }

    @Test
    void handleDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key");

        ResponseEntity<BaseResponse<Void>> response = handler.handleDBError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo("Database error: Duplicate or invalid data");
    }

    @Test
    void handleInvalidJson() {
        HttpInputMessage inputMessage = new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return InputStream.nullInputStream();
            }

            @Override
            public HttpHeaders getHeaders() {
                return new HttpHeaders();
            }
        };

        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
                "Invalid JSON", new IllegalArgumentException("cause"), inputMessage);

        ResponseEntity<BaseResponse<Void>> response = handler.handleInvalidJson(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo("Invalid JSON or enum value provided");
    }

    @Test
    void handleGenericException() {
        Exception ex = new RuntimeException("Boom");

        ResponseEntity<BaseResponse<Void>> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResultInfo()).isNotNull();
        assertThat(response.getBody().getResultInfo().getResultMsg()).isEqualTo(AppConstants.INTERNAL_ERROR_MSG);
    }
}