package com.utsoft.jan.myqqview.utils;


/**
 * Created by Administrator on 2019/8/26.
 * <p>
 * by author wz
 * <p>
 * com.utsoft.jan.myqqview.utils
 */
public class Geometry {
    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }

        public Point translate(Vector vector) {
            return new Point(this.x + vector.x, this.y + vector.y, this.z + vector.z);
        }
    }

    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale){
            return new Circle(center,radius*scale);
        }
    }

    public static class Cylinder{
        public final Point center;
        public final float height;
        public final float radius;

        public Cylinder(Point center, float height, float radius) {
            this.center = center;
            this.height = height;
            this.radius = radius;
        }
    }

    public static class Ray {
        public final Point point;
        public final Vector vector;

        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static Vector vectorBetween(Point from, Point to) {
            return new Vector(to.x - from.x, to.y - from.y, to.z - from.z);
        }

        public float lenght() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        public Vector crossProduct(Vector other) {
            return new Vector((y * other.z) - (z * other.y), (z * other.x) - (x * other.z), (x * other.y - (y * other.x)));
        }

        public float dotProduct(Vector other) {
            return x * other.x + y * other.y + z * other.z;
        }

        public Vector scale(float f) {
            return new Vector(x * f, y * f, z * f);
        }
    }

    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    private static float distanceBetween(Point sphere, Ray ray) {
        final Vector p1ToPoint = Vector.vectorBetween(ray.point, sphere);
        final Vector p2ToPoint = Vector.vectorBetween(ray.point.translate(ray.vector), sphere);

        final float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).lenght();

        final float lengthofBase = ray.vector.lenght();

        final float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthofBase;

        return distanceFromPointToRay;
    }

    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    public static Point intersectionPoint(Ray ray, Plane plane) {
        final Vector rayToPlaneVector = Vector.vectorBetween(ray.point, plane.point);

        float scaleFactor =  rayToPlaneVector.dotProduct(plane.normal)
                / ray.vector.dotProduct(plane.normal);

        final Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));

        return intersectionPoint;
    }
}
