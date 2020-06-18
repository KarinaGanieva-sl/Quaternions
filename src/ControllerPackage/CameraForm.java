package ControllerPackage;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Defines orientation of Perspective Camera in 3D-space
 *
 * @author  Karina Ganieva
 * @version 1.0
 * @since   2020-03-15
 */
class CameraForm extends Group {

  Translate t  = new Translate();
  Rotate rx = new Rotate();
  { rx.setAxis(Rotate.X_AXIS); }
  Rotate ry = new Rotate();
  { ry.setAxis(Rotate.Y_AXIS); }
  Rotate rz = new Rotate();
  { rz.setAxis(Rotate.Z_AXIS); }

    CameraForm() {
    super();
        Scale s = new Scale();
        getTransforms().addAll(t, rz, ry, rx, s);
  }
}