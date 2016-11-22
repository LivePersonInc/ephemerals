package com.liveperson.ephemerals.examples;

import com.liveperson.ephemerals.NginxEphemeral;
import com.liveperson.ephemerals.deploy.volume.GitRepoVolume;
import com.liveperson.ephemerals.junit.EphemeralResource;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentContext;
import com.liveperson.ephemerals.provider.kubernetes.KubernetesDeploymentHandler;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * Created by waseemh on 9/27/16.
 */
public class NginxWithStaticContentEphemeralTest extends EphemeralAbstractTest {

    @Rule
    public EphemeralResource<URL> nginxResource = new EphemeralResource(
            new NginxEphemeral.Builder(new KubernetesDeploymentContext(
                    new KubernetesDeploymentHandler.Builder(
                            getKubernetesService())
                            .build()))
                    .withStaticContent(new GitRepoVolume.Builder()
                            .withRepository("git://github.com/webrtcftw/iswebrtcreadyyet.com.git")
                            .withTargetDirectory("mywebsite")
                            .build())
                    .build());

    @Test
    public void test() throws IOException {
        URL url = nginxResource.get();
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url.toString()+"/mywebsite/images/canary.png");
        HttpResponse response = client.execute(request);
        Assert.assertEquals(200,response.getStatusLine().getStatusCode());
    }

}
