package com.fit.bru.rule.model;

import java.util.List;

/**
 * Decision table, ready to be displayed: the condition columns come first, then the action columns.
 *
 * @param resource resource the table is read from
 * @param columns  columns of the table
 * @param rows     rows of the table, in evaluation order
 */
public record DecisionTableModel(String resource,
                                 List<DecisionColumnModel> columns,
                                 List<DecisionRowModel> rows) {

    public DecisionTableModel {
        columns = List.copyOf(columns);
        rows = List.copyOf(rows);
    }
}
