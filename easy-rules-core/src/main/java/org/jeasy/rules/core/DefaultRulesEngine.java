package org.jeasy.rules.core;

import org.jeasy.rules.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultRulesEngine implements RulesEngine {

    protected List<RuleListener> ruleListeners;
    protected List<RulesEngineListener> rulesEngineListeners;
    protected RulesEngineParameters rulesEngineParameters;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRulesEngine.class);

    public DefaultRulesEngine() {
        this.ruleListeners = new ArrayList<>();
        this.rulesEngineListeners = new ArrayList<>();
        this.rulesEngineParameters = new RulesEngineParameters();
    }

    @Override
    public RulesEngineParameters getParameters() {
        return rulesEngineParameters;
    }

    @Override
    public List<RuleListener> getRuleListeners() {
        return ruleListeners;
    }

    @Override
    public List<RulesEngineListener> getRulesEngineListeners() {
        return rulesEngineListeners;
    }

    @Override
    public void fire(Rules rules, Facts facts) {
        Objects.requireNonNull(rules, "rules can not be null");
        Objects.requireNonNull(facts, "facts can not be null");

        LOGGER.debug("rule: {}", rules);
        LOGGER.debug("facts: {}", facts);

        beforeEvaluate(rules, facts);

        for (Rule rule : rules) {
            doFire(rule, facts);
        }

        afterExecute(rules, facts);
    }

    private void beforeEvaluate(Rules rules, Facts facts) {
        for (RulesEngineListener rulesEngineListener : rulesEngineListeners) {
            rulesEngineListener.beforeEvaluate(rules, facts);
        }
    }

    private void afterExecute(Rules rules, Facts facts) {
        for (RulesEngineListener rulesEngineListener : rulesEngineListeners) {
            rulesEngineListener.afterExecute(rules, facts);
        }
    }

    void doFire(Rule rule, Facts facts) {
        if (!beforeEvaluate(rule, facts)) {
            return;
        }

        boolean evaluateResult = false;
        try {
            evaluateResult = rule.evaluate(facts);
            afterEvaluate(rule, facts, evaluateResult);
        } catch (Exception e) {
            onEvaluationError(rule, facts, e);
        }

        if (evaluateResult) {
            beforeExecute(rule, facts);
            try {
                rule.execute(facts);
                onSuccess(rule, facts);
            } catch (Exception e) {
                onFailure(rule, facts, e);
            }
        }
    }

    private boolean beforeEvaluate(Rule rule, Facts facts) {
        for (RuleListener ruleListener : ruleListeners) {
            if (!ruleListener.beforeEvaluate(rule, facts)) {
                return false;
            }
        }
        return true;
    }

    private void afterEvaluate(Rule rule, Facts facts, boolean evaluateResult) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.afterEvaluate(rule, facts, evaluateResult);
        }
    }

    private void beforeExecute(Rule rule, Facts facts) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.beforeExecute(rule, facts);
        }
    }

    private void onSuccess(Rule rule, Facts facts) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.onSuccess(rule, facts);
        }
    }

    private void onFailure(Rule rule, Facts facts, Exception exception) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.onFailure(rule, facts, exception);
        }
    }

    private void onEvaluationError(Rule rule, Facts facts, Exception exception) {
        for (RuleListener ruleListener : ruleListeners) {
            ruleListener.onEvaluationError(rule, facts, exception);
        }
    }


    @Override
    public Map<Rule, Boolean> check(Rules rules, Facts facts) {
        return null;
    }
}
