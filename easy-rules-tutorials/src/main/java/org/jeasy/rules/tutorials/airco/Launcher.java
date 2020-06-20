/*
 * The MIT License
 *
 *  Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.tutorials.airco;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jeasy.rules.tutorials.airco.DecreaseTemperatureAction.decreaseTemperature;
import static org.jeasy.rules.tutorials.airco.HighTemperatureCondition.itIsHot;

public class Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        // define facts
        Facts facts = new Facts();
        facts.put("temperature", 30);

        LOGGER.debug("facts: {}", facts);

        // define rules
        Rule airConditioningRule = new RuleBuilder()
                .name("air conditioning rule")
                .when(itIsHot())
                .then(decreaseTemperature())
                .build();
        Rules rules = new Rules();
        rules.register(airConditioningRule);

        // fire rules on known facts
        InferenceRulesEngine rulesEngine = new InferenceRulesEngine();

        rulesEngine.registerRuleListener(new RuleListener() {
            @Override
            public boolean beforeEvaluate(Rule rule, Facts facts) {
                LOGGER.debug("before evaluate, rule: {}, facts: {}", rule, facts);
                return true;
            }

            @Override
            public void afterEvaluate(Rule rule, Facts facts, boolean evaluationResult) {
                LOGGER.debug("after evaluate, rule: {}, facts: {}", rule, facts);
            }

            @Override
            public void beforeExecute(Rule rule, Facts facts) {
                LOGGER.debug("before execute, rule: {}, facts: {}", rule, facts);
            }

            @Override
            public void onSuccess(Rule rule, Facts facts) {
                LOGGER.debug("on success, rule: {}, facts: {}", rule, facts);
            }

            @Override
            public void onFailure(Rule rule, Facts facts, Exception exception) {
                LOGGER.debug("on failure, rule: {}, facts: {}", rule, facts);
            }

            @Override
            public void onEvaluationError(Rule rule, Facts facts, Exception exception) {
                LOGGER.debug("on evaluate error, rule: {}, facts: {}", rule, facts);
            }
        });

        rulesEngine.fire(rules, facts);
        LOGGER.debug("facts: {}", facts);
    }

}