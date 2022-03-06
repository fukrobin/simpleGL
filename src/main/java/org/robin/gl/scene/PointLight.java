package org.robin.gl.scene;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

/**
 * 点光源.
 *
 * @author fkrobin
 * @date 2022/3/6
 */
@Setter
@Getter
public class PointLight extends Light {

  private float constant = 1.0f;
  private float linear = 0.09f;
  private float quadratic = 0.032f;

  public PointLight(Vector3f color, Vector3f position) {
    super(color, new Vector3f(), position);
  }

  public PointLight(Vector3f color, Vector3f direction, Vector3f position) {
    super(color, direction, position);
  }
}
