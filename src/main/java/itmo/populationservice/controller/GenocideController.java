package itmo.populationservice.controller;

import itmo.populationservice.service.PopulationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genocide")
public class GenocideController {
    private final PopulationService populationService;

    public GenocideController(PopulationService populationService) {
        this.populationService = populationService;
    }

    @GetMapping(
            value = "/count/{id1}/{id2}/{id3}",
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<Long> calculateSum(
            @PathVariable("id1") Long id1,
            @PathVariable("id2") Long id2,
            @PathVariable("id3") Long id3) {

        Long result = populationService.calculateSum(id1, id2, id3);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(
            value = "/deport/{id-from}/{id-to}",
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<Long> deportPopulation(
            @PathVariable("id-from") Long fromId,
            @PathVariable("id-to") Long toId) {

        Long result = populationService.deportPopulation(fromId, toId);
        return ResponseEntity.ok(result);
    }
}
