package org.jeasy.rules.core;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.format;

public class RuleProxy implements InvocationHandler {

    private final Object target;
    private String name;
    private String description;
    private Integer priority;

    private Method conditionMethod;
    private Set<ActionMethodOrderBean> actionMethods;
    private Method[] methods;
    private Method toStringMethod;
    private Method compareToMethod;


    private org.jeasy.rules.annotation.Rule annotation;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleProxy.class);


    public RuleProxy(Object target) {
        this.target = target;
    }

    public static Rule asRule(Object obj) {
        if (obj instanceof Rule) {
            return (Rule) obj;
        }

        //TODO validate
        return (Rule) Proxy.newProxyInstance(Rule.class.getClassLoader(),
                new Class[]{Rule.class, Comparable.class},
                new RuleProxy(obj));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String methodName = method.getName();
        switch (methodName) {
            case "getName":
                return getRuleName();
            case "getDescription":
                return getRuleDescription();
            case "getPriority":
                return getRulePriority();
            case "evaluate":
                return evaluateMethod(args);
            case "execute":
                return executeMethod(args);
            case "equals":
                return equalsMethod(args);
            case "compareTo":
                return compareToMethod(args);
            case "hashCode":
                return hashCodeMethod();
            case "toString":
                return toStringMethod();
            default:
                return null;
        }
    }

    private String toStringMethod() throws InvocationTargetException, IllegalAccessException {
        Method toStringMethod = getToStringMethod();
        if (toStringMethod != null) {
            return (String) toStringMethod.invoke(target);
        } else {
            return getRuleName();
        }
    }

    private Method getToStringMethod() {
        if (this.toStringMethod == null) {
            Method[] methods = getMethods();
            for (Method method : methods) {
                if ("toString".equals(method.getName())) {
                    this.toStringMethod = method;
                    return this.toStringMethod;
                }
            }
        }
        return this.toStringMethod;
    }

    private int hashCodeMethod() {
        int result = getRuleName().hashCode();
        int priority = getRulePriority();
        String description = getRuleDescription();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + priority;
        return result;
    }

    private Object compareToMethod(Object[] args) throws Exception {
        Method compareToMethod = getCompareToMethod();
        Object otherRule = args[0]; // validated upfront
        if (compareToMethod != null && Proxy.isProxyClass(otherRule.getClass())) {
            if (compareToMethod.getParameters().length != 1) {
                throw new IllegalArgumentException("compareTo method must have a single argument");
            }
            RuleProxy ruleProxy = (RuleProxy) Proxy.getInvocationHandler(otherRule);
            return compareToMethod.invoke(target, ruleProxy.getTarget());
        } else {
            return compareTo((Rule) otherRule);
        }
    }

    private boolean equalsMethod(Object[] args) {
        if (!(args[0] instanceof Rule)) {
            return false;
        }
        Rule otherRule = (Rule) args[0];
        if (this.priority != otherRule.getPriority()) {
            return false;
        }
        if (!this.name.equals(otherRule.getName())) {
            return false;
        }
        return this.description.equals(otherRule.getDescription());
    }

    private Object evaluateMethod(Object[] args) {
        Facts facts = (Facts) args[0];
        Method conditionMethod = getConditionMethod();
        try {
            List<Object> actualParameters = getActualParameters(conditionMethod, facts);
            return conditionMethod.invoke(target, actualParameters.toArray());
        } catch (IllegalAccessException e) {
            LOGGER.warn("can not access method {}", conditionMethod);
            return false;
        } catch (InvocationTargetException e) {
            LOGGER.warn("invoke method {} occurs error", conditionMethod);
            return false;
        }
    }

    private Object executeMethod(Object[] args) {
        Facts facts = (Facts) args[0];
        for (ActionMethodOrderBean actionMethodOrderBean : getActionMethodBeans()) {
            try {
                List<Object> actualParameters = getActualParameters(actionMethodOrderBean.getMethod(), facts);
                actionMethodOrderBean.getMethod().invoke(target, actualParameters.toArray());
            } catch (IllegalAccessException e) {
                LOGGER.warn("can not access method {}", actionMethodOrderBean.getMethod());
            } catch (InvocationTargetException e) {
                LOGGER.warn("invoke method {} occurs error", actionMethodOrderBean.getMethod());
            } catch (IllegalArgumentException e) {
                LOGGER.warn("invoke method {}, wrong number of args: {}",
                        actionMethodOrderBean.getMethod(), args);
            }
        }
        return null;
    }

    private List<Object> getActualParameters(Method method, Facts facts) {
        List<Object> actualParameters = new ArrayList<>();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (Annotation[] annotations : parameterAnnotations) {
            if (annotations.length == 1) {
                String factName = ((Fact) (annotations[0])).value(); //validated upfront.
                Object fact = facts.get(factName);
                if (fact == null && !facts.asMap().containsKey(factName)) {
                    throw new NoSuchFactException(format("No fact named '%s' found in known facts: %n%s", factName, facts), factName);
                }
                actualParameters.add(fact);
            } else {
                actualParameters.add(facts); //validated upfront, there may be only one parameter not annotated and which is of type Facts.class
            }
        }
        return actualParameters;
    }


    public Set<ActionMethodOrderBean> getActionMethodBeans() {
        if (this.actionMethods == null) {
            this.actionMethods = new TreeSet<>();
            Method[] methods = getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Action.class)) {
                    Action actionAnnotation = method.getAnnotation(Action.class);
                    actionMethods.add(new ActionMethodOrderBean(method, actionAnnotation.order()));
                }
            }
        }
        return actionMethods;
    }

    private Method getConditionMethod() {
        if (this.conditionMethod == null) {
            Method[] methods = getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Condition.class)) {
                    this.conditionMethod = method;
                    break;
                }
            }
        }
        return this.conditionMethod;
    }

    private Method[] getMethods() {
        if (this.methods == null) {
            this.methods = getTargetClass().getMethods();
        }
        return this.methods;
    }

    private String getRuleName() {
        if (this.name == null) {
            org.jeasy.rules.annotation.Rule rule = getRuleAnnotation();
            this.name = rule.name();
        }
        return this.name;
    }

    private String getRuleDescription() {
        if (this.description == null) {
            org.jeasy.rules.annotation.Rule rule = getRuleAnnotation();
            this.description = rule.description();
        }
        return this.description;
    }

    private int getRulePriority() {
        if (this.priority == null) {
            org.jeasy.rules.annotation.Rule rule = getRuleAnnotation();
            this.priority = rule.priority();
        }
        return this.priority;
    }

    private org.jeasy.rules.annotation.Rule getRuleAnnotation() {
        if (this.annotation == null) {
            this.annotation = getTargetClass().getAnnotation(org.jeasy.rules.annotation.Rule.class);
        }
        return this.annotation;
    }

    private Class<?> getTargetClass() {
        return this.target.getClass();
    }

    private Method getCompareToMethod() {
        if (this.compareToMethod == null) {
            Method[] methods = getMethods();
            for (Method method : methods) {
                if (method.getName().equals("compareTo")) {
                    this.compareToMethod = method;
                    return this.compareToMethod;
                }
            }
        }
        return this.compareToMethod;
    }

    private int compareTo(final Rule otherRule) throws Exception {
        int otherPriority = otherRule.getPriority();
        int priority = getRulePriority();
        if (priority < otherPriority) {
            return -1;
        } else if (priority > otherPriority) {
            return 1;
        } else {
            String otherName = otherRule.getName();
            String name = getRuleName();
            return name.compareTo(otherName);
        }
    }


    public Object getTarget() {
        return target;
    }
}
