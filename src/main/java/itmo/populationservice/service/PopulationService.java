package itmo.populationservice.service;

import itmo.populationservice.client.CityServiceClient;
import itmo.populationservice.exception.BadRequestException;
import itmo.populationservice.exception.NotFoundException;
import itmo.populationservice.exception.ServiceUnavailableException;
import itmo.populationservice.mapper.CityMapper;
import itmo.populationservice.model.dto.CityCreateRequestDto;
import itmo.populationservice.model.dto.CityDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

@Service
public class PopulationService {
    private final CityServiceClient cityServiceClient;
    private final CityMapper cityMapper;

    public PopulationService(CityServiceClient cityServiceClient, CityMapper cityMapper) {
        this.cityServiceClient = cityServiceClient;
        this.cityMapper = cityMapper;
    }

    public Long calculateSum(Long id1, Long id2, Long id3) {
        validateIds(id1, id2, id3);

        try {
            CityDto city1 = cityServiceClient.getCityById(id1);
            CityDto city2 = cityServiceClient.getCityById(id2);
            CityDto city3 = cityServiceClient.getCityById(id3);

            return (long) (city1.getPopulation() + city2.getPopulation() + city3.getPopulation());
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("Сервис недоступен");
        }
    }

    public Long deportPopulation(Long fromId, Long toId) {
        validateIds(fromId, toId);

        CityDto fromCityDto = cityServiceClient.getCityById(fromId);
        CityDto toCityDto = cityServiceClient.getCityById(toId);

        if (fromCityDto == null || toCityDto == null) {
            throw new NotFoundException("Один или оба города не найдены");
        }

        Long movedCount = Long.valueOf(fromCityDto.getPopulation());

        CityCreateRequestDto updateFrom = cityMapper.toCreateRequestDto(fromCityDto);
        updateFrom.setPopulation(0);

        CityCreateRequestDto updateTo = cityMapper.toCreateRequestDto(toCityDto);
        updateTo.setPopulation(toCityDto.getPopulation() + movedCount.intValue());

        cityServiceClient.updateCity(fromId, updateFrom);
        cityServiceClient.updateCity(toId, updateTo);

        return movedCount;
    }

    private void validateIds(Long... ids) {
        for (Long id : ids) {
            if (id == null || id < 1) {
                throw new BadRequestException("Неверный ID города");
            }
        }
    }
}
