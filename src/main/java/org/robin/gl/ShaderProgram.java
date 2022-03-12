package org.robin.gl;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL31.glUniformBlockBinding;

import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

/**
 * 着色器封装类，在创建着色器、link 期间会自动检测是否操作失败，并在失败时抛出 {@link ShaderException} 异常.
 *
 * @author fkrobin
 */
public class ShaderProgram {

  private final Map<String, Integer> uniforms;
  private final int programId;

  private int vertexShaderId;
  private int fragmentShaderId;

  /**
   * create shader program, throw exception if create failed.
   */
  public ShaderProgram() {
    programId = glCreateProgram();
    throwIfInvalid(programId, "Can't create shader!");
    uniforms = new HashMap<>();
  }

  public int getVertexShaderId() {
    return vertexShaderId;
  }

  public void setVertexShaderId(int vertexShaderId) {
    this.vertexShaderId = vertexShaderId;
  }

  public int getFragmentShaderId() {
    return fragmentShaderId;
  }

  public void setFragmentShaderId(int fragmentShaderId) {
    this.fragmentShaderId = fragmentShaderId;
  }

  /**
   * create uniform and cache uniform location.
   *
   * @param uniformName shader uniform name.
   */
  public void createUniform(String uniformName) {
    int uniformLocation = glGetUniformLocation(programId, uniformName);
    throwIfInvalid(uniformLocation, "Can't find uniform: " + uniformName);
    uniforms.put(uniformName, uniformLocation);
  }

  /**
   * bind uniform block.
   *
   * @param blockName uniform block name
   * @param bindPoint uniform bind point
   */
  public void bindUniformBlock(String blockName, int bindPoint) {
    int blockLocation = glGetUniformBlockIndex(programId, blockName);
    throwIfInvalid(blockLocation, "Can't find uniform block: " + blockName);
    glUniformBlockBinding(programId, blockLocation, bindPoint);
  }

  private Integer getLocation(String uniformName) {
    Integer location = uniforms.computeIfAbsent(uniformName,
                                               s -> glGetUniformLocation(programId, s));

    if (location == -1) {
      throw new ShaderException("Can't find uniform: " + uniformName);
    }
    return location;
  }

  /**
   * set matrix uniform value.
   *
   * @param uniformName uniform name
   * @param value       matrix for float
   */
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

  /**
   * query and enable shader attribute.
   *
   * @param attributeName shader attribute name
   * @return shader attribute location
   */
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

  /**
   * link shader.
   */
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

  /**
   * clean shader.
   */
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
   * 当 value 小于 0 时将会抛出异常.
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
