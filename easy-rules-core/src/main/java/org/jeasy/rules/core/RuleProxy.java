package org.jeasy.rules.core;

import org.jeasy.rules.api.Rule;

public class RuleProxy {
    public static Rule asRule(Object rule) {
        if (rule instanceof Rule) {
            return (Rule) rule;
        }
        //TODO
        throw new UnsupportedOperationException("data type is not supported!");
    }
}
