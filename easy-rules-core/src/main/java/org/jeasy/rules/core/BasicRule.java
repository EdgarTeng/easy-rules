package org.jeasy.rules.core;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;

public class BasicRule implements Rule {

    protected String name;
    protected String description;
    protected int priority;

    public BasicRule() {
        this(DEFAULT_NAME);
    }

    public BasicRule(String name) {
        this(name, DEFAULT_DESCRIPTION);
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
        return false;
    }

    @Override
    public void execute(Facts facts) throws Exception {
        //no operation
    }

    @Override
    public int hashCode() {
        return getName().hashCode() * 31 + getPriority();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Rule) {
            Rule rule = (Rule) obj;
            if (getPriority() == rule.getPriority() &&
                    getName().equals(rule.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return new StringBuilder("Rule{")
                .append("name='").append(getName()).append("', ")
                .append("priority=").append(getPriority()).append(", ")
                .append("description='").append(getDescription()).append("'}")
                .toString();
    }

    @Override
    public int compareTo(Rule rule) {
        if (this.getPriority() < rule.getPriority()) {
            return -1;
        } else if (this.getPriority() > rule.getPriority()) {
            return 1;
        } else {
            return this.getName().compareTo(rule.getName());
        }
    }
}
