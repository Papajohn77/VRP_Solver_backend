package tech.johnpapadatos.vrpsolverapi.solver.pojos;

public class RoutePointPOJO {
    private final int id;
    private final String name;
    private final double lat;
    private final double lng;
    private final String address;

    public RoutePointPOJO(
        int id, 
        String name, 
        double lat, 
        double lng, 
        String address
    ) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }
}
