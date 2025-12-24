package itmo.populationservice.client;

import  itmo.populationservice.exception.BadRequestException;
import itmo.populationservice.exception.NotFoundException;
import itmo.populationservice.exception.ServiceUnavailableException;
import itmo.populationservice.model.dto.CityCreateRequestDto;
import itmo.populationservice.model.dto.CityDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class CityServiceClient {
    private final RestTemplate restTemplate;
    private String cityServiceUrl;
    private final String cityServiceUser;
    private final String cityServicePassword;

    public CityServiceClient(RestTemplate restTemplate,
                             @Value("${city.service.url}") String cityServiceUrl,
                             @Value("${city.service.username}") String cityServiceUser,
                             @Value("${city.service.password}") String cityServicePassword) {
        this.restTemplate = restTemplate;
        this.cityServiceUrl = cityServiceUrl;
        this.cityServiceUser = cityServiceUser;
        this.cityServicePassword = cityServicePassword;
    }

    public CityDto getCityById(Long cityId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(cityServiceUser, cityServicePassword);
            headers.setContentType(MediaType.APPLICATION_XML);
            cityServiceUrl = "http://127.0.0.1:8080/api/v1/";

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<CityDto> response = restTemplate.exchange(
                    cityServiceUrl + "/cities/" + cityId,
                    HttpMethod.GET,
                    entity,
                    CityDto.class);

            return response.getBody();

        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Город с указанным ID не найден в CityService");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadRequestException("Неверный ID города");
        } catch (HttpServerErrorException.ServiceUnavailable | ResourceAccessException e) {
            throw new ServiceUnavailableException("Сервис недоступен");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обращении к CityService: " + e.getMessage());
        }
    }

    public void updateCity(Long cityId, CityCreateRequestDto cityDto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(cityServiceUser, cityServicePassword);
            headers.setContentType(MediaType.APPLICATION_XML);

            HttpEntity<CityCreateRequestDto> entity = new HttpEntity<>(cityDto, headers);

            restTemplate.exchange(
                    cityServiceUrl + "/cities/" + cityId,
                    HttpMethod.PUT,
                    entity,
                    Void.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Город с указанным ID не найден в CityService");
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadRequestException("Неверный ID города");
        } catch (HttpServerErrorException.ServiceUnavailable | ResourceAccessException e) {
            throw new ServiceUnavailableException("Сервис недоступен");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обращении к CityService: " + e.getMessage());
        }
    }

}
