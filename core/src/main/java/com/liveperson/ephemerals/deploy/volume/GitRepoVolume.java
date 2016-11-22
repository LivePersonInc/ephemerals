package com.liveperson.ephemerals.deploy.volume;

/**
 * Created by waseemh on 11/21/16.
 */
public class GitRepoVolume extends Volume {

    private final String repository;

    private final String revision;

    private final String targetDirectory;

    public GitRepoVolume(GitRepoVolume.Builder builder) {
        this.repository = builder.repository;
        this.revision = builder.revision;
        this.targetDirectory = builder.targetDirectory;
    }

    public String getRepository() {
        return repository;
    }

    public String getRevision() {
        return revision;
    }

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public static class Builder extends Volume.Builder {

        private String repository;

        private String revision;

        private String targetDirectory = ".";

        public Builder withRepository(String repository) {
            this.repository = repository;
            return this;
        }

        public Builder withRevision(String revision) {
            this.revision = revision;
            return this;
        }

        public Builder withTargetDirectory(String targetDirectory) {
            this.targetDirectory = targetDirectory;
            return this;
        }

        @Override
        public GitRepoVolume build() {
            return new GitRepoVolume(this);
        }
    }

}
