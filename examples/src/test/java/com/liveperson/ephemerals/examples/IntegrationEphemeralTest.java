package com.liveperson.ephemerals.examples;

import com.liveperson.ephemerals.NginxEphemeral;
import com.liveperson.ephemerals.SeleniumEphemeral;
import com.liveperson.ephemerals.WireMockEphemeral;
import com.liveperson.ephemerals.deploy.volume.GitRepoVolume;
import com.liveperson.ephemerals.junit.EphemeralResource;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by waseemh on 12/18/16.
 */
public class IntegrationEphemeralTest extends EphemeralAbstractTest {

    @Rule
    public EphemeralResource<URL> nginxResource = new EphemeralResource(
            new NginxEphemeral.Builder(getKubernetesDeploymentContext())
                    .withStaticContent(new GitRepoVolume.Builder()
                            .withRepository("git://github.com/example/devapp.git")
                            .withTargetDirectory("myapp")
                            .build())
                    .build());

    @Rule
    public EphemeralResource<RemoteWebDriver> seleniumResource = new EphemeralResource(
            new SeleniumEphemeral.Builder(getKubernetesDeploymentContext())
                    .withDesiredCapabilities(DesiredCapabilities.firefox())
                    .build());

    @Rule
    public EphemeralResource<URL> wireMockResource = new EphemeralResource(
            new WireMockEphemeral.Builder(getKubernetesDeploymentContext())
                    .withStubMapping(FileUtils.readFileToString(new File("/stubmapping.json")))
                    .build());


    @Test
    public void test() {

        // Fetch Nginx webserver URL
        URL url = nginxResource.get();

        // Navigate browser to Nginx URL
        seleniumResource.get().get(url.toString());

    }


    public IntegrationEphemeralTest() throws IOException {

    }


}
