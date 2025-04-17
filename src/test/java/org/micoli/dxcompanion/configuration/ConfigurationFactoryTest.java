package org.micoli.dxcompanion.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import net.javacrumbs.jsonunit.core.Option;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.micoli.dxcompanion.configuration.models.AbstractNode;
import org.micoli.dxcompanion.configuration.models.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.Assert.assertThrows;

public class ConfigurationFactoryTest {
    @Test
    public void testItReportEmptyConfiguration() {
        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> ConfigurationFactory.get(getConfigurationPath("empty").getAbsolutePath()));
        Assert.assertSame("No .dx-companion(.*).json configuration file(s) found.", exception.getMessage());
    }

    @Test
    public void testItReportErroneousConfiguration() {
        ConfigurationException exception = assertThrows(ConfigurationException.class, () -> ConfigurationFactory.get(getConfigurationPath("erroneousConfiguration").getAbsolutePath()));
        Assert.assertTrue(exception.getMessage().contains("com.google.gson.stream.MalformedJsonException: Expected ':'"));
    }

    @Test
    public void testItSucceedsToLoadASimpleConfiguration() throws Exception {
        testSuccessfullConfiguration("simple");
    }

    @Test
    public void testItSucceedsToLoadAMultipleFileConfiguration() throws Exception {
        testSuccessfullConfiguration("multiple");
    }

    private void testSuccessfullConfiguration(String testPath) throws ConfigurationException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Given
        File file = getConfigurationPath(testPath);

        // When
        ConfigurationFactory.LoadedConfiguration createdConfiguration = ConfigurationFactory.get(file.getAbsolutePath());

        //Then
        objectMapper.registerSubtypes(AbstractNode.class);
        String loadedConfiguration = objectMapper.writeValueAsString(createdConfiguration.configuration);
        String expectedConfiguration = Files.asCharSource(new File(Objects.requireNonNull(getClass().getClassLoader().getResource("configuration/" + testPath + "/expect.json")).getFile()), StandardCharsets.UTF_8).read();
        assertThatJson(loadedConfiguration)
            .when(Option.IGNORING_EXTRA_FIELDS)
            .when(Option.IGNORING_ARRAY_ORDER)
            .when(Option.TREATING_NULL_AS_ABSENT)
            .isEqualTo(expectedConfiguration)
        ;
    }

    @NotNull
    private File getConfigurationPath(String path) {
        return new File(Objects.requireNonNull(getClass().getClassLoader().getResource("configuration/" + path)).getFile());
    }
}
