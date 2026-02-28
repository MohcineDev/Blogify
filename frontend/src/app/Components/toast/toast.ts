import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogModule,
  MatDialogTitle,
} from '@angular/material/dialog';

export interface ToastData {
  title: string
  content:string
}

@Component({
  selector: 'app-modal',
  template: `
  @if(data.title){
    <h2 mat-dialog-title>{{data.title}}</h2>
  }
<mat-dialog-content>{{data.content}}</mat-dialog-content>
<mat-dialog-actions>
  <button matButton mat-dialog-close>Close</button>
</mat-dialog-actions>
`,
  imports: [MatDialogTitle, MatDialogModule, MatDialogContent, MatDialogActions, MatDialogClose, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class toast {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {

  }
}
