package org.robin.gl.scene;

import lombok.Builder;
import org.joml.Vector3f;

/**
 * 平行光光源，如太阳.
 *
 * @author fkrobin
 * @date 2022/3/6
 */
public class ParallelLight extends Light {

  public ParallelLight(Vector3f color, Vector3f direction) {
    super(color, direction, new Vector3f());
  }
}
