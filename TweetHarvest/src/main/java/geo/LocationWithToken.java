package geo;

import com.twitter.hbc.core.endpoint.Location;

public class LocationWithToken {

    private final String token;
    private final Location location;

    public String getToken() {
        return token;
    }

    public Location getLocation() {
        return location;
    }

    public LocationWithToken(String token, Location location) {
        this.token = token;
        this.location = location;
    }
}
