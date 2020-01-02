package com.github.markash.whisk.config;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "whisk.firebase")
public class FirebaseConfiguration {

    private String address;
    private String projectId;
    private String applicationName;
    private String storageBucket;

    public String getAddress() { return this.address; }
    public void setAddress(final String address) { this.address = address; }

    public String getStorageBucket() { return storageBucket; }
    public void setStorageBucket(String storageBucket) { this.storageBucket = storageBucket; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String applicationName) { this.applicationName = applicationName; }

    public String getDatabaseUrl() { return "https://" + getAddress() + "/"; }
}