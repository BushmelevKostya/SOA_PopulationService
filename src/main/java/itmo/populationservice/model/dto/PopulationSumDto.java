package itmo.populationservice.model.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;

@XmlRootElement(name = "PopulationSum")
@XmlAccessorType(XmlAccessType.FIELD)
public class PopulationSumDto {
    @XmlValue
    private Long value;

    public PopulationSumDto(Long sum) {
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
