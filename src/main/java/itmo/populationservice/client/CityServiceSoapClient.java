package itmo.populationservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.WebServiceTransportException;
import itmo.populationservice.soap.*;
import itmo.populationservice.exception.ServiceFault;
import itmo.populationservice.exception.ServiceFaultException;
import itmo.populationservice.exception.NotFoundException;
import itmo.populationservice.exception.BadRequestException;
import itmo.populationservice.exception.ServiceUnavailableException;

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
                    cityServiceUrl + "/soap/cities/" + cityId,
                    request
            );

            return response.getCity();

        } catch (SoapFaultClientException e) {
            log.error("SOAP Fault when getting city {}: {}", cityId, e.getFaultStringOrReason());
            handleSoapFault(e);
            return null;
        } catch (WebServiceTransportException e) {
            // HTTP errors (404, 500, 503 и т.д.)
            log.error("City Service transport error when getting city {}: {}", cityId, e.getMessage());
            handleTransportException(e);
            return null;
        } catch (WebServiceIOException e) {
            // Ошибки подключения, таймауты
            log.error("City Service IO error when getting city {}: {}", cityId, e.getMessage());
            throw new ServiceUnavailableException("City Service недоступен");
        }  catch (Exception e) {
            log.error("Unexpected error when getting city by id: {}", cityId, e);
            throw new ServiceUnavailableException("City Service недоступен");
        }
    }

    public void updateCity(Long cityId, City cityData) {
        try {
            UpdateCityRequest request = new UpdateCityRequest();
            request.setCityId(cityId);
            request.setCityData(cityData);

            log.info("Updating city id: {}", cityId);

            webServiceTemplate.marshalSendAndReceive(
                    cityServiceUrl + "/soap/cities/" + cityId + "/update",
                    request
            );

            log.info("City updated successfully: {}", cityId);

        } catch (SoapFaultClientException e) {
            log.error("SOAP Fault when updating city {}: {}", cityId, e.getFaultStringOrReason());
            handleSoapFault(e);
        } catch (WebServiceTransportException e) {
            log.error("City Service transport error when updating city {}: {}", cityId, e.getMessage());
            handleTransportException(e);
        } catch (WebServiceIOException e) {
            log.error("City Service IO error when updating city {}: {}", cityId, e.getMessage());
            throw new ServiceUnavailableException("City Service недоступен");
        } catch (Exception e) {
            log.error("Unexpected error when updating city: {}", cityId, e);
            throw new ServiceUnavailableException("City Service недоступен");
        }
    }

    private void handleSoapFault(SoapFaultClientException e) {
        String faultCode = e.getFaultCode() != null ? e.getFaultCode().getLocalPart() : "Unknown";
        String faultString = e.getFaultStringOrReason();

        log.warn("SOAP Fault - Code: {}, Message: {}", faultCode, faultString);

        switch (faultCode) {
            case "Client.NotFound":
                throw new NotFoundException("Город не найден: " + faultString);
            case "Client.BadRequest":
                throw new BadRequestException("Неверный запрос: " + faultString);
            case "Server.ServiceUnavailable":
                throw new ServiceUnavailableException("City Service недоступен: " + faultString);
            default:
                throw new ServiceUnavailableException("City Service недоступен: " + faultString);
        }
    }

    private void handleTransportException(WebServiceTransportException e) {
        String message = e.getMessage();
        log.debug("Transport exception message: {}", message);

        if (message != null) {
            if (message.contains("404") || message.contains("Not Found")) {
                throw new NotFoundException("Город не найден");
            } else if (message.contains("400") || message.contains("Bad Request")) {
                throw new BadRequestException("Неверный запрос");
            } else if (message.contains("503") || message.contains("Service Unavailable")) {
                throw new ServiceUnavailableException("City Service недоступен");
            } else if (message.contains("500") || message.contains("Internal Server Error") ||
                    message.contains("Server Error")) {
                throw new ServiceUnavailableException("City Service недоступен");
            }
        }

        // Для всех остальных транспортных ошибок
        throw new ServiceUnavailableException("City Service недоступен: " + message);
    }
}
