package RotationPackage;

/**
 * Defines quaternion
 *
 * @author  Karina Ganieva
 * @version 1.0
 * @since   2020-03-15
 */

public class Quaternion {
    private double w, x, y, z;
    private double[][] matrix;
    Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
        matrix = initMatrix();
    }

    /**
     * This method is used to get w
     * @return double w
     */
    double getW() { return w;}

    /**
     * This method is used to get x
     * @return double x
     */
    public double getX() { return x; }

    /**
     * This method is used to get y
     * @return double y
     */
    public double getY() { return y; }

    /**
     * This method is used to get z
     * @return double z
     */
    public double getZ() { return z; }

    /**
     * This method is used to initialize matrix
     * @return double[][] matrix
     */
    private double[][] initMatrix() {
        double[][] matrix = new double[3][3];
        matrix[0][0] = roundAvoid( 2 * (Math.pow(w, 2) + Math.pow(x, 2)) - 1, 3);
        matrix[0][1] = roundAvoid(2 * (x * y - w * z), 3);
        matrix[0][2] = roundAvoid(2 * (x * z + w * y), 3);
        matrix[1][0] =  roundAvoid(2 * (x * y + w * z), 3);
        matrix[1][1] = roundAvoid(2 * (Math.pow(w, 2) + Math.pow(y, 2)) - 1, 3);
        matrix[1][2] = roundAvoid(2 * (y * z - w * x), 3);
        matrix[2][0] = roundAvoid(2 * (x * z - w * y), 3);
        matrix[2][1] = roundAvoid(2 * (y * z + w * x), 3);
        matrix[2][2] = roundAvoid(2 * (Math.pow(w, 2) + Math.pow(z, 2)) - 1, 3);

        return matrix;
    }

    /**
     * This method is used to round number
     * @return double[][] matrix
     */
    private static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    /**
     * This method is used to rotate vector about axis
     * @param degrees angle of rotation
     * @param a x-coord of axis of rotation
     * @param b y-coord of axis of rotation
     * @param c z-coord of axis of rotation
     */
    Quaternion rotateAboutAxis(double degrees, double a, double b, double c) {
        double[] coords_from = {x, y, z};
        Vector3D norm = new Vector3D(a, b, c).normalize();
        a = norm.getX();
        b = norm.getY();
        c = norm.getZ();
        Quaternion axis = new Quaternion(Math.cos(Math.toRadians(degrees / 2)), a * Math.sin(Math.toRadians(degrees / 2)), b * Math.sin(Math.toRadians(degrees / 2)), c * Math.sin(Math.toRadians(degrees / 2)));
        double[] coords_to = new double[3];
        for(int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j)
                coords_to[i] += axis.matrix[i][j] * coords_from[j];
        }
        return new Quaternion(0, coords_to[0], coords_to[1], coords_to[2]);
    }

    /**
     * This method is used to get dot product
     * @param a first Quaternion
     * @param b second Quaternion
     * @return dot product
     */
    static double getDotProduct(Quaternion a, Quaternion b) {
        return a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
     * This method is used to interpolate pyramid using Slerp
     * @param from begin Quaternion
     * @param to target Quaternion
     * @param t parameter of interpolation
     * @return Quaternion[] quaternions after interpolation
     */
    public static Quaternion Slerp(Quaternion from, Quaternion to, double t) {
        if (t <= 0.0) return from;
        if (t >= 1.0) return to;
        double cosOmega = Quaternion.getDotProduct(from.normalize(), to.normalize());
        double q1w = to.w;
        double q1x = to.x;
        double q1y = to.y;
        double q1z = to.z;
        double k0, k1;
        if (cosOmega > 0.9999) {
            k0 = 1.0 - t;
            k1 = t;
        }
        else {
            double sinOmega = Math.sqrt(1.0 - cosOmega*cosOmega);
            double omega = Math.toDegrees(Math.atan2(sinOmega, cosOmega));
            double oneOverSinOmega = 1.0 / sinOmega;
            k0 = Math.sin(Math.toRadians((1.0 - t) * omega)) * oneOverSinOmega;
            k1 = Math.sin(Math.toRadians(t * omega)) * oneOverSinOmega;
        }
        return new Quaternion(k0*from.w + k1*q1w, k0*from.x + k1*q1x, k0*from.y + k1*q1y, k0*from.z + k1*q1z);
    }

    /**
     * This method is used to interpolate pyramid using Slerp
     * @param q1 begin Quaternion
     * @param q2 target Quaternion
     * @param t parameter of interpolation
     * @return Quaternion[] quaternions after interpolation
     */
    static Quaternion lerp(Quaternion q1, Quaternion q2, double t) {
        return new Quaternion(q1.w * (1 - t) + q2.w * t,
                q1.x * (1 - t) + q2.x * t,
                q1.y * (1 - t) + q2.y * t,
                q1.z * (1 - t) + q2.z * t);
    }

    /**
     * This method is used to get length of quaternion
     * @return double quaternions after interpolation
     */
    public double getLength() {
        return Math.sqrt(Math.pow(w, 2) + Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    /**
     * This method is used to normalize quaternion
     * @return Quaternion normalized quaternion
     */
    private Quaternion normalize() {
        double length = getLength();
        return new Quaternion(w / length, x / length, y / length, z / length);
    }
}
