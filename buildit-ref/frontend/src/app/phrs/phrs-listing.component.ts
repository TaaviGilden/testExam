import {Component} from 'angular2/core';

import {Http} from 'angular2/http';

class Plant {
    name: string;
    description: string;
    price: number;
}

class RentalPeriod {
    startDate: Date;
    endDate: Date;
}

class XLink {
    _rel: string;
    href: string;
    method: string;
}

class PurchaseOrder {
    plant: Plant;
    rentalPeriod: RentalPeriod;
    status: string;
    total: number;
    _xlinks: XLink[];
}

class PlantHireRequest {
    plant: Plant;
    rentalPeriod: RentalPeriod;
    purchaseOrder: PurchaseOrder;
    status: string;
    total: number;
    _xlinks: XLink[];
}

@Component({
  templateUrl: '/app/phrs/list.html'
})
export class PHRListingComponent {
    phrs: PlantHireRequest[];
    constructor (public http: Http) {
        this.http.get("http://localhost:8080/api/procurement/phrs")
            .subscribe(resp => {this.phrs = resp.json(); console.log(resp.json())});
    }

    follow(link: XLink) {
        console.log(link);
        if (link.method == "PATCH") {
            this.http.patch(link.href, "")
                .subscribe(resp => console.log(resp));
        } else if (link.method == "DELETE")
            this.http.delete(link.href)
                .subscribe(resp => console.log(resp));
    }
}
