package com.liveperson.ephemerals;

import com.liveperson.ephemerals.deploy.*;
import com.liveperson.ephemerals.deploy.probe.HttpProbe;
import com.liveperson.ephemerals.deploy.unit.DeploymentUnit;
import com.liveperson.ephemerals.deploy.unit.DockerDeploymentUnit;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * Created by waseemh on 9/4/16.
 */
public class SeleniumEphemeral extends DeployableEphemeral<RemoteWebDriver> {

    /**
     * Selenium related constant values
     */
    private final static int SELENIUM_PORT = 4444;
    private final static int VNC_PORT = 5900;
    private final static String SERVER_PATH = "/wd/hub";
    private static final String CHROME_IMAGE = "selenium/standalone-chrome-debug:2.53.0";
    private static final String FIREFOX_IMAGE = "selenium/standalone-firefox-debug:2.53.0";
    private static final String PHANTOMJS_IMAGE = "davert/phantomjs-env:latest";

    private final DesiredCapabilities desiredCapabilities;

    private final static Logger logger = LoggerFactory.getLogger(SeleniumEphemeral.class);

    protected SeleniumEphemeral(Builder builder) {
        super(builder);
        this.desiredCapabilities = builder.desiredCapabilities;
    }

    @Override
    protected RemoteWebDriver createObject(DeploymentEndpoints endpoints) {

        URL remoteWebDriverUrl = null;
        for(DeploymentEndpoints.Endpoint endpoint : endpoints.list()) {
            if(endpoint.getName().equals("selenium-server")) {
                String host = endpoint.getHost();
                int port = endpoint.getPort();
                try {
                    remoteWebDriverUrl = new URL(String.format("http://%s:%s/%s", host, port, SERVER_PATH));
                    logger.info("RemoteWebDriver URL {}",remoteWebDriverUrl);
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        }

        return new RemoteWebDriver(remoteWebDriverUrl,desiredCapabilities);
    }

    @Override
    protected DeploymentUnit createDeploymentUnit() {

        String browserName = desiredCapabilities.getBrowserName();
        logger.info("Selenium browser: {}",browserName);

        String image;
        switch (browserName) {
            case BrowserType.CHROME:
                image = CHROME_IMAGE;
                break;
            case BrowserType.FIREFOX:
                image = FIREFOX_IMAGE;
                break;
            case BrowserType.PHANTOMJS:
                image = PHANTOMJS_IMAGE;
                break;
            default:
                throw new UnsupportedOperationException("Provided browser type '" + browserName + "' is not supported");
        }

        return new DockerDeploymentUnit.Builder("selenium",image)
                .withCpu(1)
                .withMem(1024)
                .withHealthProbe(new HttpProbe.Builder()
                        .withPath(SERVER_PATH)
                        .withPort(SELENIUM_PORT)
                        .build())
                .withPort(new DeploymentPort.Builder("selenium-server",SELENIUM_PORT)
                        .build())
                .withPort(new DeploymentPort.Builder("vnc-server",VNC_PORT)
                        .build())
                .build();
    }

    public static class Builder extends DeployableEphemeral.Builder<Builder,RemoteWebDriver> {

        private DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();

        public Builder (DeploymentContext deploymentContext) {
            super(deploymentContext);
        }

        public Builder withDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
            this.desiredCapabilities = desiredCapabilities;
            return this;
        }

        public SeleniumEphemeral build() {
            return new SeleniumEphemeral(this);
        }

    }

}