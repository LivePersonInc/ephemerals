# Ephemerals

[![Build Status](https://travis-ci.org/LivePersonInc/ephemerals.svg?branch=master)](https://travis-ci.org/LivePersonInc/ephemerals)

Ephemerals make it easy to setup test environment on-the-fly and let it scale with your container cluster.

## Quickstart

For this quickstart, we will create a simple Junit test which will launch an Nginx server, send HTTP request to server and assert that response is valid. We will assume that you already have a Kubernetes cluster and it's ready to use.

First, add below Maven dependencies:

```xml
<dependency>
        <groupId>com.liveperson.ephemerals</groupId>
        <artifactId>ephemerals-core</artifactId>
        <version>1.0.0.0</version>
</dependency>
<dependency>
        <groupId>com.liveperson.ephemerals</groupId>
        <artifactId>ephemerals-module-nginx</artifactId>
        <version>1.0.0.0</version>
</dependency>
<dependency>
        <groupId>com.liveperson.ephemerals</groupId>
        <artifactId>ephemerals-provider-kubernetes</artifactId>
        <version>1.0.0.0</version>
</dependency>
```

Initialize Nginx ephemeral server and set Kubernetes cluster configuration using Junit rule:

```java
@Rule
public EphemeralResource<URL> nginxResource = new EphemeralResource(
new NginxEphemeral.Builder(new KubernetesDeploymentContext(
        new KubernetesDeploymentHandler.Builder(
                new KubernetesService.Builder()
                        .withHost(KUBERNETES_HOST)
                        .withTrustCerts(KUBERNETES_TRUSTCERTS)
                        .withUsername(KUBERNETES_USERNAME)
                        .withPassword(KUBERNETES_PASSWORD)
                        .withNamespace(KUBERNETES_NAMESPACE)
                        .build())
                .build()))
        .build());
```

Send HTTP request to server and verify response:

```java
@Test
public void test() throws IOException {

    //Send HTTP request and verify reponse
    HttpClient client = HttpClientBuilder.create().build();
    HttpGet request = new HttpGet(url.toString());
    HttpResponse response = client.execute(request);
    Assert.assertEquals(200,response.getStatusLine().getStatusCode());

}
```

## Documentation

See the [Wiki](https://github.com/LivePersonInc/ephemerals/wiki/) for documentation and examples.

## Features

#### Tests Integration

Ephemerals can be integrated into system tests, integration and end-to-end tests to programmatically create required environment entities from developer's test code.

#### Plug-able Cluster Systems
 
Ephemerals were built to support multiple cluster management systems (Ephemeral's Deployment Providers). For example, you can switch from Kubernetes to Mesos almost transparently.

#### Cluster-based Scaling

Ephemerals can scale according to your servers cluster resources. The amount of parallel Ephemerals instances depends only on your cluster's nodes capacity.

#### Cloud Platforms Support

Ephemerals can be used with any cloud computing platform (Google Cloud, AWS, etc..), given it supports one of our Deployment Providers.
A perfect use-case would be using Ephemerals with Google Cloud Platform and its GKE, which is based on Kubernetes.


## Build

Maven is used as a build system. In order to produce a package, run maven command `mvn clean package -DskipTests`. 

Tests can be executed using command `mvn test`.

## License

MIT
