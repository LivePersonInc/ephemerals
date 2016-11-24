package com.liveperson.ephemerals;

import com.liveperson.ephemerals.deploy.DeploymentContext;
import com.liveperson.ephemerals.deploy.DeploymentEndpoints;
import com.liveperson.ephemerals.deploy.DeploymentPort;
import com.liveperson.ephemerals.deploy.probe.HttpProbe;
import com.liveperson.ephemerals.deploy.unit.DeploymentUnit;
import com.liveperson.ephemerals.deploy.unit.DockerDeploymentUnit;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by waseemh on 11/22/16.
 */
public class AppiumAndroidEphemeral extends DeployableEphemeral<AndroidDriver> {
    /**
     * Selenium related constant values
     */
    private final static int APPIUM_PORT = 4723;
    private final static int VNC_PORT = 5900;
    private final static String SERVER_STATUS_PATH = "/wd/hub/status";
    private static final String APPUIM_IMAGE = "rgonalo/appium-emulator-debug";

    private final DesiredCapabilities desiredCapabilities;

    private final static Logger logger = LoggerFactory.getLogger(AppiumAndroidEphemeral.class);

    private AppiumAndroidEphemeral(AppiumAndroidEphemeral.Builder builder) {
        super(builder);
        this.desiredCapabilities = builder.desiredCapabilities;
    }

    @Override
    protected AndroidDriver createObject(DeploymentEndpoints endpoints) {

        URL remoteWebDriverUrl = null;
        for(DeploymentEndpoints.Endpoint endpoint : endpoints.list()) {
            if(endpoint.getName().equals("appium-server")) {
                String host = endpoint.getHost();
                int port = endpoint.getPort();
                try {
                    remoteWebDriverUrl = new URL(String.format("http://%s:%s/%s", host, port, "/wd/hub"));
                    logger.info("RemoteWebDriver URL {}",remoteWebDriverUrl);
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        }

        return new AndroidDriver(remoteWebDriverUrl,desiredCapabilities);
    }

    @Override
    protected DeploymentUnit createDeploymentUnit() {

        return new DockerDeploymentUnit.Builder("appium",APPUIM_IMAGE)
                .withCpu(1.5)
                .withMem(2048)
                .withHealthProbe(new HttpProbe.Builder()
                        .withPath(SERVER_STATUS_PATH)
                        .withPort(APPIUM_PORT)
                        .build())
                .withReadinessProbe(new HttpProbe.Builder()
                        .withPath(SERVER_STATUS_PATH)
                        .withPort(APPIUM_PORT)
                        .build())
                .withPort(new DeploymentPort.Builder("appium-server",APPIUM_PORT)
                        .build())
                .withPort(new DeploymentPort.Builder("vnc-server",VNC_PORT)
                        .build())
                .build();
    }

    public static class Builder extends DeployableEphemeral.Builder<AppiumAndroidEphemeral.Builder,AndroidDriver> {

        private DesiredCapabilities desiredCapabilities = DesiredCapabilities.android();

        public Builder (DeploymentContext deploymentContext) {
            super(deploymentContext);
        }

        public AppiumAndroidEphemeral.Builder withDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
            this.desiredCapabilities = desiredCapabilities;
            return this;
        }

        public AppiumAndroidEphemeral build() {
            return new AppiumAndroidEphemeral(this);
        }

    }
}
