package com.liveperson.ephemerals;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * Created by waseemh on 11/24/16
 *
 */
public class SeleniumEphemeralHubTest {

    @Test
    public void test() throws MalformedURLException {
        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(new URL("http://localhost:8080/wd/hub"), DesiredCapabilities.firefox());
        remoteWebDriver.get("http://google.com");
    }

}
