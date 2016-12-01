package com.liveperson.ephemerals;

import com.liveperson.ephemerals.deploy.*;
import com.liveperson.ephemerals.deploy.probe.HttpProbe;
import com.liveperson.ephemerals.deploy.unit.DeploymentUnit;
import com.liveperson.ephemerals.deploy.unit.DockerDeploymentUnit;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by waseemh on 9/4/16.
 */
public final class SeleniumEphemeral extends DeployableEphemeral<RemoteWebDriver> {

    /**
     * Selenium related constant values
     */
    private final static int SELENIUM_PORT = 4444;
    private final static int VNC_PORT = 5900;
    private final static String SERVER_PATH = "/wd/hub";
    private static final String CHROME_IMAGE = "selenium/standalone-chrome-debug:2.53.0";
    private static final String FIREFOX_IMAGE = "selenium/standalone-firefox-debug:2.53.0";
    private static final String PHANTOMJS_IMAGE = "davert/phantomjs-env:latest";
    private static final int DEFAULT_WIDTH = 1800;
    private static final int DEFAULT_HEIGHT = 1700;

    private final DesiredCapabilities desiredCapabilities;
    private Dimension dimension;
    private final static Logger logger = LoggerFactory.getLogger(SeleniumEphemeral.class);

    private SeleniumEphemeral(Builder builder) {
        super(builder);
        this.desiredCapabilities = builder.desiredCapabilities;
        if (builder.dimension != null)
            this.dimension = builder.dimension;
    }

    @Override
    protected RemoteWebDriver createObject(DeploymentEndpoints endpoints) {

        URL remoteWebDriverUrl = null;
        for (DeploymentEndpoints.Endpoint endpoint : endpoints.list()) {
            if (endpoint.getName().equals("selenium-server")) {
                String host = endpoint.getHost();
                int port = endpoint.getPort();
                try {
                    remoteWebDriverUrl = new URL(String.format("http://%s:%s/%s", host, port, SERVER_PATH));
                    logger.info("RemoteWebDriver URL {}", remoteWebDriverUrl);
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        }

        return new RemoteWebDriver(remoteWebDriverUrl, desiredCapabilities);
    }

    @Override
    protected DeploymentUnit createDeploymentUnit() {

        String browserName = desiredCapabilities.getBrowserName();
        logger.info("Selenium browser: {}", browserName);

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

        return new DockerDeploymentUnit.Builder("selenium", image)
                .withCpu(1)
                .withMem(1024)
                .withHealthProbe(new HttpProbe.Builder()
                        .withPath(SERVER_PATH)
                        .withPort(SELENIUM_PORT)
                        .build())
                .withPort(new DeploymentPort.Builder("selenium-server", SELENIUM_PORT)
                        .build())
                .withPort(new DeploymentPort.Builder("vnc-server", VNC_PORT)
                        .build())
                .withEnvVar("SCREEN_WIDTH", dimension == null ? String.valueOf(DEFAULT_WIDTH) : String.valueOf(dimension.getWidth()))
                .withEnvVar("SCREEN_HEIGHT", dimension == null ? String.valueOf(DEFAULT_HEIGHT) : String.valueOf(dimension.getHeight()))
                .build();
    }

    public static class Builder extends DeployableEphemeral.Builder<Builder, RemoteWebDriver> {

        private DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
        private Dimension dimension;

        public Builder(DeploymentContext deploymentContext) {
            super(deploymentContext);
        }

        public Builder withDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
            this.desiredCapabilities = desiredCapabilities;
            return this;
        }

        public Builder withDimension(Dimension dimension) {
            this.dimension = dimension;
            return this;
        }

        public SeleniumEphemeral build() {
            return new SeleniumEphemeral(this);
        }

    }

}