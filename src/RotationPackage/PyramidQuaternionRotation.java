package RotationPackage;

/**
 * Defines the class to work with controller and rotate pyramid using quaternions
 *
 * @author  Karina Ganieva
 * @version 1.0
 * @since   2020-03-15
 */

public class PyramidQuaternionRotation {
    private Quaternion[] from;
    private  Quaternion[] to;
    public PyramidQuaternionRotation(Vector3D[] from_vectors)
    {
        from = new Quaternion[5];
        for(int i = 0; i < from.length; ++i)
            from[i] = new Quaternion(0, from_vectors[i].getX(), from_vectors[i].getY(), from_vectors[i].getZ());
        to = new Quaternion[5];
    }

    /**
     * This method is used to get target quaternions
     * @return Quaternion[] target quaternions
     */
    public Quaternion[] getTo() {
        return to;
    }

    /**
     * This method is used to get begin quaternions
     * @return Quaternion[] begin uaternions
     */
    public Quaternion[] getFrom() {
        return from;
    }

    /**
     * This method is used to rotate pyramid about axis
     * @param degrees angle of rotation
     * @param a x-coord of axis of rotation
     * @param b y-coord of axis of rotation
     * @param c z-coord of axis of rotation
     */
    public void rotateAboutAxis(double degrees, double a, double b, double c) {
        for(int i = 0; i < to.length; ++i)
            to[i] = from[i].rotateAboutAxis(degrees, a, b, c);
    }

    /**
     * This method is used to interpolate pyramid using Slerp
     * @param t parameter of interpolation
     * @return Quaternion[] quaternions after interpolation
     */
    public Quaternion[] Slerp(double t) {
        Quaternion[] mid = new Quaternion[5];
        for(int i = 0; i < mid.length; ++i)
            mid[i] = Quaternion.Slerp(from[i], to[i], t);
        return mid;
    }

    /**
     * This method is used to interpolate pyramid using lerp
     * @param t parameter of interpolation
     * @return Quaternion[] quaternions after interpolation
     */
    public Quaternion[] lerp(double t) {
        Quaternion[] mid = new Quaternion[5];
        for(int i = 0; i < mid.length; ++i)
            mid[i] = Quaternion.lerp(from[i], to[i], t);
        return mid;
    }
}
