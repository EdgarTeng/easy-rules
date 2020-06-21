package org.jeasy.rules.core;

import java.lang.reflect.Method;

public class ActionMethodOrderBean implements Comparable<ActionMethodOrderBean> {

    private final Method method;
    private final int order;

    public ActionMethodOrderBean(Method method, int order) {
        this.method = method;
        this.order = order;
    }

    public Method getMethod() {
        return method;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(ActionMethodOrderBean o) {
        if (getOrder() < o.getOrder()) {
            return -1;
        } else if (getOrder() > o.getOrder()) {
            return 1;
        } else {
            return (getMethod().equals(o.getMethod())) ? 0 : 1;
        }
    }

    @Override
    public int hashCode() {
        return method.hashCode() * 31 + order;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ActionMethodOrderBean)) {
            return false;
        }

        ActionMethodOrderBean that = (ActionMethodOrderBean) obj;

        if (order != that.order) {
            return false;
        }
        return method.equals(that.method);
    }
}
