import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Observable} from 'rxjs/Rx';

import {Plant, Query} from './declarations';

@Injectable()
export class PlantCatalogService {
    plants: Plant[] = [];
    constructor(public http: Http) {}
    
    executeQuery(query: Query) {
        this.http.get(`http://localhost:8080/api/procurement/plants?name=${query.name}&startDate=${query.startDate}&endDate=${query.endDate}`)
            .subscribe(response => this.plants = response.json());
    }
}
