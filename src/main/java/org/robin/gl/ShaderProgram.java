package org.robin.gl;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL31.*;

import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

/**
 * @author fkrobin
 */
public class ShaderProgram {

  private final Map<String, Integer> uniforms;
  private final int programId;

  private int vertexShaderId;
  private int fragmentShaderId;

  public ShaderProgram() {
    programId = glCreateProgram();
    throwIfInvalid(programId, "Can't create shader!");
    uniforms = new HashMap<>();
  }

  public void createUniform(String uniformName) {
    int uniformLocation = glGetUniformLocation(programId, uniformName);
    throwIfInvalid(uniformLocation, "Can't find uniform: " + uniformName);
    uniforms.put(uniformName, uniformLocation);
  }

  public void bindUniformBlock(String blockName, int bindPoint) {
    int blockLocation = glGetUniformBlockIndex(programId, blockName);
    throwIfInvalid(blockLocation, "Can't find uniform block: " + blockName);
    glUniformBlockBinding(programId, blockLocation, bindPoint);
  }
  
  private Integer getLocation(String uniformName) {
    return uniforms.computeIfAbsent(uniformName, s -> glGetUniformLocation(programId, s));
  }

  public void setUniform(String uniformName, Matrix4f value) {
    // Dump the matrix into a float buffer
    try (MemoryStack stack = MemoryStack.stackPush()) {
      glUniformMatrix4fv(getLocation(uniformName), false, value.get(stack.mallocFloat(16)));
    }
  }

  public void setUniform(String uniformName, int value) {
    glUniform1i(getLocation(uniformName), value);
  }


  public void setUniform(String uniformName, float value) {
    glUniform1f(getLocation(uniformName), value);
  }

  public void setUniform(String uniformName, Vector3f value) {
    glUniform3f(getLocation(uniformName), value.x, value.y, value.z);
  }

  public void setUniform(String uniformName, Vector4f value) {
    glUniform4f(getLocation(uniformName), value.x, value.y, value.z, value.w);
  }

  public int getAttribLocation(String attributeName) {
    return glGetAttribLocation(programId, attributeName);
  }

  public void enableVertexAttrib(int attribLocation) {
    glEnableVertexAttribArray(attribLocation);
  }

  public int getAndEnableVertexAttrib(String attributeName) {
    int attribLocation = getAttribLocation(attributeName);
    enableVertexAttrib(attribLocation);
    return attribLocation;
  }

  public void createVertexShader(String shaderCode) {
    vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
  }

  public void createFragmentShader(String shaderCode) {
    fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
  }

  protected int createShader(String shaderCode, int shaderType) {
    int shaderId = glCreateShader(shaderType);
    throwIfInvalid(shaderId, "Can't create shader.");

    glShaderSource(shaderId, shaderCode);
    glCompileShader(shaderId);

    throwIfInvalid(glGetShaderi(shaderId, GL_COMPILE_STATUS),
        "Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));

    glAttachShader(programId, shaderId);

    return shaderId;
  }

  public void link() {
    glLinkProgram(programId);
    throwIfInvalid(glGetProgrami(programId, GL_LINK_STATUS),
        "Can't link shader program: " + glGetProgramInfoLog(programId, 1024));

    if (vertexShaderId != 0) {
      glDetachShader(programId, vertexShaderId);
    }
    if (fragmentShaderId != 0) {
      glDetachShader(programId, fragmentShaderId);
    }

    glValidateProgram(programId);
    throwIfInvalid(glGetProgrami(programId, GL_VALIDATE_STATUS),
        "Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
  }

  public void bind() {
    glUseProgram(programId);
  }

  public void unbind() {
    glUseProgram(0);
  }

  public void cleanup() {
    unbind();
    if (vertexShaderId != 0) {
      glDeleteShader(vertexShaderId);
    }
    if (fragmentShaderId != 0) {
      glDeleteShader(fragmentShaderId);
    }
    if (programId != 0) {
      glDeleteProgram(programId);
    }
  }

  /**
   * 当 value 小于 0 时将会抛出异常
   *
   * @param value        任何小于0的值都表示出现了错误
   * @param errorMessage 发生错误时，给出的提示信息
   */
  private void throwIfInvalid(int value, String errorMessage) {
    if (value <= 0) {
      throw new ShaderException(errorMessage);
    }
  }
}
