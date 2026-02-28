import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'delete-post-btn',
  imports: [],
  templateUrl: './delete-post-btn.html',
  styleUrl: './delete-post-btn.css',
})

export class DeletePostBtn {
  @Input() postId?: number
  @Output() delete = new EventEmitter<boolean>()


  emitDelete() {
    this.delete.emit()
   }
}
