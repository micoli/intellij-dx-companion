package org.micoli.dxcompanion.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Files;
import com.google.gson.*;
import org.micoli.dxcompanion.configuration.models.AbstractNode;
import org.micoli.dxcompanion.configuration.models.Configuration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.io.FileReader;

public class ConfigurationFactory {
    public static class LoadedConfiguration {
        public Configuration configuration;
        public String serial;

        private LoadedConfiguration(Configuration configuration, String serial) {
            this.configuration = configuration;
            this.serial = serial;
        }
    }

    private static final MessageDigest messageDigest;
    public static final ArrayList<String> acceptableConfigurationFiles = new ArrayList<>(Arrays.asList(".dx-companion.json", ".dx-companion.yaml", ".dx-companion.local.json", ".dx-companion.local.yaml"));

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static LoadedConfiguration get(String projectPath) throws ConfigurationException {
        List<String> files = acceptableConfigurationFiles.stream().filter((configurationFile) -> new File(projectPath, configurationFile).exists()).toList();
        if (files.isEmpty()) {
            throw new ConfigurationException("No .dx-companion(.*).json configuration file(s) found.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.registerSubtypes(AbstractNode.class);
        String stringContent = "";
        try {
            stringContent = loadConfigurationFiles(projectPath, files);
            return new LoadedConfiguration(
                objectMapper.readValue(stringContent, Configuration.class),
                Arrays.toString(messageDigest.digest(stringContent.getBytes(StandardCharsets.UTF_8)))
            );
        } catch (Exception e) {
            throw new ConfigurationException(e.getClass().descriptorString() + "-" + e.getMessage() + "\\n" + stringContent);
        }
    }

    private static String loadConfigurationFiles(String projectPath, List<String> files) throws IOException, GsonTools.JsonObjectExtensionConflictException {
        JsonObject mergedJson = new JsonObject();
        final Yaml yaml = new Yaml();
        for (String file : files) {
            String inputBuffer = "";
            File fullPathFile = new File(projectPath, file);
            if (file.endsWith(".json")) {
                inputBuffer = Files.asCharSource(fullPathFile, StandardCharsets.UTF_8).read();
            } else {
                try {
                    final Object load = yaml.load(new FileReader(fullPathFile));
                    inputBuffer = new GsonBuilder().setPrettyPrinting().create().toJson(load, LinkedHashMap.class);
                } catch (Exception e) {
                    throw new IOException(e.getMessage());
                }
            }
            JsonElement jsonFile = JsonParser.parseString(inputBuffer).getAsJsonObject();
            GsonTools.extendJsonObject(mergedJson, GsonTools.ConflictStrategy.PREFER_SECOND_OBJ, jsonFile.getAsJsonObject());
        }
        return mergedJson.toString();
    }
}