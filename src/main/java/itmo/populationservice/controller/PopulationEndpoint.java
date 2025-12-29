package itmo.populationservice.controller;

import itmo.populationservice.client.CityServiceSoapClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import itmo.populationservice.service.PopulationService;
import itmo.populationservice.soap.*;
import itmo.populationservice.exception.ServiceFault;
import itmo.populationservice.exception.ServiceFaultException;

@Endpoint
public class PopulationEndpoint {
    private static final Logger log = LoggerFactory.getLogger(CityServiceSoapClient.class);
    private static final String NAMESPACE_URI = "http://populationapi.com/soap";
    private final PopulationService populationService;

    @Autowired
    public PopulationEndpoint(PopulationService populationService) {
        this.populationService = populationService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CalculatePopulationSumRequest")
    @ResponsePayload
    public CalculatePopulationSumResponse calculatePopulationSum(@RequestPayload CalculatePopulationSumRequest request) {

        log.info("Calculating population sum for cities: {}, {}, {}",
                request.getCityId1(), request.getCityId2(), request.getCityId3());

        // Валидация параметров согласно OpenAPI (minimum: 1)
        if (request.getCityId1() < 1 || request.getCityId2() < 1 || request.getCityId3() < 1) {
            log.warn("Invalid city IDs: {}, {}, {}",
                    request.getCityId1(), request.getCityId2(), request.getCityId3());
            throw new ServiceFaultException("BadRequest", new ServiceFault("400", "Ошибка в url запроса"));
        }

        try {
            long sum = populationService.calculateSum(
                    request.getCityId1(),
                    request.getCityId2(),
                    request.getCityId3()
            );

            CalculatePopulationSumResponse response = new CalculatePopulationSumResponse();
            response.setPopulationSum(sum);

            log.info("Population sum calculated: {}", sum);
            return response;

        } catch (itmo.populationservice.exception.NotFoundException e) {
            log.warn("Cities not found when calculating sum: {}", e.getMessage());
            throw new ServiceFaultException("NotFound", new ServiceFault("404", "Объект не найден"));
        } catch (itmo.populationservice.exception.ServiceUnavailableException e) {
            log.error("Service unavailable when calculating sum: {}", e.getMessage());
            throw new ServiceFaultException("ServiceUnavailable", new ServiceFault("503", "Сервис недоступен"));
        } catch (Exception e) {
            log.error("Unexpected error calculating population sum", e);
            throw new ServiceFaultException("Error", new ServiceFault("500", "Непридвиденная ошибка"));
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeportPopulationRequest")
    @ResponsePayload
    public DeportPopulationResponse deportPopulation(@RequestPayload DeportPopulationRequest request) {

        log.info("Deporting population from city {} to city {}",
                request.getSourceCityId(), request.getTargetCityId());

        if (request.getSourceCityId() < 1 || request.getTargetCityId() < 1) {
            log.warn("Invalid city IDs for deportation: from {}, to {}",
                    request.getSourceCityId(), request.getTargetCityId());
            throw new ServiceFaultException("BadRequest", new ServiceFault("400", "Ошибка в url запроса"));
        }

        try {
            long movedPopulation = populationService.deportPopulation(
                    request.getSourceCityId(),
                    request.getTargetCityId()
            );

            DeportPopulationResponse response = new DeportPopulationResponse();
            response.setMovedPopulation(movedPopulation);

            log.info("Population deported: {}", movedPopulation);
            return response;

        } catch (itmo.populationservice.exception.NotFoundException e) {
            log.warn("Cities not found when deporting population: {}", e.getMessage());
            throw new ServiceFaultException("NotFound", new ServiceFault("404", "Объект не найден"));
        } catch (itmo.populationservice.exception.ServiceUnavailableException e) {
            log.error("Service unavailable when deporting population: {}", e.getMessage());
            throw new ServiceFaultException("ServiceUnavailable", new ServiceFault("503", "Сервис недоступен"));
        } catch (Exception e) {
            log.error("Unexpected error deporting population", e);
            throw new ServiceFaultException("Error", new ServiceFault("500", "Непридвиденная ошибка"));
        }
    }
}
