package com.fit.bru.rule;

import com.fit.bru.rule.context.RuleExecutionContext;

import java.util.Objects;


public final class RuleContext implements RuleExecutionContext {
    private final String firstname;
    private final String lastname;

    private int age;

    public RuleContext(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String firstname() {
        return firstname;
    }

    public String lastname() {
        return lastname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RuleContext) obj;
        return Objects.equals(this.firstname, that.firstname) &&
                Objects.equals(this.lastname, that.lastname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname);
    }

    @Override
    public String toString() {
        return "RuleContext[" +
                "firstname=" + firstname + ", " +
                "lastname=" + lastname + ']';
    }


}
