package RotationPackage;

/**
 * Defines the class to work with controller and rotate vector using axis-angle rotation
 *
 * @author  Karina Ganieva
 * @version 1.0
 * @since   2020-03-15
 */

public class RotationAxisAngle {
    private Vector3D from;
    private Vector3D to;
    public RotationAxisAngle(double a1, double b1, double c1)
    {
        from = new Vector3D(a1, b1, c1);
    }

    /**
     * This method is used to get target vector
     * @return Vector3D target vector
     */
    public Vector3D getTo() {
        return to;
    }

    /**
     * This method is used to get begin vectors
     * @return Vector3D begin vector
     */
    public Vector3D getFrom() {
        return from;
    }

    /**
     * This method is used to rotate vector about axis
     * @param n axis of rotation
     * @param angle angle of rotation
     */
    public void rotateAboutAxis(Vector3D n, double angle) {
        n = n.normalize();
        to = Vector3D.sumVectors(from.multiplyByScalar(Math.cos(Math.toRadians(angle))),
                (Vector3D.multiplyVectors(n, from)).multiplyByScalar(Math.sin(Math.toRadians(angle))),
                n.multiplyByScalar(Vector3D.dotProduct(n, from) * (1 - Math.cos(Math.toRadians(angle)))));
    }

    /**
     * This method is used to interpolate pyramid using Slerp
     * @param from
     * @param to parameter of interpolation
     * @param t parameter of interpolation
     * @return Vector3D[] vectors after interpolation
     */
    public static Vector3D Slerp(Vector3D from, Vector3D to, double t) {
        if (t <= 0.0) return from;
        if (t >= 1.0) return to;
        double cosOmega = Vector3D.dotProduct(from.normalize(), to.normalize());
        double q1x = to.getX();
        double q1y = to.getY();
        double q1z = to.getZ();
        if (cosOmega < 0.0) {
            q1x = -q1x;
            q1y = -q1y;
            q1z = -q1z;
            cosOmega = -cosOmega;
        }
        double k0, k1;
        if (cosOmega > 0.9999) {
            k0 = 1.0 - t;
            k1 = t;
        }
        else {
            double sinOmega = Math.sqrt(1.0 - cosOmega*cosOmega);
            double omega = Math.atan2(sinOmega, cosOmega);
            double oneOverSinOmega = 1.0 / sinOmega;
            k0 = Math.sin((1.0 - t) * omega) * oneOverSinOmega;
            k1 = Math.sin(t * omega) * oneOverSinOmega;
        }
        return new Vector3D(k0*from.getX() + k1*q1x, k0*from.getY() + k1*q1y, k0*from.getZ() + k1*q1z);
    }

    public static Vector3D lerp(Vector3D q1, Vector3D q2, double t) {
        return new Vector3D(q1.getX() * (1 - t) + q2.getX() * t,
                q1.getY() * (1 - t) + q2.getY() * t,
                q1.getZ() * (1 - t) + q2.getZ() * t);
    }
}
