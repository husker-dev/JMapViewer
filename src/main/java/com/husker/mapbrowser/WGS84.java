
// Copyright 2015 Sebastian Kuerten
//
// This file is part of geomath.
//
// geomath is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// geomath is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with geomath. If not, see <http://www.gnu.org/licenses/>.


package com.husker.mapbrowser;

/**
 * This class provides methods for calculations with the WGS84 projection. It
 * offers conversions between WGS84 coordinates and the plane used in
 * Mercator-projection. It also contains a method for calculating the distance
 * between two coordinates using the haversine formula.
 *
 * @author Sebastian Kuerten (sebastian@topobyte.de)
 */
public class WGS84
{

    /**
     * Convert a position on the unit plane to a longitude.
     *
     * @param x
     *            the position on the plane (interval is <code>[0..1]</code>).
     * @return the longitude.
     */
    public static double merc2lon(double x)
    {
        return x * 360 - 180;
    }

    /**
     * Convert a position on the plane to a longitude.
     *
     * @param x
     *            the position on the plane (interval is
     *            <code>[0..worldsize]</code>).
     * @param worldsize
     *            a bound used to calculate the relative position of
     *            <code>x</code> on the Mercator-plane.
     * @return the longitude.
     */
    public static double merc2lon(double x, double worldsize)
    {
        return merc2lon(x / worldsize);
    }

    /**
     * Convert a position on the unit plane to a longitude.
     *
     * @param y
     *            the position on the plane (interval is <code>[0..1]</code>).
     * @return the latitude.
     */
    public static double merc2lat(double y)
    {
        double n = Math.PI - 2 * Math.PI * y;
        return 180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)));
    }

    /**
     * Convert a position on the plane to a longitude.
     *
     * @param y
     *            the position on the plane (interval is
     *            <code>[0..worldsize]</code>).
     * @param worldsize
     *            a bound used to calculate the relative position of
     *            <code>y</code> on the Mercator-plane.
     * @return the latitude.
     */
    public static double merc2lat(double y, double worldsize)
    {
        return merc2lat(y / worldsize);
    }

    /**
     * The inverse of <code>merc2lon</code>.
     *
     * @param lon
     *            the longitude to convert.
     * @return the relative position on the unit plane (interval
     *         <code>[0..1]</code> ).
     */
    public static double lon2merc(double lon)
    {
        return (lon + 180) / 360;
    }

    /**
     * The inverse of <code>merc2lon</code>.
     *
     * @param lon
     *            the longitude to convert.
     * @param worldsize
     *            the size of the Mercator-plane used to calculate the relative
     *            position <code>x</code>.
     * @return the relative position on the plane (interval
     *         <code>[0..worldsize]</code>).
     */
    public static double lon2merc(double lon, double worldsize)
    {
        return lon2merc(lon) * worldsize;
    }

    /**
     * The inverse of <code>merc2lat</code>.
     *
     * @param lat
     *            the latitude to convert.
     * @return the relative position on the unit plane (interval
     *         <code>[0..1]</code> ).
     */
    public static double lat2merc(double lat)
    {
        double rlat = Math.toRadians(lat);
        double cos = Math.cos(rlat);
        double sin = Math.sin(rlat);
        double tan = sin / cos;
        return (1 - Math.log(tan + 1 / cos) / Math.PI) / 2;
    }

    /**
     * The inverse of <code>merc2lat</code>.
     *
     * @param lat
     *            the latitude to convert.
     * @param worldsize
     *            the size of the Mercator-plane used to calculate the relative
     *            position <code>x</code>.
     * @return the relative position on the plane (interval
     *         <code>[0..worldsize]</code>).
     */
    public static double lat2merc(double lat, double worldsize)
    {
        return lat2merc(lat) * worldsize;
    }

    // a = 6378.137, b = 6356.752, radius ~= (2a + b) / 3
    private static final double RADIUS_EARTH = 6371008.67;

    /**
     * Calculate the distance in meters from (lon1, lat1) to (lon2, lat2) using
     * the haversine formula and a mean earth radius of 6371008.67 meters.
     *
     * @param lon1
     *            the first longitude.
     * @param lat1
     *            the first latitude.
     * @param lon2
     *            the second longitude.
     * @param lat2
     *            the second latitude.
     * @return the distance between the two points in meters.
     */
    public static double haversineDistance(double lon1, double lat1,
                                           double lon2, double lat2)
    {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * RADIUS_EARTH;
    }

}
