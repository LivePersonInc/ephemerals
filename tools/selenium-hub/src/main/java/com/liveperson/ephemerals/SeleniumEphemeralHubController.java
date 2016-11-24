package com.liveperson.ephemerals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesEphemeral;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by waseemh on 11/24/16.
 */
@RestController
public class SeleniumEphemeralHubController {

    @RequestMapping("/wd/hub")
    public String newSession(Map<String, Object> capabilitiesMap) {

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(capabilitiesMap);

        SeleniumEphemeral seleniumEphemeral =
                new SeleniumEphemeral.Builder(KubernetesEphemeral.create(null,null,null))
                .withDesiredCapabilities(desiredCapabilities)
                .build();

        RemoteWebDriver remoteWebDriver = seleniumEphemeral.get();
        try {
            String json = new ObjectMapper().writeValueAsString(remoteWebDriver.getCapabilities().asMap());
            return json;
        } catch (JsonProcessingException e) {
            return null;
        }

    }

}