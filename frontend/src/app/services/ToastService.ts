import { Injectable } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { toast } from "../Components/toast/toast";

@Injectable({ providedIn: 'root' })
export class ToastService {

    constructor(private dialog: MatDialog) {

    }
    show(content: string, title?: string): void {

        this.dialog.open(toast, {
            data: {
                title: title,
                content: content
            },
            panelClass: 'custom-toast-panel' 
        });
    }


} 