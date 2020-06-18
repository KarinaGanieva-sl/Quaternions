package RotationPackage;

public class Vector3D {
    private double x;
    private double y;
    private double z;

    public Vector3D(double a, double b, double c) {
        x = a;
        y = b;
        z = c;
    }

    public double getX() { return x;}
    public double getY() { return y;}
    public double getZ() { return z;}

    static double dotProduct(Vector3D a, Vector3D b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    Vector3D multiplyByScalar(double k) {
        return new Vector3D(x * k, y * k, z * k);
    }

    static Vector3D sumVectors(Vector3D a, Vector3D b) {
        return new Vector3D(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    static Vector3D sumVectors(Vector3D a, Vector3D b, Vector3D c) {
        return new Vector3D(a.x + b.x + c.x, a.y + b.y + c.y, a.z + b.z + c.z);
    }

    static Vector3D multiplyVectors(Vector3D a, Vector3D b) {
        return new Vector3D(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    public double getLength() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    Vector3D normalize() {
        return new Vector3D(x / getLength(), y / getLength(), z / getLength());
    }
}
