package itmo.populationservice.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "population_data")
public class Population {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_id", unique = true)
    private Long cityId;

    @Column(nullable = false)
    private Long count;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
