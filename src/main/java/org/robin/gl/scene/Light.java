package org.robin.gl.scene;

import lombok.Data;
import org.joml.Vector3f;

/**
 * Light.
 *
 * @author fkrobin
 * @date 2022/3/6
 */
@Data
public class Light {

  private final float[] colorFloats;
  private final float[] positionFloats;
  private final float[] directionFloats;
  private final Vector3f color;
  private final Vector3f ambient;
  private final Vector3f diffuse;
  private final Vector3f specular;

  private final Vector3f direction;
  private final Vector3f position;

  /**
   * 初始化光源.
   *
   * @param color     光源颜色
   * @param direction 光源的方向，{@link PointLight} 可以忽略此属性
   * @param position  光源的位置，{@link ParallelLight} 可以忽略此属性
   */
  public Light(Vector3f color, Vector3f direction, Vector3f position) {
    this.color = color;

    this.ambient = new Vector3f();
    this.diffuse = new Vector3f();
    this.specular = new Vector3f(1.0f);

    this.direction = direction;
    this.position = position;

    this.colorFloats = new float[]{color.x, color.y, color.z};
    this.positionFloats = new float[]{position.x, position.y, position.z};
    this.directionFloats = new float[]{direction.x, direction.y, direction.z};
    updateColor();
  }

  public void setDirection(Vector3f direction) {
    this.direction.set(direction);
  }

  public void setPosition(Vector3f position) {
    this.position.set(position);
  }

  /**
   * 更新光源的各个分量.
   */
  public void updateColor() {
    color.set(colorFloats);
    color.mul(0.5f, diffuse);
    color.mul(0.2f, ambient);
  }
}
