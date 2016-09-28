package com.liveperson.ephemerals.examples;

import com.liveperson.ephemerals.NginxEphemeral;
import com.liveperson.ephemerals.SeleniumEphemeral;
import com.liveperson.ephemerals.junit.EphemeralResource;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentContext;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentHandler;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.URL;

/**
 * An example test showing how to use multiple Ephemeral resources.
 *
 * In this example, an Nginx Ephemeral is launched and accessed by a Selenium Ephemeral.
 *
 * Created by waseemh on 9/27/16.
 */
public class SeleniumWithNginxTest extends EphemeralAbstractTest {

    @Rule
    public EphemeralResource<URL> nginxResource = new EphemeralResource(
            new NginxEphemeral.Builder(new KubernetesDeploymentContext(
                    new KubernetesDeploymentHandler.Builder(
                            getKubernetesService())
                            .build()))
                    .build());

    @Rule
    public EphemeralResource<RemoteWebDriver> seleniumResource = new EphemeralResource(
            new SeleniumEphemeral.Builder(new KubernetesDeploymentContext(
                    new KubernetesDeploymentHandler.Builder(
                            getKubernetesService())
                            .build()))
                    .build());

    @Test
    public void test() throws IOException {
        URL url = nginxResource.getEphemeral().get();
        RemoteWebDriver browser = seleniumResource.get();
        browser.get(url.toString()+"/index.html");
    }

}
