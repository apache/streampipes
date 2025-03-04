/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import * as CSV from 'csv-string';

export class DataSetUtils {
    /**
     * Compares two CSV strings for equality.
     *
     * @param actualCsv - The actual CSV string.
     * @param expectedCsv - The expected CSV string.
     * @param ignoreTime - Whether to ignore the timestamp field during comparison.
     */
    public static csvEqual(
        actualCsv: string,
        expectedCsv: string,
        ignoreTime: boolean,
    ) {
        const actualResult = ignoreTime
            ? DataSetUtils.getActualResultIgnoringTime(actualCsv)
            : DataSetUtils.parseCsv(actualCsv);

        const expectedResult = DataSetUtils.parseCsv(expectedCsv);
        DataSetUtils.compareCsvResults(actualResult, expectedResult);
    }

    private static getActualResultIgnoringTime(csv: string) {
        return DataSetUtils.parseCsv(csv).map(row => row.splice(1));
    }

    private static parseCsv(csv: string) {
        return CSV.parse(csv, ';');
    }

    private static compareCsvResults(
        actualResult: any[],
        expectedResult: any[],
    ) {
        expect(actualResult).to.deep.equal(expectedResult);
    }

    /**
     * Compares two JSON strings for equality.
     *
     * @param actualJsonString - The actual JSON string.
     * @param expectedJson - The expected JSON object.
     * @param ignoreTime - Whether to ignore the timestamp field during comparison.
     */
    public static jsonFilesEqual(
        actualJsonString: string,
        expectedJson: any[],
        ignoreTime: boolean,
    ) {
        const actualJson = JSON.parse(actualJsonString);

        if (ignoreTime) {
            actualJson.forEach((item: any) => delete item.timestamp);
            expectedJson.forEach((item: any) => delete item.timestamp);
        }

        expect(actualJson).to.deep.equal(expectedJson);
    }
}
