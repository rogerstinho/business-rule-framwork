package com.fit.bru.rule.model;

import java.util.List;

/**
 * Row of a decision table.
 *
 * @param order position of the row in the table, starting at 1
 * @param cells values of the row, aligned with the columns of the table (a blank condition matches any value)
 */
public record DecisionRowModel(int order,
                               List<String> cells) {

    public DecisionRowModel {
        cells = List.copyOf(cells);
    }
}
