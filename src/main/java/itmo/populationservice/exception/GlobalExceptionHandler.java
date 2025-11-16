package itmo.populationservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(itmo.populationservice.exception.NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(itmo.populationservice.exception.NotFoundException ex) {
        return buildXmlResponse("Объект не найден", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(itmo.populationservice.exception.BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(itmo.populationservice.exception.BadRequestException ex) {
        return buildXmlResponse("Ошибка в url запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailable(ServiceUnavailableException ex) {
        return buildXmlResponse("Сервис недоступен", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return buildXmlResponse("Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> buildXmlResponse(String message, HttpStatus status) {
        String body = "<message>" + message + "</message>";
        return ResponseEntity.status(status)
                .header("Content-Type", "text/plain;charset=UTF-8")
                .body(body);
    }
}
