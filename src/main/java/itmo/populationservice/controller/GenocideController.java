package itmo.populationservice.controller;

import itmo.populationservice.exception.BadRequestException;
import itmo.populationservice.exception.NotFoundException;
import itmo.populationservice.model.dto.MovedPopulationDto;
import itmo.populationservice.model.dto.PopulationSumDto;
import itmo.populationservice.service.PopulationService;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/genocide")
@XmlRootElement
public class GenocideController {
    private final PopulationService populationService;

    public GenocideController(PopulationService populationService) {
        this.populationService = populationService;
    }

    @GetMapping(value = "/count/{id1}/{id2}/{id3}",
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> getPopulationSum(
            @PathVariable Long id1,
            @PathVariable Long id2,
            @PathVariable Long id3) {
        try {
            Long sum = populationService.calculateSum(id1, id2, id3);
            return ResponseEntity.ok(new PopulationSumDto(sum));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/deport/{idFrom}/{idTo}",
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> deportPopulation(
            @PathVariable("idFrom") Long idFrom,
            @PathVariable("idTo") Long idTo) {
        try {
            Long movedCount = populationService.deportPopulation(idFrom, idTo);
            return ResponseEntity.ok(new MovedPopulationDto(movedCount));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
