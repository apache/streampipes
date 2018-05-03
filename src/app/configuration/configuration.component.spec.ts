import { TestBed, async } from '@angular/core/testing';
import { ConfigurationComponent } from './configuration.component';
import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule, MatGridListModule, MatTooltipModule, MatCheckboxModule, MatIconModule, MatInputModule } from '@angular/material';
import { FormsModule } from '@angular/forms';
import { ConsulServiceComponent } from './consul-service/consul-service.component';
import { ConfigurationService } from './shared/configuration.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ConfigurationModule', () => {
    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [
                CommonModule,
                FlexLayoutModule,
                MatGridListModule,
                MatButtonModule,
                MatIconModule,
                MatInputModule,
                MatCheckboxModule,
                MatTooltipModule,
                FormsModule,
                HttpClientTestingModule
            ],
            declarations: [
                ConfigurationComponent,
                ConsulServiceComponent
            ],
            providers: [
                ConfigurationService
            ]
        }).compileComponents();
    }));
    it('should create the component', async(() => {
        const fixture = TestBed.createComponent(ConfigurationComponent);
        const app = fixture.debugElement.componentInstance;
        expect(app).toBeTruthy();
    }));
});
