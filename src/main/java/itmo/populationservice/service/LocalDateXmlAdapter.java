package itmo.populationservice.service;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        return LocalDate.parse(v, dateFormat);
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return v.format(dateFormat);
    }
}
