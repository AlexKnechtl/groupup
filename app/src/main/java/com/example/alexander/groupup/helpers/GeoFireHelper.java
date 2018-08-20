package com.example.alexander.groupup.helpers;

import com.firebase.geofire.GeoLocation;

public class GeoFireHelper {
    public static Double GetDistance(GeoLocation geoLocationA, GeoLocation geoLocationB) {
        int radius = 6371; // Earth's radius in kilometers
        double latDelta = degreesToRadians(geoLocationB.latitude - geoLocationA.latitude);
        double lonDelta = degreesToRadians(geoLocationB.longitude - geoLocationA.longitude);

        double a = (Math.sin(latDelta / 2) * Math.sin(latDelta / 2)) +
                (Math.cos(degreesToRadians(geoLocationA.latitude)) * Math.cos(degreesToRadians(geoLocationB.latitude)) *
                        Math.sin(lonDelta / 2) * Math.sin(lonDelta / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radius * c;
    }

    public static double degreesToRadians(double a) {
        return a / 180 * Math.PI;
    }
}