package org.jeasy.rules.core;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Rule;

import java.util.ArrayList;
import java.util.List;

public class RuleBuilder {

    private String name;
    private String description;
    private int priority;
    private Condition condition;
    private List<Action> actions = new ArrayList<>();


    public RuleBuilder name(String name) {
        this.name = name;
        return this;
    }

    public RuleBuilder description(String description) {
        this.description = description;
        return this;
    }

    public RuleBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    public RuleBuilder when(Condition condition) {
        this.condition = condition;
        return this;
    }

    public RuleBuilder then(Action action) {
        this.actions.add(action);
        return this;
    }

    public Rule build() {
        DefaultRule rule = new DefaultRule(name, description, priority);
        rule.setCondition(condition);
        actions.stream().forEach(action -> rule.addAction(action));
        return rule;
    }
}
