package com.fit.bru.utils.parser;

import com.fit.bru.exception.RuleBuildingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class MarkdownTableParser {

    private static final String SEPARATOR = "\\|";

    private MarkdownTableParser() {
        // Prevent instantiation
    }

    public static List<MarkdownRow> parse(String resource) {
        try (var inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {

            if (inputStream == null) {
                throw new RuleBuildingException("The resource '%s' is not found".formatted(resource));
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return parse(br.lines().collect(Collectors.toList()));
            }
        } catch (IOException e) {
            throw new RuleBuildingException(e);
        }
    }

    private static List<MarkdownRow> parse(List<String> lines) {
        int headerSize = parseLine(lines.getFirst()).size();

        // remove header label
        lines.removeFirst();
        // remove header label separator
        lines.removeFirst();

        List<MarkdownRow> list = new ArrayList<>();
        for (String line : lines) {
            if (!line.isBlank()) {
                List<String> cells = parseLine(line);
                if (cells.size() != headerSize) {
                    throw new RuleBuildingException("The resource '%s' is not well structured");
                }
                MarkdownRow markdownRow = parseRow(cells, cells.lastIndexOf(""));
                list.add(markdownRow);
            }
        }
        return list;
    }


    private static List<String> parseLine(String line) {
        return Arrays.stream(line.trim().replaceFirst(SEPARATOR, "")
                .split(SEPARATOR)).map(String::trim).toList();
    }

    private static MarkdownRow parseRow(List<String> cells, int splitterIndex) {
        return new MarkdownRow(cells.subList(0, splitterIndex), cells.subList(splitterIndex + 1, cells.size()));
    }

}
