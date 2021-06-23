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

// tslint:disable-next-line:no-implicit-dependencies
import * as CSV from 'csv-string';
import { UserUtils } from './UserUtils';

export class DataLakeUtils {

    public static checkResults(dataLakeIndex: string, fileRoute: string) {

        it('Validate result in datalake', () => {
            cy.request('GET', '/streampipes-backend/api/v3/users/' + UserUtils.testUserName + '/datalake/data/' + dataLakeIndex + '/download?format=csv',
                {'content-type': 'application/octet-stream'}).should((response) => {
                const expectedResultString = response.body;
                cy.readFile(fileRoute).then((actualResultString) => {
                    DataLakeUtils.resultEqual(actualResultString, expectedResultString);
                });
            });
        });
    }

    private static resultEqual(actual: string, expected: string) {
        const expectedResult = DataLakeUtils.parseCsv(expected);
        const actualResult = DataLakeUtils.parseCsv(actual);
        expect(expectedResult).to.deep.equal(actualResult);
    }

    private static  parseCsv(csv: string) {
        const result = [];
        const index = CSV.readAll(csv, row => {
            result.push(row);
        });

        return result;
    }
}
