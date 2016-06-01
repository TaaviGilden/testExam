import {Component, Input, Output, EventEmitter} from 'angular2/core';

import {PlantHireRequest} from '../declarations';

@Component({
    selector: 'overview-component',
  templateUrl: '/app/phr/overview/overview.html'
})
export class OverviewComponent {
    @Input() phr: PlantHireRequest;
    @Output() createPHREvent: EventEmitter<any> = new EventEmitter();

    createPHR() {
        this.createPHREvent.emit(null);
    }
}
