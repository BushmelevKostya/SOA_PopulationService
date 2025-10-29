package itmo.populationservice.service;

import itmo.populationservice.exception.NotFoundException;
import itmo.populationservice.model.entity.Population;
import itmo.populationservice.repository.PopulationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PopulationService {
    private final PopulationRepository populationRepository;

    public PopulationService(PopulationRepository populationRepository) {
        this.populationRepository = populationRepository;
    }

    @Transactional
    public Long calculateSum(Long id1, Long id2, Long id3) {
        return getPopulationCount(id1) +
                  getPopulationCount(id2) +
                  getPopulationCount(id3);
    }

    @Transactional
    public Long deportPopulation(Long fromId, Long toId) {
        Population from = populationRepository.findByCityId(fromId)
                .orElseThrow(() -> new NotFoundException("Город-источник не найден"));
        Population to = populationRepository.findByCityId(toId)
                .orElseThrow(() -> new NotFoundException("Город-назначение не найден"));

        Long movedCount = from.getCount();
        to.setCount(to.getCount() + movedCount);
        from.setCount(0L);

        populationRepository.save(to);
        populationRepository.save(from);

        return movedCount;
    }

    private Long getPopulationCount(Long cityId) {
        return populationRepository.findByCityId(cityId)
                .orElseThrow(() -> new NotFoundException("Город с id " + cityId + " не найден"))
                .getCount();
    }
}
