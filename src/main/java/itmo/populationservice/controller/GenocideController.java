package itmo.populationservice.controller;

import itmo.populationservice.client.CityServiceSoapClient;
import itmo.populationservice.exception.BadRequestException;
import itmo.populationservice.exception.ServiceUnavailableException;
import itmo.populationservice.service.PopulationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genocide")
@CrossOrigin(origins = "*")
public class GenocideController {

    private final PopulationService service;

    public GenocideController(PopulationService service) {
        this.service = service;
    }

    @GetMapping(value = "/count/{id1}/{id2}/{id3}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> calculatePopulationSum(
            @PathVariable Long id1,
            @PathVariable Long id2,
            @PathVariable Long id3) {

        validateIds(id1, id2, id3);

        try {
            Long sum = service.calculateSum(id1, id2, id3);
            String xmlResponse = String.format("<result>%d</result>", sum);
            return ResponseEntity.ok(xmlResponse);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Сервис недоступен: " + e.getMessage());
        }
    }

    @PostMapping(value = "/deport/{fromId}/{toId}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> deportPopulation(
            @PathVariable Long fromId,
            @PathVariable Long toId) {

        validateIds(fromId, toId);

        try {
            Long movedCount = service.deportPopulation(fromId, toId);
            String xmlResponse = String.format("<movedCount>%d</movedCount>", movedCount);
            return ResponseEntity.ok(xmlResponse);
        } catch (Exception e) {
            throw new ServiceUnavailableException("Сервис недоступен: " + e.getMessage());
        }
    }

    private void validateIds(Long... ids) {
        for (Long id : ids) {
            if (id == null || id < 1) {
                throw new BadRequestException("Неверные параметры запроса: ID должен быть положительным числом");
            }
        }
    }
}
