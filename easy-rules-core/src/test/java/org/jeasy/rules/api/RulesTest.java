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
package org.jeasy.rules.api;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.core.BasicRule;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RulesTest.class);

    @Test
    public void testRule() {
        Rules rules = new Rules();
        rules.register(new WeatherRule("weather rule", "if rain, take an umbrella", 1));

        Facts facts = new Facts();
        Fact<Boolean> rainFact = new Fact<>("rain", true);
        facts.add(rainFact);

        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
    }

    @Test
    public void testRuleBuilder() {
        Rules rules = new Rules();
        rules.register(new RuleBuilder()
                .name("weather rule")
                .priority(1)
                .description("when rain, take an umbrella")
                .when(Condition.TRUE)
                .then(facts -> System.out.println("dear, take an umbrella please"))
                .then(facts -> System.out.println("记得带雨伞"))
                .build());

        Facts facts = new Facts();
        Fact<Boolean> rainFact = new Fact<>("rain", true);
        facts.add(rainFact);

        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
    }


    @Test
    public void testRuleAnnotation() {
        Rules rules = new Rules();
        rules.register(new HelloRule());

        Facts facts = new Facts();
        Fact<String> nameFact = new Fact<>("name", "Ken");
        Fact<String> chineseNameFact = new Fact<>("chineseName", "肯");
        facts.add(nameFact);
        facts.add(chineseNameFact);

        DefaultRulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.addRuleListener(new RuleListener() {
            @Override
            public boolean beforeEvaluate(org.jeasy.rules.api.Rule rule, Facts facts) {
                return true;
            }

            @Override
            public void afterEvaluate(org.jeasy.rules.api.Rule rule, Facts facts, boolean evaluationResult) {
                LOGGER.info("afterEvaluate, rule: {}, facts: {}", rule, facts);
            }

            @Override
            public void onEvaluationError(org.jeasy.rules.api.Rule rule, Facts facts, Exception exception) {
                LOGGER.info("onEvaluationError, rule: {}, facts: {}", rule, facts, exception);
            }

            @Override
            public void beforeExecute(org.jeasy.rules.api.Rule rule, Facts facts) {
                LOGGER.info("beforeExecute, rule: {}, facts: {}", rule, facts);
            }

            @Override
            public void onSuccess(org.jeasy.rules.api.Rule rule, Facts facts) {
                LOGGER.info("onSuccess, rule: {}, facts: {}", rule, facts);
            }

            @Override
            public void onFailure(org.jeasy.rules.api.Rule rule, Facts facts, Exception exception) {
                LOGGER.info("onFailure, rule: {}, facts: {}", rule, facts, exception);
            }
        });
        rulesEngine.fire(rules, facts);
    }

    static class WeatherRule extends BasicRule {

        public WeatherRule(String name, String description, int priority) {
            super(name, description, priority);
        }

        @Override
        public boolean evaluate(Facts facts) {
            return facts.get("rain");
        }

        @Override
        public void execute(Facts facts) throws Exception {
            System.out.println("Dear, take an umbrella please!");
        }
    }

    @Rule(name = "hello rule", description = "always say hello", priority = 1)
    public static class HelloRule {

        @org.jeasy.rules.annotation.Condition
        public boolean alwaysTrue() {
            return true;
        }

        @org.jeasy.rules.annotation.Action(order = 0)
        public void sayHello(@org.jeasy.rules.annotation.Fact("name") String name) {
            System.out.println("hello " + name);
        }

        @Action(order = 1)
        public void sayHelloInChinese(Facts facts) {
            System.out.println("你好，" + facts.get("chineseName"));
        }

    }

}
