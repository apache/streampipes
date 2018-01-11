package org.streampipes.rest.v2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestStreamStoryReplacement {

    public static void main(String[] args) {
        System.out.println(fixStreamStoryUrl("http://motorka.ijs.si/lisbon/dashboard.html"));
    }

    private static String fixStreamStoryUrl(String url) {
        Pattern pattern = Pattern.compile("/[a-zA-z]+.[a-zA-Z]+$");
        Matcher matcher = pattern.matcher(url);
        return matcher.group();
    }


}
