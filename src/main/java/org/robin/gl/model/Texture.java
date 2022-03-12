package org.robin.gl.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fkrobin
 * @date 2022/3/7
 */
@Data
public class Texture {

  private int id;
  private TextureType type;
  private String path;

  /**
   * 纹理类型：漫反射贴图、高光贴图等.
   */
  public enum TextureType {
    DIFFUSE_MAP, SPECULAR_MAP
  }
}
