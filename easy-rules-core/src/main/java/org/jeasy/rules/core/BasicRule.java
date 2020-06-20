package org.jeasy.rules.core;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;

public class BasicRule implements Rule {

    protected String name;
    protected String description;
    protected int priority;

    public BasicRule() {
        this(DEFAULT_NAME, DEFAULT_DESCRIPTION, DEFAULT_PRIORITY);
    }

    public BasicRule(String name) {
        this(name, DEFAULT_DESCRIPTION, DEFAULT_PRIORITY);
    }

    public BasicRule(String name, String description) {
        this(name, description, DEFAULT_PRIORITY);
    }

    public BasicRule(String name, String description, int priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public boolean evaluate(Facts facts) {
        //TODO
        return false;
    }

    @Override
    public void execute(Facts facts) throws Exception {
        //TODO
    }

    @Override
    public int compareTo(Rule rule) {
        if (this.getPriority() < rule.getPriority()) {
            return -1;
        } else if (this.getPriority() > rule.getPriority()) {
            return 1;
        } else {
            if (this.getName() == null) {
                return 1;
            }
            if (rule.getName() == null) {
                return -1;
            }
            return this.getName().compareTo(rule.getName());
        }
    }
}
