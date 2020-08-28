import { Component, OnInit } from '@angular/core';
import { DatalakeRestService } from '../../core-services/datalake/datalake-rest.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'sp-datalake-configuration',
  templateUrl: './datalake-configuration.component.html',
  styleUrls: ['./datalake-configuration.component.css']
})
export class DatalakeConfigurationComponent implements OnInit {

  constructor(
    protected dataLakeRestService: DatalakeRestService,
    private snackBar: MatSnackBar) { }

  ngOnInit(): void {
  }

  removeDataFromDataLake(): void {
    console.log('Delete');
    this.dataLakeRestService.removeAllData().subscribe(
      res => {
        let message = '';

        if (res) {
          message = 'Data successfully deleted!';
        } else {
          message = 'There was a problem when deleting the data!';
        }

        this.snackBar.open(message, '', {
          duration: 2000,
          verticalPosition: 'top',
          horizontalPosition: 'right'
        });
      });
  }

}
