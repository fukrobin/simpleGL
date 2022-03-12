package org.robin.gl.model;

import java.text.NumberFormat;
import lombok.Builder;
import lombok.Data;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author fkrobin
 * @date 2022/3/7
 */
@Data
@Builder
public class Vertex {

  private static NumberFormat numberFormat = NumberFormat.getInstance();

  static {
    numberFormat.setMaximumFractionDigits(0);
  }

  private Vector3f position;
  private Vector3f normal;
  private Vector2f textureCoordinates;

  @Override
  public String toString() {
    return "Vertex{"
        + "position=" + position.toString(numberFormat)
        + ", normal=" + normal.toString(numberFormat)
        + ", textureCoordinates=" + textureCoordinates.toString(numberFormat)
        + '}';
  }
}
