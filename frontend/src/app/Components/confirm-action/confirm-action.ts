import { Component, EventEmitter, Input, Output, output } from '@angular/core';

@Component({
  selector: 'app-confirm-action',
  imports: [],
  templateUrl: './confirm-action.html',
  styleUrl: './confirm-action.css',
})
export class confirmAction {
  @Input() content: string = ''
  @Output() submit = new EventEmitter<boolean>()
  @Output() cancel = new EventEmitter<boolean>()


  onCancel(e: Event) {
    e.stopPropagation()
    this.cancel.emit(true)
  }
  onSubmit(e: Event) {
    e.stopPropagation()
    this.submit.emit(true)
  }
}
