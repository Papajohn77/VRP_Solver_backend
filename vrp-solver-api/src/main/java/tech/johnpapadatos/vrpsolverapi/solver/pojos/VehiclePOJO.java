package tech.johnpapadatos.vrpsolverapi.solver.pojos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VehiclePOJO {
    private final String name;
    private int capacity;
    private List<RoutePointPOJO> route;

    public VehiclePOJO(String name, int capacity, DepotPOJO depot) {
        this.name = name;
        this.capacity = capacity;
        this.route = new ArrayList<>(List.of(depot, depot));
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<RoutePointPOJO> getRoute() {
        return Collections.unmodifiableList(route);
    }

    public void reduceCapacity(int demand) {
        capacity -= demand;
    }

    public void insertInRoute(int position, RoutePointPOJO routePoint) {
        route.add(position, routePoint);
    }
}
