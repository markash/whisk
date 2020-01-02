package com.github.markash.whisk.config;

import java.nio.file.Path;
import java.util.Optional;

import io.quarkus.arc.config.ConfigProperties;

@ConfigProperties(prefix = "whisk")
public class Configuration {

    private Path address;
    private FirebaseConfiguration firebase;

    public Path getAddress() { return address; }
    public void setAddress(Path address) { this.address = address; }

    public FirebaseConfiguration getFirebase() { return this.firebase; }
    public void setFirebase(final FirebaseConfiguration firebase) { this.firebase = firebase; }

    @Override
    public String toString() {
        
        return 
            Optional.ofNullable(getAddress())
                .map(address -> address.toFile().getAbsolutePath())
                .orElse("null");
    }
}