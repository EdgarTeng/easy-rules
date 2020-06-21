package org.jeasy.rules.core;

import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;

import java.util.ArrayList;
import java.util.List;

public class DefaultRule extends BasicRule {

    protected Condition condition;
    protected List<Action> actions = new ArrayList<>();

    public DefaultRule() {
        super();
    }

    public DefaultRule(String name) {
        super(name);
    }

    public DefaultRule(String name, String description) {
        super(name, description);
    }

    public DefaultRule(String name, String description, int priority) {
        super(name, description, priority);
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public void addAction(Action action) {
        this.actions.add(action);
    }

    @Override
    public boolean evaluate(Facts facts) {
        return condition.evaluate(facts);
    }

    @Override
    public void execute(Facts facts) throws Exception {
        for (Action action : actions) {
            action.execute(facts);
        }
    }
}
