package geo;

import com.twitter.hbc.core.endpoint.Location;
import java.util.HashMap;

public class Locations {
    public static final HashMap<String, LocationWithToken> INDEX = new HashMap<>();
    
    static {
        INDEX.put("sydney", new LocationWithToken("syd", getCityLocation(150.6396, -34.1399, 151.3439, -33.5780)));
        INDEX.put("melbourne", new LocationWithToken("mel", getCityLocation(144.5532, -38.2250, 145.5498, -37.5401)));
    }
    
    public static LocationWithToken getLocationWithToken(String token) {
        return INDEX.getOrDefault(token, INDEX.get("melbourne"));
    }
    
    public static Location getCityLocation(double longSW, double latSW, double longNE, double latNE) {
        return new Location(new Location.Coordinate(longSW, latSW), new Location.Coordinate(longNE, latNE));
    }
}
