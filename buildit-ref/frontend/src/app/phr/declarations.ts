export class Relation {
    href: string;
}

export class Plant {
    name: string;
    description: string;
    price: number;
    _links: {[rel:string]: Relation};
}

export class Query {
    name: string;
    startDate: Date;
    endDate: Date;
}

export class RentalPeriod {
    startDate: Date;
    endDate: Date;
}

export class PlantHireRequest {
    plant: Plant;
    rentalPeriod: RentalPeriod;
    status: string;
    total: number;   
}