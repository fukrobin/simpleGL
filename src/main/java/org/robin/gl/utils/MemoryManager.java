package org.robin.gl.utils;

import java.util.HashSet;
import java.util.Set;
import org.robin.gl.ShaderProgram;

/**
 * 管理常用组件的内存释放.
 *
 * @author fkrobin
 * @date 2022/3/13
 */
public class MemoryManager {

  private static final Set<ShaderProgram> SHADERS = new HashSet<>();

  public static void manageShaderProgram(ShaderProgram shaderProgram) {
    SHADERS.add(shaderProgram);
  }

  /**
   * 创建并管理着色器的内存.
   */
  public static ShaderProgram createShader(String vertexShaderPath, String fragmentShaderPath) {
    ShaderProgram shader = new ShaderProgram(vertexShaderPath, fragmentShaderPath);
    SHADERS.add(shader);
    return shader;
  }

  public static void cleanup() {
    SHADERS.forEach(ShaderProgram::cleanup);
  }
}
