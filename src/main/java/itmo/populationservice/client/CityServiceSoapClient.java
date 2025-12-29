package itmo.populationservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import itmo.populationservice.soap.*;
import itmo.populationservice.exception.ServiceFault;
import itmo.populationservice.exception.ServiceFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CityServiceSoapClient {
    private static final Logger log = LoggerFactory.getLogger(CityServiceSoapClient.class);
    private final WebServiceTemplate webServiceTemplate;
    private final String cityServiceUrl;

    public CityServiceSoapClient(WebServiceTemplate webServiceTemplate,
                                 @Value("${city.service.soap.url}") String cityServiceUrl) {
        this.webServiceTemplate = webServiceTemplate;
        this.cityServiceUrl = cityServiceUrl;
    }

    public City getCityById(Long cityId) {
        try {
            GetCityRequest request = new GetCityRequest();
            request.setCityId(cityId);

            log.info("Getting city by id: {}", cityId);

            GetCityResponse response = (GetCityResponse) webServiceTemplate.marshalSendAndReceive(
                    cityServiceUrl + "/cities/" + cityId,
                    request
            );

            return response.getCity();

        } catch (SoapFaultClientException e) {
            log.error("SOAP Fault when getting city {}: {}", cityId, e.getFaultStringOrReason());
            handleSoapFault(e);
            return null;
        } catch (Exception e) {
            log.error("Error when getting city by id: {}", cityId, e);
            throw new ServiceFaultException("Error", new ServiceFault("500", "Error getting city: " + e.getMessage()));
        }
    }

    public void updateCity(Long cityId, City cityData) {
        try {
            UpdateCityRequest request = new UpdateCityRequest();
            request.setCityId(cityId);
            request.setCityData(cityData);

            log.info("Updating city id: {}", cityId);

            webServiceTemplate.marshalSendAndReceive(
                    cityServiceUrl + "/cities/" + cityId + "/update",
                    request
            );

            log.info("City updated successfully: {}", cityId);

        } catch (SoapFaultClientException e) {
            log.error("SOAP Fault when updating city {}: {}", cityId, e.getFaultStringOrReason());
            handleSoapFault(e);
        } catch (Exception e) {
            log.error("Error when updating city: {}", cityId, e);
            throw new ServiceFaultException("Error", new ServiceFault("500", "Error updating city: " + e.getMessage()));
        }
    }

    private void handleSoapFault(SoapFaultClientException e) {
        String faultCode = e.getFaultCode().getLocalPart();
        String faultString = e.getFaultStringOrReason();

        log.warn("SOAP Fault - Code: {}, Message: {}", faultCode, faultString);

        switch (faultCode) {
            case "Client.NotFound":
                throw new ServiceFaultException("NotFound", new ServiceFault("404", "City not found: " + faultString));
            case "Client.BadRequest":
                throw new ServiceFaultException("BadRequest", new ServiceFault("400", "Bad request: " + faultString));
            case "Server.ServiceUnavailable":
                throw new ServiceFaultException("ServiceUnavailable", new ServiceFault("503", "Service unavailable: " + faultString));
            default:
                throw new ServiceFaultException("Error", new ServiceFault("500", "SOAP error: " + faultString));
        }
    }
}
