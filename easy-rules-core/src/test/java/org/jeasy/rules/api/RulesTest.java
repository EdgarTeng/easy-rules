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

import org.jeasy.rules.core.BasicRule;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.junit.Test;

public class RulesTest {

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


}
