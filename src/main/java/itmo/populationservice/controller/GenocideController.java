package itmo.populationservice.controller;

import itmo.populationservice.service.PopulationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
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
        return ResponseEntity.ok(populationService.calculateSum(id1, id2, id3));
    }

    @PostMapping(
            value = "/deport/{id-from}/{id-to}",
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<Long> deportPopulation(
            @PathVariable("id-from") Long fromId,
            @PathVariable("id-to") Long toId) {
        return ResponseEntity.ok(populationService.deportPopulation(fromId, toId));
    }
}
