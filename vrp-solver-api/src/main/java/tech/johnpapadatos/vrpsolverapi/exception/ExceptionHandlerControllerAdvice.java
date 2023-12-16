package tech.johnpapadatos.vrpsolverapi.exception;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import tech.johnpapadatos.vrpsolverapi.exception.shemas.ExceptionResponseDTO;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
    // TODO: Send the logs to an error tracking tool.
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerControllerAdvice.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ExceptionResponseDTO handleMethodArgumentNotValidException(
        MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        String constraintViolation
            = methodArgumentNotValidException.getAllErrors()
                .stream()
                .map(c -> c.getDefaultMessage())
                .findFirst()
                .orElse("Bad Request");

        return new ExceptionResponseDTO(
            constraintViolation, 
            ZonedDateTime.now()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = BadRequestException.class)
    public ExceptionResponseDTO handleBadRequestException(
        BadRequestException badRequestException
    ) {
        return new ExceptionResponseDTO(
            badRequestException.getMessage(), 
            ZonedDateTime.now()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NotFoundException.class)
    public ExceptionResponseDTO handleNotFoundException(
        NotFoundException notFoundException
    ) {
        return new ExceptionResponseDTO(
            notFoundException.getMessage(), 
            ZonedDateTime.now()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = AlreadyExistsException.class)
    public ExceptionResponseDTO handleAlreadyExistsException(
        AlreadyExistsException alreadyExistsException
    ) {
        return new ExceptionResponseDTO(
            alreadyExistsException.getMessage(), 
            ZonedDateTime.now()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ExceptionResponseDTO handleUnexpectedExceptions(
        Exception exception
    ) {
        LOGGER.error(exception.getMessage(), exception);
        return new ExceptionResponseDTO(
            "An unexpected error occurred.", 
            ZonedDateTime.now()
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(value = BadGatewayException.class)
    public ExceptionResponseDTO handleBadGatewayException(
        BadGatewayException badGatewayException
    ) {
        LOGGER.error(badGatewayException.getMessage(), badGatewayException);
        return new ExceptionResponseDTO(
            badGatewayException.getMessage(), 
            ZonedDateTime.now()
        );
    }
}
