package itmo.populationservice.exception;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import javax.xml.namespace.QName;

public class ServiceFaultException extends RuntimeException {
    private ServiceFault serviceFault;

    public ServiceFaultException(String message, ServiceFault serviceFault) {
        super(message);
        this.serviceFault = serviceFault;
    }

    public ServiceFaultException(String message, Throwable cause, ServiceFault serviceFault) {
        super(message, cause);
        this.serviceFault = serviceFault;
    }

    public ServiceFault getServiceFault() {
        return serviceFault;
    }
}
