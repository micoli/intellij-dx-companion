package org.micoli.dxcompanion.autocomplete;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.JsonLanguage;
import com.intellij.json.psi.*;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.patterns.PatternCondition;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.micoli.dxcompanion.configuration.ConfigurationFactory;

import java.util.*;

public class DxActionCompletionContributor extends CompletionContributor {

    private record Action(
        String actionId,
        String text
    ) {
    }

    private static final String prefix = "action:";

    public DxActionCompletionContributor() {
        PsiElementPattern.Capture<PsiElement> commandValuePattern = PlatformPatterns
            .psiElement()
            .withSuperParent(2, JsonProperty.class)
            .with(new PatternCondition<PsiElement>("isCommandProperty") {
                @Override
                public boolean accepts(@NotNull PsiElement element, ProcessingContext context) {
                    if (!(element.getParent() instanceof JsonStringLiteral)) {
                        return false;
                    }
                    PsiElement parent = element.getParent();
                    if (!(parent.getParent() instanceof JsonProperty)) {
                        return false;
                    }
                    return "command".equals(((JsonProperty) parent.getParent()).getName());
                }
            });

        extend(CompletionType.BASIC,
            commandValuePattern,
            new CompletionProvider<>() {
                @Override
                protected void addCompletions(
                    @NotNull CompletionParameters parameters,
                    @NotNull ProcessingContext context,
                    @NotNull CompletionResultSet result
                ) {
                    if (!(parameters.getOriginalFile().getLanguage() instanceof JsonLanguage)) {
                        return;
                    }
                    if (!ConfigurationFactory.acceptableConfigurationFiles.contains(parameters.getOriginalFile().getOriginalFile().getName())) {
                        return;
                    }

                    PsiElement element = parameters.getPosition();

                    if (!element.getParent().getText().contains(prefix) &&
                        !element.getParent().getText().contains("\"action")) {
                        return;
                    }

                    List<Action> actions = listActions();

                    for (Action action : actions) {
                        result.addElement(
                            LookupElementBuilder.create(prefix + action.actionId)
                                .withPresentableText(action.actionId)
                                .withTypeText(action.text, true)
                        );
                    }
                }
            });
    }

    private List<Action> listActions() {
        List<Action> actions = new ArrayList<>();
        ActionManager actionManager = ActionManager.getInstance();
        for (String actionId : actionManager.getActionIdList("")) {
            AnAction action = actionManager.getAction(actionId);
            if (action == null) {
                continue;
            }
            actions.add(new Action(
                actionId,
                action.getTemplatePresentation().getText()
            ));
        }
        return actions;
    }
}