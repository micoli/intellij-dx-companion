package org.micoli.dxcompanion.configuration;

public class ConfigurationException extends Exception {
    public final Long serial;

    public ConfigurationException(String message, Long serial) {
        super(message);
        this.serial = serial;
    }
}
