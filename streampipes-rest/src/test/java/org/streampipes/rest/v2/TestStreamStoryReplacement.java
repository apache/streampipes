/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
