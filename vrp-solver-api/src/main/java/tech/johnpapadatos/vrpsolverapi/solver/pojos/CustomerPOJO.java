package tech.johnpapadatos.vrpsolverapi.solver.pojos;

public class CustomerPOJO extends RoutePointPOJO {
    private final int demand;

    public CustomerPOJO(
        int id, 
        String name, 
        double lat, 
        double lng, 
        String address, 
        int demand
    ) {
        super(id, name, lat, lng, address);
        this.demand = demand;
    }

    public int getDemand() {
        return demand;
    }
}