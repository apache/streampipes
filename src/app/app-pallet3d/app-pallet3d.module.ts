import {FlexLayoutModule} from "@angular/flex-layout";
import {MatFormFieldModule, MatGridListModule, MatInputModule} from "@angular/material";
import {FormsModule} from "@angular/forms";
import {CustomMaterialModule} from "../CustomMaterial/custom-material.module";
import {CommonModule} from "@angular/common";
import {NgModule} from "@angular/core";
import {AppPallet3dComponent} from "./app-pallet3d.component";
import {AppPallet3dRestService} from "./services/pallet3d-rest.service";

@NgModule({
    imports: [
        CommonModule,
        FlexLayoutModule,
        CustomMaterialModule,
        MatGridListModule,
        MatInputModule,
        MatFormFieldModule,
        FormsModule
    ],
    declarations: [
        AppPallet3dComponent
    ],
    providers: [
        AppPallet3dRestService,
        {
            provide: 'RestApi',
            useFactory: ($injector: any) => $injector.get('RestApi'),
            deps: ['$injector'],
        }
    ],
    entryComponents: [
        AppPallet3dComponent,
    ],
    exports: [
        AppPallet3dComponent
    ]
})
export class AppPallet3dModule {
}