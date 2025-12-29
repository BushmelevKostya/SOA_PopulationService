package itmo.populationservice.service;

import itmo.populationservice.client.CityServiceSoapClient;
import itmo.populationservice.exception.BadRequestException;
import itmo.populationservice.exception.NotFoundException;
import itmo.populationservice.exception.ServiceUnavailableException;
import itmo.populationservice.soap.City;
import itmo.populationservice.soap.Coordinates;
import itmo.populationservice.soap.Human;
import org.springframework.stereotype.Service;

@Service
public class PopulationService {
    private final CityServiceSoapClient cityServiceClient;

    public PopulationService(CityServiceSoapClient cityServiceClient) {
        this.cityServiceClient = cityServiceClient;
    }

    public Long calculateSum(Long id1, Long id2, Long id3) {
        validateIds(id1, id2, id3);

        try {
            City city1 = cityServiceClient.getCityById(id1);
            City city2 = cityServiceClient.getCityById(id2);
            City city3 = cityServiceClient.getCityById(id3);

            if (city1 == null || city2 == null || city3 == null) {
                throw new NotFoundException("Один или несколько городов не найдены");
            }

            return city1.getPopulation() + city2.getPopulation() + city3.getPopulation();
        } catch (Exception e) {
            throw new ServiceUnavailableException("Сервис недоступен");
        }
    }

    public Long deportPopulation(Long fromId, Long toId) {
        validateIds(fromId, toId);

        try {
            City fromCity = cityServiceClient.getCityById(fromId);
            City toCity = cityServiceClient.getCityById(toId);

            if (fromCity == null || toCity == null) {
                throw new NotFoundException("Один или оба города не найдены");
            }

            long movedCount = fromCity.getPopulation();

            City updateFrom = createCityCopy(fromCity);
            updateFrom.setPopulation(0L);

            City updateTo = createCityCopy(toCity);
            updateTo.setPopulation(toCity.getPopulation() + movedCount);

            cityServiceClient.updateCity(fromId, updateFrom);
            cityServiceClient.updateCity(toId, updateTo);

            return movedCount;
        } catch (Exception e) {
            throw new ServiceUnavailableException("Сервис недоступен");
        }
    }

    private void validateIds(Long... ids) {
        for (Long id : ids) {
            if (id == null || id < 1) {
                throw new BadRequestException("Неверные параметры запроса");
            }
        }
    }

    private City createCityCopy(City original) {
        City copy = new City();
        copy.setId(original.getId());
        copy.setName(original.getName());
        copy.setPopulation(original.getPopulation());

        if (original.getCoordinates() != null) {
            Coordinates coords = new Coordinates();
            coords.setX(original.getCoordinates().getX());
            coords.setY(original.getCoordinates().getY());
            copy.setCoordinates(coords);
        }

        if (original.getGovernor() != null) {
            Human governor = new Human();
            governor.setName(original.getGovernor().getName());
            governor.setAge(original.getGovernor().getAge());
            governor.setHeight(original.getGovernor().getHeight());
            governor.setBirthday(original.getGovernor().getBirthday());
            copy.setGovernor(governor);
        }

        copy.setCreationDate(original.getCreationDate());
        copy.setArea(original.getArea());
        copy.setMetersAboveSeaLevel(original.getMetersAboveSeaLevel());
        copy.setCarCode(original.getCarCode());
        copy.setClimate(original.getClimate());
        copy.setStandardOfLiving(original.getStandardOfLiving());

        return copy;
    }
}
