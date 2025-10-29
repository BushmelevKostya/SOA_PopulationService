package itmo.populationservice.model.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "MovedPopulation")
@XmlAccessorType(XmlAccessType.FIELD)
public class MovedPopulationDto {
    @XmlValue
    private Long value;

    public MovedPopulationDto(Long movedCount) {
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
