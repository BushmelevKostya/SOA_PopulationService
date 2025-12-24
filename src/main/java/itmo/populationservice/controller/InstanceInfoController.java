package itmo.populationservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class InstanceInfoController {

    @Value("${server.port}")
    private String port;

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    private RestTemplate loadBalancedRestTemplate;

    @GetMapping("/instance-info")
    public Map<String, String> getInternalInstanceInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("instance", "Population-Service-" + port);
        info.put("port", port);
        System.out.println("📍 Request handled by instance: " + port);
        return info;
    }

}
