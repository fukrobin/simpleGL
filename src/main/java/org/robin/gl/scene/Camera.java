package org.robin.gl.scene;

import static org.joml.Math.cos;
import static org.joml.Math.sin;
import static org.joml.Math.toRadians;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * 相机类， 默认上向量为 y 轴 正方向.
 *
 * @author fkrobin
 * @date 2022/2/27
 */
@Getter
@Setter
public class Camera {

  private static final Vector3f DEST = new Vector3f();

  /**
   * 视图矩阵.
   */
  private final Matrix4f viewMatrix;

  private final Matrix4f projectionMatrix;

  /**
   * 世界坐标系的 Y 轴向量.
   */
  private final Vector3f worldUp;

  /**
   * 摄像机的位置.
   */
  private final Vector3f position;

  /**
   * 摄像机视线方向，随着摄像机的旋转而改变.
   */
  private final Vector3f target;
  /**
   * 摄像机坐标系的 Y 轴向量. 随着摄像机的旋转而改变
   */
  private final Vector3f up;
  /**
   * 摄像机的右向量.
   */
  private final Vector3f right;

  /**
   * 摄像机的累计旋转角度.
   */
  private final Vector3f rotation;

  /**
   * 平截头体的近裁剪平面距离.
   */
  private float nearClip = 0.1f;

  /**
   * 平截头体的远裁剪平面距离.
   */
  private float farClip = 100f;

  /**
   * 摄像机的视野范围.
   */
  private float fieldOfView = 45.0f;

  private int viewWidth = 0;
  private int viewHeight = 0;

  /**
   * 移动速度.
   */
  private float speed;
  /**
   * 鼠标灵敏度.
   */
  private float rotateSensitivity;

  /**
   * 默认位置为世界原点，目标方向为 Z 轴负方向.
   */
  public Camera() {
    this(new Vector3f());
  }

  /**
   * 指定摄像机的初始位置，目标方向为 Z 轴负方向.
   *
   * @param position 摄像机的初始位置.
   */
  public Camera(Vector3f position) {
    this(position, new Vector3f(0, 1, 0), new Vector3f(0, -90, 0));
  }

  /**
   * 指定摄像机的初始位置和视线方向.
   *
   * @param position 摄像机的初始位置
   * @param rotation 摄像机的初始旋转
   */
  public Camera(Vector3f position, Vector3f up, Vector3f rotation) {
    this.position = position;

    this.target = new Vector3f();
    this.worldUp = up;
    this.up = new Vector3f();
    this.right = new Vector3f();

    this.rotation = rotation;

    this.viewMatrix = new Matrix4f();
    this.projectionMatrix = new Matrix4f();

    this.speed = 0.05f;
    this.rotateSensitivity = 0.05f;
    updateCameraVectors();
  }

  /**
   * 根据摄像机视线方向、旋转角度，位置计算视图矩阵,.
   *
   * @return 当前的 View matrix
   * @see Matrix4f#lookAt
   */
  public Matrix4f getViewMatrix() {
    return viewMatrix.setLookAt(position, position.add(target, DEST), up);
  }

  /**
   * 懒更新，获取矩阵的时候才会更新.
   *
   * @return 投影矩阵
   */
  public Matrix4f getProjectionMatrix() {
    return projectionMatrix.setPerspective(fieldOfView,
                                           (float) viewWidth / (float) viewHeight,
                                           nearClip,
                                           farClip);
  }

  //////////////////////////////////////////////////
  // Function method
  //////////////////////////////////////////////////

  public void forward() {
    position.add(target.mul(speed, DEST));
  }

  public void backward() {
    position.sub(target.mul(speed, DEST));
  }

  public void toLeft() {
    position.sub(right.mul(speed, DEST));
  }

  public void toRight() {
    position.add(right.mul(speed, DEST));
  }

  public void toUp() {
    position.add(up.mul(speed, DEST));
  }

  public void toDown() {
    position.sub(up.mul(speed, DEST));
  }

  public void rotate(double pitch, double yaw) {
    rotate((float) pitch, (float) yaw);
  }

  /**
   * 根据俯仰和偏好角度更新 target 向量. 在俯仰角达到正负 90度时，会导致万向节死锁的问题，因此限制它.
   *
   * @param pitch 俯仰角度
   * @param yaw   偏航角度
   */
  public void rotate(float pitch, float yaw) {
    rotation.x += pitch * rotateSensitivity;
    rotation.y += yaw * rotateSensitivity;

    if (rotation.x > 89.0f) {
      rotation.x = 89.0f;
    }
    if (rotation.x < -89.0f) {
      rotation.x = -89.0f;
    }
    updateCameraVectors();
  }

  private void updateCameraVectors() {
    target.x = cos(toRadians(rotation.y)) * cos(toRadians(rotation.x));
    target.y = sin(toRadians(rotation.x));
    target.z = sin(toRadians(rotation.y)) * cos(toRadians(rotation.x));
    target.normalize();

    target.cross(worldUp, right).normalize();
    right.cross(target, up).normalize();
  }
}
