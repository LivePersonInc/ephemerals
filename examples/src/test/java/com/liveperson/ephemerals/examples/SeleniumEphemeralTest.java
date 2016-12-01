package com.liveperson.ephemerals.examples;

import com.liveperson.ephemerals.SeleniumEphemeral;
import com.liveperson.ephemerals.junit.EphemeralResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Created by waseemh on 9/21/16.
 */
public class SeleniumEphemeralTest extends EphemeralAbstractTest {

    @Rule
    public EphemeralResource<RemoteWebDriver> seleniumResource = new EphemeralResource(
            new SeleniumEphemeral.Builder(getKubernetesDeploymentContext()).withDimension(new Dimension(2000,1800))
                    .build());

    @Test
    public void test() {
        RemoteWebDriver remoteWebDriver = seleniumResource.get();
        remoteWebDriver.get("http://yahoo.com");
        Assert.assertNotNull(remoteWebDriver.findElementById("uh-logo"));
    }

}
