import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';

@Component({
    selector: 'set-stream-app',
    templateUrl: './set-stream.component.html',
    styleUrls: ['./set-stream.component.css']
})
export class SetStreamComponent {
    selected: boolean;
    selectedName: String;
    @Output() setStreamSelected = new EventEmitter<boolean>();
    toggleClass(box) {
        if (this.selected) {
            var streamBox = document.getElementById("streamBox");
            var setBox = document.getElementById("setBox");
            this.selectedName = box;
            if (box == "streamBox") {
                streamBox.classList.add("selectedBox");
                setBox.classList.remove("selectedBox");
            }
            else{
                setBox.classList.add("selectedBox");
                streamBox.classList.remove("selectedBox");
            }

        }
        else{
            var selectedBox = document.getElementById(box);
            selectedBox.classList.add("selectedBox");
            this.selected = true;
        }
        this.setStreamSelected.emit(box);
    }
}