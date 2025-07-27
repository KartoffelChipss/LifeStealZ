package com.zetaplugins.lifestealz.addon;

public class AddonMetadata {
    private final String name;
    private final String version;
    private final String author;
    private final String description;
    private final String configName;

    private AddonMetadata(Builder builder) {
        this.name = builder.name;
        this.version = builder.version;
        this.author = builder.author;
        this.description = builder.description;
        this.configName = builder.configName;
    }

    public static class Builder {
        private String name;
        private String version;
        private String author;
        private String description;
        private String configName;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder config(String configName) {
            this.configName = configName;
            return this;
        }

        public AddonMetadata build() {
            if (name == null || version == null || author == null) {
                throw new IllegalStateException("Name, version, and author are required");
            }
            if (configName == null) {
                configName = name.toLowerCase().replaceAll("[^a-z0-9]", "");
            }
            return new AddonMetadata(this);
        }
    }

    public String getName() { return name; }
    public String getVersion() { return version; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getConfigName() { return configName; }
}