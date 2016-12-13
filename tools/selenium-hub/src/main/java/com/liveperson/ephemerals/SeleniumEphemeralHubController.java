package com.liveperson.ephemerals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesEphemeral;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    Map<String,SeleniumEphemeral> seleniumEphemerals = new HashMap<>();

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
        seleniumEphemerals.put(remoteWebDriver.getSessionId().toString(),seleniumEphemeral);
        try {
            String json = new ObjectMapper().writeValueAsString(remoteWebDriver.getCapabilities().asMap());
            return json;
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    @RequestMapping(value="/session/:sessionId/**")
    public String sessionOperation(String sessionId, @RequestBody String body, HttpMethod method, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {

        seleniumEphemerals.get(sessionId);
        URI uri = new URI("http", null, null, 80, request.getRequestURI(), request.getQueryString(), null);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity =
                restTemplate.exchange(uri, method, new HttpEntity<String>(body), String.class);

        return responseEntity.getBody();
    }

}