package com.fit.bru.utils.parser;

import com.fit.bru.exception.RuleBuildingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MarkdownTableParserTest {

    @Test
    void parse() {

        assertEquals(2, MarkdownTableParser.parse("decision-table.md").size());

        assertThrows(RuleBuildingException.class, () -> MarkdownTableParser.parse("markdown.md"),
                "The resource 'markdown.md' is not found");

        assertThrows(RuleBuildingException.class, () -> MarkdownTableParser.parse("invalid-table.md"),
                "The resource 'invalid-table.md' is not well structured");
    }
}