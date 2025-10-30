package itmo.populationservice.client;

import itmo.populationservice.model.dto.CityCreateRequestDto;
import itmo.populationservice.model.dto.CityDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CityServiceClient {
    private final RestTemplate restTemplate;
    private final String cityServiceUrl;
    private final String cityServiceUser;
    private final String cityServicePassword;

    public CityServiceClient(RestTemplate restTemplate,
                             @Value("${city.service.url}") String cityServiceUrl,
                             @Value("${city.service.username}") String cityServiceUser,
                             @Value("${city.service.password}") String cityServicePassword)
    {
        this.restTemplate = restTemplate;
        this.cityServiceUrl = cityServiceUrl;
        this.cityServiceUser = cityServiceUser;
        this.cityServicePassword = cityServicePassword;
    }

    public CityDto getCityById(Long cityId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(cityServiceUser, cityServicePassword);
        headers.setContentType(MediaType.APPLICATION_XML);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                cityServiceUrl + "/cities/" + cityId,
                HttpMethod.GET,
                entity,
                CityDto.class).getBody();
    }

    public void updateCity(Long cityId, CityCreateRequestDto cityDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(cityServiceUser, cityServicePassword);
        headers.setContentType(MediaType.APPLICATION_XML);

        HttpEntity<CityCreateRequestDto> entity = new HttpEntity<>(cityDto, headers);

        restTemplate.exchange(
                cityServiceUrl + "/cities/" + cityId,
                HttpMethod.PUT,
                entity,
                Void.class);
    }
}
