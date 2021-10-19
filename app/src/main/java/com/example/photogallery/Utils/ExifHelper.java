package com.example.photogallery.Utils;

public class ExifHelper {

    /**
     * Convert latitude or longitude into DMS (degree minute second) format.
     * @param lat_long Latitude or longitude to be converted.
     * @return DMS formatted latitude or longitude.
     */
    synchronized public static final String convert(double lat_long) {
        StringBuilder sb = new StringBuilder();
        lat_long = Math.round(lat_long*10000.0)/10000.0;
        lat_long=Math.abs(lat_long);
        int degree = (int) lat_long;
        lat_long *= 60;
        lat_long -= (degree * 60.0d);
        int minute = (int) lat_long;
        lat_long *= 60;
        lat_long -= (minute * 60.0d);
        int second = (int) (lat_long*1000.0d);

        sb.setLength(0);
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000");
        return sb.toString();
    }

    /**
     * Converts latitude reference from decimal for DMS (degree minute second) format.
     * @param latitude The latitude being referenced.
     * @return "S" or "N".
     */
    public static String latitudeRef(double latitude) {
        return latitude<0.0d?"S":"N";
    }

    /**
     * Converts longitude reference from decimal for DMS (degree minute second) format.
     * @param longitude The longitude being referenced.
     * @return "W" or "E".
     */
    public static String longitudeRef(double longitude) {
        return longitude<0.0d?"W":"E";
    }
}
