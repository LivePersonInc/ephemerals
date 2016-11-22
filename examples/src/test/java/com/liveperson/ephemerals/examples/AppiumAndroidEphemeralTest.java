package com.liveperson.ephemerals.examples;

import com.liveperson.ephemerals.AppiumAndroidEphemeral;
import com.liveperson.ephemerals.junit.EphemeralResource;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentContext;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentHandler;
import io.appium.java_client.android.AndroidDriver;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by waseemh on 9/21/16.
 */
public class AppiumAndroidEphemeralTest extends EphemeralAbstractTest {

    static DesiredCapabilities CAPS;

    static {
        Map<String, Object> caps = new HashMap<>();
        caps.put("deviceName","Android Emulator");
        caps.put("platformVersion", "4.4");
        caps.put("app", "http://github.com/appium/sample-code/blob/master/sample-code/apps/ContactManager/ContactManager.apk?raw=true");
        caps.put("appPackage", "com.example.android.contactmanager");
        caps.put("appActivity", ".ContactManager");
        caps.put("newCommandTimeout",600);
        CAPS = new DesiredCapabilities(caps);
    }

    @Rule
    public EphemeralResource<AndroidDriver> seleniumResource = new EphemeralResource(
            new AppiumAndroidEphemeral.Builder(new KubernetesDeploymentContext(
                    new KubernetesDeploymentHandler.Builder(
                            getKubernetesService())
                            .build()))
                    .withDesiredCapabilities(CAPS)
                    .build());

    @Test
    public void test() {
        WebDriverWait webDriverWait = new WebDriverWait(seleniumResource.get(),60);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//*[@text='Add Contact']")));
        WebElement el = seleniumResource.get().findElement(By.xpath(".//*[@text='Add Contact']"));
        el.click();
    }

}
