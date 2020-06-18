package ControllerPackage;

import RotationPackage.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.awt.*;

/**
 * Defines the scene and its controllers
 *
 * @author  Karina Ganieva
 * @version 1.0
 * @since   2020-03-15
 */

public class Controller {
    private Group root = new Group();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final CameraForm cameraForm = new CameraForm();
    private final CameraForm cameraForm2 = new CameraForm();
    private final CameraForm cameraForm3 = new CameraForm();
    private final CameraForm world = new CameraForm();
    private Cylinder connect = new Cylinder(0.3, 0.3);
    private Cylinder line = new Cylinder(1, 1);
    private Sphere sphere = new Sphere(UNIT);
    private Pyramid pyramid = new Pyramid(2*UNIT, 3*UNIT, 3);

    static final int AXIS_LENGTH = 250;
    private static final int AXIS_WIDTH = 1;
    private static final int UNIT = 25;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_MIN_DISTANCE = -550;
    private static final double CAMERA_MAX_DISTANCE = -150;
    private static final double CAMERA_INITIAL_X_ANGLE = 75;
    private static final double CAMERA_INITIAL_Z_ANGLE = -8;
    private static final String[] MODES = {"Quaternion rotation", "Axis-angle rotation"};
    private static final String[] METHODS = {"Slerp", "Lerp"};
    private static final String[] FIGURES = {"Vector", "Pyramid"};
    private  RotationQuaternion rotationQuaternion;
    private RotationAxisAngle rotationAxisAngle;
    private PyramidQuaternionRotation pyramidQuaternionRotation;
    private PyramidAxisAngleRotation pyramidAxisAngleRotation;
    private int mode;
    private int method;
    private boolean pressed_apply;

    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private double mouseDeltaX;
    private double mouseDeltaY;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    @FXML
    private ComboBox<String> figure_combobox;

    @FXML
    private Button button_plus;

    @FXML
    private ComboBox<String> method_combobox;

    @FXML
    private Slider slider_t;

    @FXML
    private TextField text_t;

    @FXML
    private AnchorPane pane_main;

    @FXML
    private SubScene subScene;

    @FXML
    private ComboBox<String> mode_combobox;

    @FXML
    private TextField text_angle;

    @FXML
    private Slider slider_angle;

    @FXML
    private TextField vector_a;

    @FXML
    private TextField axis_b;

    @FXML
    private TextField vector_c;

    @FXML
    private TextField axis_a;

    @FXML
    private TextField vector_b;

    @FXML
    private TextField axis_c;

