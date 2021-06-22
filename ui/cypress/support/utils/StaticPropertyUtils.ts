import {UserInput} from '../model/UserInput';

export class StaticPropertyUtils {

    public static input(configs: UserInput[]) {

        it('Configure Properties', function () {
            configs.forEach(config => {
                if (config.type == 'checkbox') {
                    cy.dataCy(config.selector).children().click();
                } else if (config.type == 'drop-down') {
                    cy.dataCy(config.selector).click().get('mat-option').contains(config.value).click();
                }  else if (config.type == 'radio') {
                    cy.dataCy(config.selector + config.value ).click();
                } else {
                    cy.dataCy(config.selector).type(config.value);
                }
            });
        });
    }
}
