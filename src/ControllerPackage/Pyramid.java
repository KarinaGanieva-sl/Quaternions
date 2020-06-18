package ControllerPackage;

import RotationPackage.Vector3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;


/**
 * Define pyramid created from Triangle Mesh
 *
 * @author  Karina Ganieva
 * @version 1.0
 * @since   2020-03-15
 */
class Pyramid extends Group
{
    private MeshView pyramid;
    private TriangleMesh pyramidMesh;
    private Vector3D[] points = new Vector3D[5];
    Pyramid(float h, float s, int axis)
    {
        pyramidMesh = new TriangleMesh();
        pyramidMesh.getTexCoords().addAll(0,0);
        pyramidMesh.getFaces().addAll(
                0,0,  2,0,  1,0,          // Front left face
                0,0,  1,0,  3,0,          // Front right face
                0,0,  3,0,  4,0,          // Back right face
                0,0,  4,0,  2,0,          // Back left face
                4,0,  1,0,  2,0,          // Bottom rear face
                4,0,  3,0,  1,0           // Bottom front face
        );
        pyramid = new MeshView(pyramidMesh);
        pyramid.setDrawMode(DrawMode.FILL);

        PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setSpecularColor(Color.WHITE);
        blueMaterial.setDiffuseColor(Color.rgb(0, 0, 255, 0.9));
        pyramid.setMaterial(blueMaterial);
        if(axis == 3)
        {
            initPoints(h, s);
            setPoints(points);
            return;
        }
        pyramidMesh.getPoints().addAll(
                0,    0,    0,            // Point 0 - Top
                0,    h,    -s/2,         // Point 1 - Front
                -s/2, h,    0,            // Point 2 - Left
                s/2,  h,    0,            // Point 3 - Back
                0,    h,    s/2           // Point 4 - Right
        );

        if(axis == 0)
        {
            pyramid.setTranslateX(Controller.AXIS_LENGTH/2.0);
            pyramid.setRotate(90);
            pyramid.setTranslateY(-h/2);
        }
        if(axis == 1)
        {
            pyramid.setTranslateY(Controller.AXIS_LENGTH/2.0);
            pyramid.setRotate(180);
        }
        if(axis == 2)
        {
            pyramid.setTranslateZ(Controller.AXIS_LENGTH/2.0);
            pyramid.setRotationAxis(Rotate.X_AXIS);
            pyramid.setRotate(270);
            pyramid.setTranslateY(-h/2);
        }
    }

    /**
     * This method is used to set up points to create pyramid
     * @param h x-coordinate of axis
     * @param s y-coordinate of axis
     */
    void initPoints(float h, float s) {
        points[0] = new Vector3D(0, 0, h);
        points[1] = new Vector3D(0, -s/2, 0);
        points[2] = new Vector3D(-s/2, 0, 0);
        points[3] = new Vector3D(s/2, 0, 0);
        points[4] = new Vector3D(0, s/2, 0);
    }

    Pyramid(float h, float s, PhongMaterial material, int axis) {
        this(h,s,axis);
        pyramid.setMaterial(material);
    }

    /**
     * This method is used to get pyramid
     * @return MeshView pyramid
     */
    MeshView getPyramid() {
        return pyramid;
    }

    /**
     * This method is used to get points of pyramid
     * @return MeshView pyramid
     */
    Vector3D[] getPoints() {
        return points;
    }

    /**
     * This method is used to set up points of pyramid
     * @param points pyramid
     */
    void setPoints(Vector3D[] points) {
        pyramidMesh.getPoints().clear();
        for(int i = 0; i < points.length; i++)
            pyramidMesh.getPoints().addAll((float)points[i].getX(), (float)points[i].getY(), (float)points[i].getZ());
    }

}
