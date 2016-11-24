# Ephemerals

[![Build Status](https://travis-ci.org/LivePersonInc/ephemerals.svg?branch=master)](https://travis-ci.org/LivePersonInc/ephemerals)
[![Maven Central](https://img.shields.io/maven-central/v/com.liveperson.ephemerals/ephemerals-core.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.liveperson.ephemerals%22)

Ephemerals make it easy to setup test environment on-the-fly and let it scale with your container cluster. The main motivation behind Ephemerals is that whole test environment is launched and destroyed during test lifecycle. 

Ephemerals takes care of deployment process of testing endpoints and initialization of test objects:

![](https://github.com/LivePersonInc/ephemerals/raw/master/eph.png)

Ephemerals can be deployed on various cloud providers for creating use-and-throw instances of browsers, web services, databases or anything else than can expose a connection endpoint.

## Prerequisites

Ephemeral objects will be deployed and launched on a container cluster using a cluster manager. Following cluster managers are currently supported:

- Kubernetes

For this quickstart, you can create a Kubernetes cluster using following options:

- Run a Kuberenetes cluster locally using Minikube. You can find a detailed [tutorial here](http://blog.kubernetes.io/2016/07/minikube-easily-run-kubernetes-locally.html) on how to install and use Minikube.
- Opening an account on [Google Cloud Platform (GCP)](https://cloud.google.com). GCP and its [Container Engine(GKE)](https://cloud.google.com/container-engine/) use Kubernetes to manage and orchestrate containers in the cluster. Follow this [tutorial](https://deis.com/blog/2016/first-kubernetes-cluster-gke/) to spinup your first Kubernetes cluster on GKE.

## Quickstart

For this quickstart, we will create a simple Junit test which will launch a Selenium standalone server using Kubernetes and initialize a RemoteWebDriver instance . We will assume that you already have a Kubernetes cluster and it's ready to use.

First, add below Maven dependencies:

```xml
<dependency>
        <groupId>com.liveperson.ephemerals</groupId>
        <artifactId>ephemerals-core</artifactId>
        <version>1.0.0.3</version>
</dependency>
<dependency>
        <groupId>com.liveperson.ephemerals</groupId>
        <artifactId>ephemerals-module-selenium</artifactId>
        <version>1.0.0.3</version>
</dependency>
<dependency>
        <groupId>com.liveperson.ephemerals</groupId>
        <artifactId>ephemerals-provider-kubernetes</artifactId>
        <version>1.0.0.3</version>
</dependency>
```

Initialize FireFox Selenium ephemeral instance and set Kubernetes cluster configuration using Junit rule:

```java
@Rule
public EphemeralResource<RemoteWebDriver> seleniumResource = new EphemeralResource(
new SeleniumEphemeral.Builder(KubernetesEphemeral.create(KUBERNETES_HOST,KUBERNETES_USERNAME,KUBERNETES_PASSWORD))
                .withDesiredCapabilities(DesiredCapabilities.firefox())
        .build());
```

Get RemoteWebDriver instance and do some stuff using WebDriver API:

```java
@Test
public void test() throws IOException {

        RemoteWebDriver remoteWebDriver = seleniumResource.get();
        remoteWebDriver.get("http://yahoo.com");
        Assert.assertNotNull(remoteWebDriver.findElementById("uh-logo"));

}
```

For more examples, see [here](https://github.com/LivePersonInc/ephemerals/tree/master/examples)

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
