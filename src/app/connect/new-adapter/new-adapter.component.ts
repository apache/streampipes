import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { RestService } from '../rest.service';
import { FormatDescription } from '../model/connect/grounding/FormatDescription';
import { AdapterDescription } from '../model/connect/AdapterDescription';
import { MatDialog } from '@angular/material';
import { AdapterStartedDialog } from './component/adapter-started-dialog.component';
import { Logger } from '../../shared/logger/default-log.service';
import { GenericAdapterSetDescription } from '../model/connect/GenericAdapterSetDescription';
import { GenericAdapterStreamDescription } from '../model/connect/GenericAdapterStreamDescription';

@Component({
  selector: 'sp-new-adapter',
  templateUrl: './new-adapter.component.html',
  styleUrls: ['./new-adapter.component.css'],
})
export class NewAdapterComponent implements OnInit {
  @Input()
  adapter: AdapterDescription;
  allFormats: FormatDescription[] = [];
  isLinearStepper: boolean = true;

  hasInputProtocol: Boolean;
  hasInputFormat: Boolean;
  hasInput: Boolean[];
  inputValue = '';

  constructor(
    private logger: Logger,
    private restService: RestService,
    private _formBuilder: FormBuilder,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    this.restService.getFormats().subscribe(x => {
      this.allFormats = x.list;
      this.allFormats;
    });
  }

  public startAdapter() {
    let dialogRef = this.dialog.open(AdapterStartedDialog, {
      // width: '250px',
      // data: { name: this.name, animal: this.animal }
    });

    this.restService.addAdapter(this.adapter);

    dialogRef.afterClosed().subscribe(result => {});
  }

  formatSelected(selectedFormat) {
    if (
      this.adapter instanceof GenericAdapterSetDescription ||
      this.adapter instanceof GenericAdapterStreamDescription
    ) {
      this.adapter.format = selectedFormat;
    }
  }
}
