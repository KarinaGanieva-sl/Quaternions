package RotationPackage;

/**
 * Defines the class to work with controller and rotate pyramid using axis-angle rotation
 *
 * @author  Karina Ganieva
 * @version 1.0
 * @since   2020-03-15
 */

public class PyramidAxisAngleRotation {
    private Vector3D[] from;
    private Vector3D[] to;
    public PyramidAxisAngleRotation(Vector3D[] from_vectors)
    {
        from = from_vectors;
        to = new Vector3D[5];
    }

    /**
     * This method is used to get target vectors
     * @return Vector3D[] target vectors
     */
    public Vector3D[] getTo() {
        return to;
    }

    /**
     * This method is used to get begin vectors
     * @return Vector3D[] begin vectors
     */
    public Vector3D[] getFrom() {
        return from;
    }

    /**
     * This method is used to rotate pyramid about axis
     * @param n axis of rotation
     * @param angle angle of rotation
     */
    public void rotateAboutAxis(Vector3D n, double angle) {
        n = n.normalize();
        for(int i = 0; i < to.length; ++i)
            to[i] = Vector3D.sumVectors(from[i].multiplyByScalar(Math.cos(Math.toRadians(angle))),
                    (Vector3D.multiplyVectors(n, from[i])).multiplyByScalar(Math.sin(Math.toRadians(angle))),
                    n.multiplyByScalar(Vector3D.dotProduct(n, from[i]) * (1 - Math.cos(Math.toRadians(angle)))));
    }

    /**
     * This method is used to interpolate pyramid using Slerp
     * @param t parameter of interpolation
     * @return Vector3D[] vectors after interpolation
     */
    public Vector3D[] Slerp(double t) {
        Vector3D[] mid = new Vector3D[5];
        for(int i = 0; i < mid.length; ++i)
            mid[i] = RotationAxisAngle.Slerp(from[i], to[i], t);
        return mid;
    }

    /**
     * This method is used to interpolate pyramid using lerp
     * @param t parameter of interpolation
     * @return Vector3D[] vectors after interpolation
     */
    public Vector3D[] lerp(double t) {
        Vector3D[] mid = new Vector3D[5];
        for(int i = 0; i < mid.length; ++i)
            mid[i] = RotationAxisAngle.lerp(from[i], to[i], t);
        return mid;
    }

}
