package org.micoli.dxcompanion.configuration.models.PostToggle;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.micoli.dxcompanion.configuration.models.RunnableNode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(name = "action", value = PostToggleAction.class),
    @JsonSubTypes.Type(name = "script", value = PostToggleScript.class),
})
public abstract class PostToggle implements RunnableNode {
}
