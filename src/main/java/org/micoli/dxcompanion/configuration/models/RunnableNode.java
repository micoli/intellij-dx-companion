package org.micoli.dxcompanion.configuration.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(name = "action", value = Action.class),
    @JsonSubTypes.Type(name = "script", value = Script.class),
    @JsonSubTypes.Type(name = "observedFile", value = ObservedFile.class),
})
public interface RunnableNode {
    String getLabel();
    String getIcon();
}
