package com.cluborg.coreplayerservice.validator;

import com.cluborg.coreplayerservice.exceptions.PlayerValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public abstract class AbstractValidationHandler<T, U extends Validator> {

    private final Class<T> validationClass;

    private final U validator;

    protected AbstractValidationHandler(Class<T> clazz, U validator) {
        this.validationClass = clazz;
        this.validator = validator;
    }

    abstract protected Mono<ServerResponse> processPostBody(T validBody, final ServerRequest originalRequest);
    abstract protected Mono<ServerResponse> updatePlayer(T validBody, final ServerRequest originalRequest);

    public final Mono<ServerResponse> handleRequest(final ServerRequest request) {
        return request.bodyToMono(this.validationClass)
                .flatMap(body -> {
                    Errors errors = new BeanPropertyBindingResult(body, this.validationClass.getName());
                    this.validator.validate(body, errors);

                    if (errors.getAllErrors()
                            .isEmpty()) {
                        switch (request.method()){
                            case POST:
                                return processPostBody(body, request);
                            case PUT:
                                return updatePlayer(body, request);
                                default:
                                    return processPostBody(body, request);
                        }
                    } else {
                        return onValidationErrors(errors.getFieldErrors(), body, request);
                    }
                });
    }

    protected Mono<ServerResponse> onValidationErrors(List<FieldError> errors, T invalidBody, final ServerRequest request) {
        throw new PlayerValidationException(HttpStatus.BAD_REQUEST, errors.get(0).getDefaultMessage());
    }
}
