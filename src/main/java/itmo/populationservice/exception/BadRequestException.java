package itmo.populationservice.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT, faultStringOrReason = "Ошибка в url запроса")
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
