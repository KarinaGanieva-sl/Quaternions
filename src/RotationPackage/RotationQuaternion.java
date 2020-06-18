package RotationPackage;

public class RotationQuaternion {
    private Quaternion from;
    private Quaternion to;
    public RotationQuaternion(double a, double b, double c)
    {
        from = new Quaternion(0, a, b, c);
    }

    public Quaternion getTo() {
        return to;
    }

    public Quaternion getFrom() {
        return from;
    }

    public void rotateAboutAxis(double degrees, double a, double b, double c) {
        to = from.rotateAboutAxis(degrees, a, b, c);
    }

    public Quaternion Slerp(double t) {
        return Quaternion.Slerp(from, to, t);
    }

    public Quaternion lerp(double t) {
        return Quaternion.lerp(from, to, t);
    }

}
