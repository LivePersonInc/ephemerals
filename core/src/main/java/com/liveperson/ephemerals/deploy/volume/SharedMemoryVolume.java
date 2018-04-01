package com.liveperson.ephemerals.deploy.volume;


public class SharedMemoryVolume extends Volume {
    private final String name;
    private final EmptyDir emptyDir;

    public SharedMemoryVolume(SharedMemoryVolume.Builder builder) {
        this.name = builder.name;
        this.emptyDir = builder.emptyDir;
    }

    public String getName() {
        return this.name;
    }

    public EmptyDir getEmptyDir() {
        return this.emptyDir;
    }

    public static class EmptyDir {
        private String medium;

        public EmptyDir(String medium) {
            this.medium = medium;
        }

        public String getMedium() {
            return this.medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }
    }

    public static class Builder extends com.liveperson.ephemerals.deploy.volume.Volume.Builder {
        private String name;
        private EmptyDir emptyDir;

        public Builder() {
        }

        public SharedMemoryVolume.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public SharedMemoryVolume.Builder withEmptyDir(EmptyDir emptyDir) {
            this.emptyDir = emptyDir;
            return this;
        }


        public SharedMemoryVolume build() {
            return new SharedMemoryVolume(this);
        }
    }
}