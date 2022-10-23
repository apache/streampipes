import { Component, OnInit } from '@angular/core';
import { AbstractFunctionDetailsDirective } from '../abstract-function-details.directive';
import { SpMetricsEntry } from '@streampipes/platform-services';

@Component({
  selector: 'sp-functions-metrics',
  templateUrl: './functions-metrics.component.html',
  styleUrls: []
})
export class SpFunctionsMetricsComponent extends AbstractFunctionDetailsDirective implements OnInit {

  metrics: SpMetricsEntry;

  ngOnInit(): void {
    super.onInit();
  }

  afterFunctionLoaded(): void {
    this.loadMetrics();
  }

  loadMetrics() {
    this.functionsService.getFunctionMetrics(this.activeFunction.functionId.id).subscribe(metrics => {
      this.metrics = metrics;
      this.contentReady = true;
    })
  }
}