    /**
     * This method is used to initialize the scene and 3D-figures
     */
    public void initialize() {
        initSize();
        initCombobox();
        initSliders();

        buildCamera();
        buildAxes();
        buildFigures();
        root.getChildren().add(world);

        subScene.setRoot(root);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);
    }

    /**
     * This method is used to change figure in 3D-space
     */
    @FXML
    void figureChangedEvent() {
        if(figure_combobox.getSelectionModel().getSelectedIndex() == 0)
            buildVectorSpace();
        else
            buildPyramidSpace();
    }

    /**
     * This method is used to build vector space f the chosen figure is vector
     */
    void buildVectorSpace() {
        subScene.setFill(Color.BLACK);
        world.getChildren().remove(pyramid.getPyramid());
        buildFigures();
        vector_a.setDisable(false);
        vector_b.setDisable(false);
        vector_c.setDisable(false);
    }

    /**
     * This method is used
     * to build pyramid space if the chosen figure is pyramid
     */
    void buildPyramidSpace() {
        subScene.setFill(Color.GREY);
        world.getChildren().removeAll(connect, line, sphere);
        world.getChildren().add(pyramid.getPyramid());
        vector_a.setDisable(true);
        vector_b.setDisable(true);
        vector_c.setDisable(true);
    }

    /**
     * This method is used to set size of different controllers
     */
    void initSize() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        pane_main.setPrefHeight(height);
        pane_main.setPrefWidth(width);
        pane_main.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                subScene.setWidth((double)t1*0.5);
            }
        });
        pane_main.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                subScene.setHeight((double)t1*0.8);
            }
        });
    }

    /**
     * This method is used to handle event "Apply button clicked"
     */
    @FXML
    void applyButtonClicked() {
        pressed_apply = true;
        world.getChildren().remove(connect);
        mode = mode_combobox.getSelectionModel().getSelectedIndex();
        method = method_combobox.getSelectionModel().getSelectedIndex();
        if(figure_combobox.getSelectionModel().getSelectedIndex() == 0)
            rotateAboutAxis();
        else
            rotatePyramid();
    }

    /**
     * This method is used to get data from user and rotate pyramid
     */
    void rotatePyramid() {
        double a_axis, b_axis, c_axis;
        double angle = Double.parseDouble(text_angle.getText());
        try {
            a_axis = Double.parseDouble(axis_a.getText());
            b_axis = Double.parseDouble(axis_b.getText());
            c_axis = Double.parseDouble(axis_c.getText());
            if(a_axis == 0 && b_axis == 0 && c_axis == 0)
                throw new NumberFormatException();
        }
        catch(NumberFormatException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("At least one number must be not 0");
            alert.showAndWait();
            return;
        }
        catch(Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage() + "must be a number!");
            alert.showAndWait();
            return;
        }
        if(mode == 0)
            rotatePyramidQuaternion(a_axis, b_axis, c_axis, angle);
        if(mode == 1)
            rotatePyramidAxisAngle(a_axis, b_axis, c_axis, angle);
    }

    /**
     * This method is used to rotate pyramid using quaternions
     * @param a_axis x-coordinate of axis
     * @param b_axis y-coordinate of axis
     * @param c_axis z-coordinate of axis
     * @param angle angle of rotation
     */
    void rotatePyramidQuaternion(double a_axis, double b_axis, double c_axis, double angle) {
        pyramidQuaternionRotation = new PyramidQuaternionRotation(pyramid.getPoints());
        pyramidQuaternionRotation.rotateAboutAxis(angle, a_axis, b_axis, c_axis);
        pyramid.setPoints(convertQuaternionIntoVector(pyramidQuaternionRotation.getTo()));
    }

    /**
     * This method is used to rotate pyramid using axis-angle rotation
     * @param a_axis x-coordinate of axis
     * @param b_axis y-coordinate of axis
     * @param c_axis z-coordinate of axis
     * @param angle angle of rotation
     */
    void rotatePyramidAxisAngle(double a_axis, double b_axis, double c_axis, double angle) {
        pyramidAxisAngleRotation = new PyramidAxisAngleRotation(pyramid.getPoints());
        pyramidAxisAngleRotation.rotateAboutAxis(new Vector3D(a_axis, b_axis, c_axis), angle);
        pyramid.setPoints(pyramidAxisAngleRotation.getTo());
    }

    /**
     * This method is used to rotate pyramid using axis-angle rotation
     * @param input quaternions for convertation
     * @return Vector3D[] vectors from convertation
     */
    Vector3D[] convertQuaternionIntoVector(Quaternion[] input) {
        Vector3D[] output = new Vector3D[input.length];
        for(int i = 0; i < output.length; i++)
            output[i] = new Vector3D(input[i].getX(), input[i].getY(), input[i].getZ());
        return output;
    }

    /**
     * This method is used to rotate figure about axis
     */
    void rotateAboutAxis() {
        double a_vector, b_vector, c_vector;
        double a_axis, b_axis, c_axis;
        double angle = Double.parseDouble(text_angle.getText());
        try {
            a_vector = Double.parseDouble(vector_a.getText());
            b_vector = Double.parseDouble(vector_b.getText());
            c_vector = Double.parseDouble(vector_c.getText());
            a_axis = Double.parseDouble(axis_a.getText());
            b_axis = Double.parseDouble(axis_b.getText());
            c_axis = Double.parseDouble(axis_c.getText());
            if(a_axis == 0 && b_axis == 0 && c_axis == 0)
                throw new NumberFormatException();
        }
        catch(NumberFormatException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("At least one number must be not 0");
            alert.showAndWait();
            return;
        }
        catch(Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage() + "must be a number!");
            alert.showAndWait();
            return;
        }
        if(mode == 0)
           rotateAboutAxisQuaternion(a_vector, b_vector, c_vector, a_axis, b_axis, c_axis, angle);
        if(mode == 1)
            rotateAxisAngle(a_vector, b_vector, c_vector, a_axis, b_axis, c_axis, angle);
        world.getChildren().add(connect);
    }

    /**
     * This method is used to rotate vector about axis using quaternions
     * @param a_vector x-coordinate of vector
     * @param b_vector y-coordinate of vector
     * @param c_vector z-coordinate of axis
     * @param a_axis x-coordinate of axis
     * @param b_axis y-coordinate of axis
     * @param c_axis z-coordinate of axis
     * @param angle angle of rotation
     */
    void rotateAboutAxisQuaternion(double a_vector, double b_vector, double c_vector, double a_axis, double b_axis, double c_axis, double angle) {
        rotationQuaternion = new RotationQuaternion(a_vector, b_vector, c_vector);
        rotationQuaternion.rotateAboutAxis(angle, a_axis, b_axis, c_axis);
        Quaternion to = rotationQuaternion.getTo();
        Quaternion.Slerp(rotationQuaternion.getFrom(), rotationQuaternion.getTo(), 0.5);
        createConnection(new Point3D(to.getX()/to.getLength()*UNIT, to.getY()/to.getLength()*UNIT, to.getZ()/to.getLength()*UNIT),
                new Point3D(to.getX()*UNIT, to.getY()*UNIT, to.getZ()*UNIT), line);
        Quaternion from = rotationQuaternion.getFrom();
        createConnection(new Point3D(from.getX() * UNIT, from.getY() * UNIT, from.getZ() * UNIT),
                new Point3D(to.getX() * UNIT, to.getY() * UNIT, to.getZ() * UNIT), connect);
    }

    /**
     * This method is used to rotate vector about axis using axis-angle rotation
     * @param a_vector x-coordinate of vector
     * @param b_vector y-coordinate of vector
     * @param c_vector z-coordinate of axis
     * @param a_axis x-coordinate of axis
     * @param b_axis y-coordinate of axis
     * @param c_axis z-coordinate of axis
     * @param angle angle of rotation
     */
    void rotateAxisAngle(double a_vector, double b_vector, double c_vector, double a_axis, double b_axis, double c_axis, double angle) {
        rotationAxisAngle = new RotationAxisAngle(a_vector, b_vector, c_vector);
        rotationAxisAngle.rotateAboutAxis(new Vector3D(a_axis, b_axis, c_axis), angle);
        Vector3D to = rotationAxisAngle.getTo();
        createConnection(new Point3D(to.getX()/to.getLength()*UNIT, to.getY()/to.getLength()*UNIT, to.getZ()/to.getLength()*UNIT),
                new Point3D(to.getX()*UNIT, to.getY()*UNIT, to.getZ()*UNIT), line);
        Vector3D from = rotationAxisAngle.getFrom();
        createConnection(new Point3D(from.getX() * UNIT, from.getY() * UNIT, from.getZ() * UNIT),
                new Point3D(to.getX() * UNIT, to.getY() * UNIT, to.getZ() * UNIT), connect);
    }

    /**
     * This method is used to initialize sliders for t and angle
     */
    void initSliders() {
        slider_angle.valueProperty().addListener((observable, oldValue, newValue) ->
                text_angle.setText(String.valueOf(slider_angle.getValue())));
        slider_angle.setShowTickLabels(true);
        text_angle.setText(String.valueOf(slider_angle.getValue()));

        slider_t.valueProperty().addListener((observable, oldValue, newValue) ->
                {
                    double t = slider_t.getValue();
                    text_t.setText(String.valueOf(t));
                    if(pressed_apply && figure_combobox.getSelectionModel().getSelectedIndex() == 0)
                        interpolateVector(t);
                    if(pressed_apply && figure_combobox.getSelectionModel().getSelectedIndex() == 1)
                        interpolatePyramid(t);
                });
        slider_t.setShowTickLabels(true);
        text_t.setText(String.valueOf(slider_t.getValue()));
    }

    /**
     * This method is used to interpolate pyramid
     * @param t parameter of interpolation
     */
    void interpolatePyramid(double t) {
        if(mode == 0)
        {
            Quaternion[] mid = null;
            switch (method)
            {
                case 0:
                    mid = pyramidQuaternionRotation.Slerp(t); break;
                case 1:
                    mid = pyramidQuaternionRotation.lerp(t);
            }
            pyramid.setPoints(convertQuaternionIntoVector(mid));
        }
        if(mode == 1)
        {
            Vector3D[] mid = null;
            switch (method)
            {
                case 0:
                    mid = pyramidAxisAngleRotation.Slerp(t); break;
                case 1:
                    mid = pyramidAxisAngleRotation.lerp(t);
            }
            pyramid.setPoints(mid);
        }
    }

    /**
     * This method is used to interpolate vector
     * @param t parameter of interpolation
     */
    void interpolateVector(double t) {
        if(mode == 0)
        {
            Quaternion mid = null;
            switch (method) {
                case 0:
                    mid = rotationQuaternion.Slerp(t); break;
                case 1:
                    mid = rotationQuaternion.lerp(t);
            }
            createConnection(new Point3D(mid.getX() / mid.getLength() * UNIT, mid.getY() / mid.getLength() * UNIT, mid.getZ() / mid.getLength() * UNIT),
                    new Point3D(mid.getX() * UNIT, mid.getY() * UNIT, mid.getZ() * UNIT), line);
        }
        if(mode == 1)
        {
            Vector3D mid = null;
            switch (method)
            {
                case 0:
                    mid = RotationAxisAngle.Slerp(rotationAxisAngle.getFrom(), rotationAxisAngle.getTo(), t); break;
                case 1:
                    mid = RotationAxisAngle.lerp(rotationAxisAngle.getFrom(), rotationAxisAngle.getTo(), t);
            }
            createConnection(new Point3D(mid.getX() / mid.getLength() * UNIT, mid.getY() / mid.getLength() * UNIT, mid.getZ() / mid.getLength() * UNIT),
                    new Point3D(mid.getX() * UNIT, mid.getY() * UNIT, mid.getZ() * UNIT), line);
        }
    }

    /**
     * This method is used to build a camera
     */
    void buildCamera() {
        world.getChildren().add(cameraForm);
        cameraForm.getChildren().add(cameraForm2);
        cameraForm2.getChildren().add(cameraForm3);
        cameraForm3.getChildren().add(camera);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        camera.setTranslateX(0);
        cameraForm.rz.setAngle(CAMERA_INITIAL_Z_ANGLE);
        cameraForm.rx.setAngle(CAMERA_INITIAL_X_ANGLE);

        subScene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;

                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                }
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }
                if (me.isPrimaryButtonDown()) {
                    cameraForm.ry.setAngle(cameraForm.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
                    cameraForm.rx.setAngle(cameraForm.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                    cameraForm2.t.setX(cameraForm2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);
                    cameraForm2.t.setY(cameraForm2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);
                }
            }
        });
    }

    /**
     * This method is used to build figures
     */
    void buildFigures() {
        PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setSpecularColor(Color.rgb(0, 0, 255, 0.6));
        blueMaterial.setDiffuseColor(Color.rgb(0, 0, 255, 0.6));
        sphere = new Sphere(UNIT);
        sphere.setMaterial(blueMaterial);
        world.getChildren().add(sphere);

        PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setSpecularColor(Color.rgb(255, 0, 0));
        redMaterial.setDiffuseColor(Color.rgb(255, 0, 0));
        line.setMaterial(redMaterial);
        world.getChildren().add(line);

        PhongMaterial material = new PhongMaterial();
        material.setSpecularColor(Color.rgb(11,255,244));
        material.setDiffuseColor(Color.rgb(11,255,244));
        connect.setMaterial(material);
    }

    /**
     * This method is used to coordinate axes
     */
    void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, AXIS_WIDTH, AXIS_WIDTH);
        Pyramid pyramidX = new Pyramid(10, 10, redMaterial, 0);
        final Box yAxis = new Box(AXIS_WIDTH, AXIS_LENGTH, AXIS_WIDTH);
        Pyramid pyramidY = new Pyramid(10, 10, greenMaterial, 1);
        final Box zAxis = new Box(AXIS_WIDTH, AXIS_WIDTH, AXIS_LENGTH);
        Pyramid pyramidZ = new Pyramid(10, 10, blueMaterial, 2);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        world.getChildren().addAll(xAxis, yAxis, zAxis);
        world.getChildren().addAll(pyramidX.getPyramid(), pyramidY.getPyramid(), pyramidZ.getPyramid());
    }

    /**
     * This method is used to initialize figures? modes and methods combobox
     */
    void initCombobox() {
        mode_combobox.getItems().addAll(MODES);
        mode_combobox.getSelectionModel().select(0);
        method_combobox.getItems().addAll(METHODS);
        method_combobox.getSelectionModel().select(0);
        figure_combobox.getItems().addAll(FIGURES);
        figure_combobox.getSelectionModel().select(0);
    }

    /**
     * This method is used to connect two points and change line
     * @param origin first point
     * @param target second point
     * @param line line-connection for two points
     */
    void createConnection(Point3D origin, Point3D target, Cylinder line) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);
        line.setHeight(height);
        line.getTransforms().clear();
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
    }

    /**
     * This method is used to zoom in on camera
     */
    @FXML
    void plusButtonClicked() {
        if(CAMERA_INITIAL_DISTANCE + 50 == CAMERA_MAX_DISTANCE)
            return;
        CAMERA_INITIAL_DISTANCE += 50;
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    }

    /**
     * This method is used to zoom out on camera
     */
    @FXML
    void minusButtonClicked() {
        if(CAMERA_INITIAL_DISTANCE - 50 == CAMERA_MIN_DISTANCE)
            return;
        CAMERA_INITIAL_DISTANCE -= 50;
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    }

    /**
     * This method is used to set up light theme
     */
    @FXML
    void darkButtonClicked() {
        subScene.setFill(Color.BLACK);
    }

    /**
     * This method is used to set up light theme
     */
    @FXML
    void silverButtonClicked() {
        subScene.setFill(Color.SILVER);
    }

}
