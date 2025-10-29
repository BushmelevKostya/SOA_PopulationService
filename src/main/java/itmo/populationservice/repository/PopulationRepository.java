package itmo.populationservice.repository;

import itmo.populationservice.model.entity.Population;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PopulationRepository extends JpaRepository<Population, Long> {
    Optional<Population> findByCityId(Long cityId);
}
