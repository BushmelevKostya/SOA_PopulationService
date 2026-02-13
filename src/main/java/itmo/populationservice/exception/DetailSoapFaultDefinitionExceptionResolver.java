package itmo.populationservice.exception;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import javax.xml.namespace.QName;

public class DetailSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {

    private static final QName CODE = new QName("code");
    private static final QName DESCRIPTION = new QName("description");

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        logger.warn("Exception processed: " + ex.getClass().getName() + " - " + ex.getMessage());

        String code = "500";
        String description = "Непредвиденная ошибка";

        if (ex instanceof ServiceFaultException) {
            ServiceFault serviceFault = ((ServiceFaultException) ex).getServiceFault();
            code = serviceFault.getCode();
            description = serviceFault.getDescription();

            logger.info("ServiceFaultException - Code: {}, Description: {}" + code + " " + description);

        } else if (ex instanceof NotFoundException) {
            code = "404";
            description = ex.getMessage();
        } else if (ex instanceof BadRequestException) {
            code = "400";
            description = ex.getMessage();
        } else if (ex instanceof ServiceUnavailableException) {
            code = "503";
            description = ex.getMessage();
        }

        SoapFaultDetail detail = fault.addFaultDetail();
        detail.addFaultDetailElement(CODE).addText(code);
        detail.addFaultDetailElement(DESCRIPTION).addText(description);

        logger.info("SOAP Fault detail added - Code: {}, Description: {}" + code + " " + description);
    }
}
