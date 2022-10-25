import { CanNotEditAdapterDialog } from './can-not-edit-adapter-dialog.component';
import { DialogRef } from '@streampipes/shared-ui';
import { Pipeline } from '@streampipes/platform-services';

describe('CanNotEditAdapterDialog', () => {

  it('Show error message and containing pipelines', () => {
    const expectedPipelineName = 'Demo pipeline';
    const pipeline = new Pipeline();
    pipeline.name = expectedPipelineName;

    mount(`<sp-can-not-edit-adapter-dialog
                                                   [pipelines]="pipelines">
                   </sp-can-not-edit-adapter-dialog>`,
      {'pipelines': [pipeline]});

    cy.dataCy('can-not-edit-adapter-dialog-warning').should('be.visible');
    cy.dataCy('adapter-in-pipeline').should('have.length', 1);
    cy.dataCy('adapter-in-pipeline').contains(expectedPipelineName);
  });


  const mount = (template: string, cp?) => {
    cy.mount(template, {
      declarations: [CanNotEditAdapterDialog],
      providers: [{ provide: DialogRef, useValue: {}}],
      componentProperties: cp
    });
  };
});
