package com.liveperson.ephemerals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesEphemeral;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by waseemh on 11/24/16.
 *
 */

@RestController
@RequestMapping("/wd/hub")
public class SeleniumEphemeralHubController {

    @RequestMapping(method= RequestMethod.POST, value="/session")
    public String newSession(@RequestBody String desiredCapabilities) throws IOException {

        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

        HashMap<String,Object> capsMap = new ObjectMapper().readValue(desiredCapabilities,typeRef);
        Map capsMapFinal = (Map) capsMap.get("desiredCapabilities");
        DesiredCapabilities desiredCapabilitiesObj = new DesiredCapabilities(capsMapFinal);

        SeleniumEphemeral seleniumEphemeral =
                new SeleniumEphemeral.Builder(KubernetesEphemeral
                        .create("https://23.236.58.203","admin","lRNoBEIyPZGRC2jW"))
                .withDesiredCapabilities(desiredCapabilitiesObj)
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