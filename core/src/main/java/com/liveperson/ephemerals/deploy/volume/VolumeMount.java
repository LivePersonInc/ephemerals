package com.liveperson.ephemerals.deploy.volume;

/**
 * Created by waseemh on 11/21/16.
 */
public class VolumeMount {

    private String name;

    private String mountPath;

    public VolumeMount(String name, String mountPath) {
        this.name = name;
        this.mountPath = mountPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMountPath() {
        return mountPath;
    }

    public void setMountPath(String mountPath) {
        this.mountPath = mountPath;
    }
}
