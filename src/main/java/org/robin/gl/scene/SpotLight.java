package org.robin.gl.scene;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.joml.Math;
import org.joml.Vector3f;

/**
 * 聚光灯.
 *
 * @author fkrobin
 * @date 2022/3/6
 */
@Setter
@Getter
public class SpotLight extends PointLight {

  private float cutOff;
  private float outerCutOff;

  /**
   * 放射光光源.
   *
   * @param color     光源颜色
   * @param direction 光源的方向
   * @param position  光源的位置
   */
  public SpotLight(Vector3f color, Vector3f direction, Vector3f position) {
    super(color, direction, position);
    setCutOff(12.5f);
    setOuterCutOff(17.5f);
  }

  public void setCutOff(float cutOff) {
    this.cutOff = Math.cos(Math.toRadians(cutOff));
  }

  public void setOuterCutOff(float outerCutOff) {
    this.outerCutOff = Math.cos(Math.toRadians(outerCutOff));
  }
}
