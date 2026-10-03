package com.fit.bru.utils.parser;

import java.util.List;

/**
 * Markdown decision table.
 *
 * @param headers header labels of all the columns, including the separator column
 * @param rows    rows of the table
 */
public record MarkdownTable(List<String> headers, List<MarkdownRow> rows) {

}
