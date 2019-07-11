package org.streampipes.processors.textmining.jvm.processor;

import opennlp.tools.util.Span;
import org.streampipes.commons.exceptions.SpRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class TextMiningUtil {
    /*
     * Given an array of spans and an array of tokens, it extracts and merges the tokens
     * specified in the spans and adds them to a list. This list is returned
     */
    public static List<String> extractSpans(Span[] spans, String[] tokens) throws SpRuntimeException {
        List<String> list = new ArrayList<>();
        for (Span span : spans) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = span.getStart(); i < span.getEnd(); i++) {
                if (i >= tokens.length) {
                    throw new SpRuntimeException("token list does not fit spans (token list lenght: " + tokens.length
                        + ", span: [" + span.getStart() + ", " + span.getEnd() + "))");
                }
                stringBuilder.append(tokens[i]).append(' ');
            }
            // Removing the last space
            stringBuilder.setLength(Math.max(stringBuilder.length() - 1, 0));
            list.add(stringBuilder.toString());
        }
        return list;
    }
}
