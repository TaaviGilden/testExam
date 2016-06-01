import {Injectable} from 'angular2/core';
import {Http, Headers} from 'angular2/http';

import moment from 'moment';

import {Plant, Query, PlantHireRequest, RentalPeriod} from './declarations';

@Injectable()
export class ProcurementService {
    phr: PlantHireRequest = new PlantHireRequest();
    constructor(public http: Http) {
    }
    setPlant(plant: Plant, query: Query) {
        this.phr.plant = plant;
        this.phr.rentalPeriod = new RentalPeriod();
        this.phr.rentalPeriod.startDate = query.startDate;
        this.phr.rentalPeriod.endDate = query.endDate;
        this.phr.total = (moment(query.endDate).diff(moment(query.startDate), 'days') + 1) * plant.price;
    }
    createPHR() {
        var headers = new Headers();
        headers.append('Content-Type', 'application/json');
        this.http.post(
            "http://localhost:8080/api/procurement/phrs",
            JSON.stringify(this.phr),
            {headers: headers})
            .subscribe(response => console.log(response.json()));
    }
}