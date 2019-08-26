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
}
