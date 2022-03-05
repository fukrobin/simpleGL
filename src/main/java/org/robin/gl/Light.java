package org.robin.gl;

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

  private float[] colorFloats = new float[]{1.0f, 1.0f, 1.0f};
  private Vector3f color = new Vector3f(colorFloats);
  private Vector3f ambient = new Vector3f(0.2f);
  private Vector3f diffuse = new Vector3f(0.5f);
  private Vector3f specular = new Vector3f(1.0f);

  private Vector3f direction = new Vector3f(-0.2f, -1.0f, -0.3f);
  private Vector3f position = new Vector3f();

  /**
   * 更新光源的各个分量.
   */
  public void updateColor() {
    color.set(colorFloats);
    color.mul(0.5f, diffuse);
    color.mul(0.2f, ambient);
  }
}
