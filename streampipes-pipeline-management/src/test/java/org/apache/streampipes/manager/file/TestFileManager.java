/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.manager.file;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class TestFileManager {

    @Test
    public void testRemoveBomWithNoBom() throws IOException {
        String expected = "test";
        InputStream inputStream = IOUtils.toInputStream(expected, StandardCharsets.UTF_8);
        InputStream resultStream = FileManager.removeBom(inputStream);
        String resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

        assertEquals(expected, resultString);
    }

    @Test
    public void testRemoveBomWithBom() throws IOException {
        String expected = "test";
        String UTF8_BOM = "\uFEFF";
        String inputString = UTF8_BOM + expected;
        InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
        InputStream resultStream = FileManager.removeBom(inputStream);
        String resultString = IOUtils.toString(resultStream, StandardCharsets.UTF_8);

        assertEquals(expected, resultString);
    }

    @Test
    public void testRemoveBomWithBomAndUmlauts() throws IOException {
        String expected = "testäüö";
        String UTF8_BOM = "\uFEFF";
        String inputString = UTF8_BOM + expected;
        InputStream inputStream = IOUtils.toInputStream(inputString, StandardCharsets.UTF_8);
        InputStream resultStream = FileManager.removeBom(inputStream);
        String resultString = IOUtils.toString(resultStream, StandardCharsets.ISO_8859_1);

        assertEquals(expected, resultString);
    }
}