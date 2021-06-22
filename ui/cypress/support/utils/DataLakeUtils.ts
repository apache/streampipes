import * as CSV from 'csv-string';

export class DataLakeUtils {

    public static checkResults(dataLakeIndex: string, fileRoute: string) {

        it('Validate result in datalake', function () {
            const streamPipesUser = 'riemer@fzi.de';
            cy.request('GET', '/streampipes-backend/api/v3/users/' + streamPipesUser + '/datalake/data/' + dataLakeIndex + '/download?format=csv',
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
        const result = []
        const index = CSV.readAll(csv, row => {
            result.push(row);
        });

        return result;
    }

}