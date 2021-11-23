import { ProcessingElementTestUtils } from '../../../support/utils/ProcessingElementTestUtils';
import { ProcessorTest } from '../../../support/model/ProcessorTest';

const allTests = Cypress.env('processingElements');


allTests.forEach(test => {
    const testName:string = "textFilter1"
    const processorTest = test as ProcessorTest;

    if(testName === processorTest.name){
        describe('Test Processor ' + test.dir, () => {
            before('Setup Test',() => {
                cy.initStreamPipesTest();
            });

            it('Initialize Test', () => {
                ProcessingElementTestUtils.testElement(processorTest);
            });
        });
    }
});