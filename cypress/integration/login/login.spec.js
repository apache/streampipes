describe('Login Streampipes', function () {

    Cypress.Cookies.defaults({
        whitelist: "JSESSIONID"
    });

    it('Open Streampipes', function () {
        cy.visit('');
    });
    it('Should open Login Page', function () {
        cy.url({timeout: 60000}).should('contain', '#/login');
    });




    it('Fill in Username and Password', function () {
        cy.get('input[type="email"]').type('riemer@fzi.de');
        cy.get('input[type="password"]').type('1234');
    });
    it('Start Login', function () {
        cy.get('button').contains('Login').parent().click();
    });
    it('Go to Pipeline Editor', function() {
        cy.get('#ic_dashboard_24px_cache14').parent().click();
    });

    it('Cancel User Guide', function() {
        cy.get('.ng-binding.ng-scope').contains('Cancel').parent().click();
        
    });
    it('Drag n Drop', function() {
        // var images = Cypress.$("img[src$='http://141.21.12.190/assets/img/pe_icons/icon-flowrate-1.png']")
        // console.log(images);
        // images.parent().parent().parent().click();
        // cy.get('button').contains("Select categories").click();
        // cy.get('button').contains("Vehicle Source").click({force: true});
        cy.get('pipeline-element').contains("VP").parent().parent()
        .trigger('mousedown',0,0)
        .trigger('mousemove', 119, 300)
        .trigger('mouseup', 119, 300);
    })
});