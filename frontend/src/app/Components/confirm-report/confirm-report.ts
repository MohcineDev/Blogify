import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-confirm',
  imports: [FormsModule],
  templateUrl: './confirm-report.html',
  styleUrl: './confirm-report.css',
})

export class ConfirmReport{

  @Input() displayConfirm: any = false
  @Input() errMsg: String = ''
  @Output() toparent = new EventEmitter<boolean>()
  @Output() submit = new EventEmitter<boolean>()

  submitReport(e: Event): void {
    e.stopPropagation()
    this.submit.emit( )
  }

  hideConfirm(e: Event): void {
    if (e.target === e.currentTarget) {
      this.toparent.emit(true)
    }

  }
}
