package org.robin.gl;

import lombok.Data;
import org.joml.Vector3f;

/**
 * Material.
 *
 * @author fkrobin
 * @date 2022/3/3
 */
@Data
public class Material {

  private Vector3f ambient;
  private Vector3f diffuse;
  private Vector3f specular;

  /**
   * 初始化各个材质分量.
   */
  public Material() {
    ambient = new Vector3f();
    diffuse = new Vector3f();
    specular = new Vector3f();
  }

  public void setAmbient(Vector3f ambient) {
    this.ambient.set(ambient);
  }

  public void setAmbient(float[] ambient) {
    this.ambient.set(ambient);
  }

  public void setDiffuse(Vector3f diffuse) {
    this.diffuse.set(diffuse);
  }

  public void setDiffuse(float[] diffuse) {
    this.diffuse.set(diffuse);
  }

  public void setSpecular(Vector3f specular) {
    this.specular.set(specular);
  }

  public void setSpecular(float[] specular) {
    this.specular.set(specular);
  }
}
