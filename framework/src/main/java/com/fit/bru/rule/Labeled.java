package com.fit.bru.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Condition or action with the business label describing it.
 */
record Labeled<F>(String label, F function) {

    /**
     * Labels the functions with a default label: the prefix followed by the position of the function.
     */
    static <F> List<Labeled<F>> withDefaultLabels(String prefix, List<F> functions) {
        List<Labeled<F>> labeled = new ArrayList<>();
        for (int i = 0; i < functions.size(); i++) {
            labeled.add(new Labeled<>(prefix + " " + (i + 1), functions.get(i)));
        }
        return labeled;
    }

    static <F> List<String> labels(List<Labeled<F>> labeled) {
        return labeled.stream().map(Labeled::label).toList();
    }

    static <F> List<F> functions(List<Labeled<F>> labeled) {
        return labeled.stream().map(Labeled::function).toList();
    }
}
