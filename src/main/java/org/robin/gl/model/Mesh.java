package org.robin.gl.model;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.robin.gl.utils.Util.toArray;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.robin.gl.ShaderProgram;
import org.robin.gl.model.Texture.TextureType;

/**
 * 每个Mesh对应着不可再细分的绘图单位.
 *
 * @author fkrobin
 * @date 2022/3/7
 */
@Getter
public class Mesh {

  private final List<Float> vertices;
  private final List<Float> normals;
  private final List<Float> texturesCoords;
  private final List<Integer> indices;
  private final List<Texture> textures;

  private int vao;
  private int vbo;
  private int ebo;

  public Mesh(List<Float> vertices,
              List<Float> normals,
              List<Float> texturesCoords,
              List<Integer> indices,
              List<Texture> textures) {
    this.vertices = vertices;
    this.normals = normals;
    this.texturesCoords = texturesCoords;
    this.indices = indices;
    this.textures = textures;

    setupMesh();
  }

  private void setupMesh() {
    vao = glGenVertexArrays();
    vbo = glGenBuffers();
    ebo = glGenBuffers();

    glBindVertexArray(vao);

    long size = (vertices.size() + normals.size() + texturesCoords.size()) * 4L;
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, size, GL_STATIC_DRAW);

    glBufferSubData(GL_ARRAY_BUFFER, 0, toArray(vertices));
    glBufferSubData(GL_ARRAY_BUFFER, vertices.size() * 4L, toArray(normals));
    glBufferSubData(GL_ARRAY_BUFFER, (vertices.size() + normals.size()) * 4L,
                    toArray(texturesCoords));

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
    glVertexAttribPointer(1, 3, GL_FLOAT, false, 3 * 4, vertices.size() * 4L);
    glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * 4, (vertices.size() + normals.size()) * 4L);

    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    glEnableVertexAttribArray(2);

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, convertToIntBuffer(indices), GL_STATIC_DRAW);
    glBindVertexArray(0);
  }

  private FloatBuffer convertToFloatBuffer(List<Vertex> vertices) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.size() * 8);
    vertices.forEach(vertex -> {
      vertex.getPosition().get(buffer);
      vertex.getNormal().get(buffer);
      vertex.getTextureCoordinates().get(buffer);
    });
    return buffer;
  }

  private IntBuffer convertToIntBuffer(List<Integer> indices) {
    IntBuffer intBuffer = BufferUtils.createIntBuffer(indices.size());
    indices.forEach(intBuffer::put);
    intBuffer.flip();
    return intBuffer;
  }

  public void draw(ShaderProgram shader) {
    int diffuseNum = 0;
    int specularNum = 0;
    for (int i = 0; i < textures.size(); i++) {
      Texture texture = textures.get(i);
      String name;
      if (texture.getType() == TextureType.DIFFUSE_MAP) {
        name = "material.diffuseMap" + diffuseNum++;
      } else {
        // continue;
        name = "material.specularMap" + specularNum++;
      }
      glActiveTexture(GL_TEXTURE0 + i);
      glBindTexture(GL_TEXTURE_2D, texture.getId());
      shader.setUniform(name, i);
    }

    glBindVertexArray(vao);
    glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
    glBindVertexArray(0);

    glActiveTexture(GL_TEXTURE0);
  }

  /**
   * 清理顶点缓冲：vao, vbo, ebo.
   */
  public void cleanup() {
    glDeleteBuffers(vao);
    glDeleteBuffers(vbo);
    glDeleteBuffers(ebo);
  }

  public String print() {
    System.out.println("getVertices");
    this.getVertices().forEach(System.out::println);
    System.out.println("getIndices");
    this.getIndices().forEach(System.out::println);
    System.out.println("Texture");
    this.getTextures().forEach(System.out::println);

    return "Mesh(vertices=" + this.getVertices() + ", indices=" + this.getIndices() + ", textures="
        + this.getTextures() + ", vao=" + this.getVao() + ", vbo=" + this.getVbo() + ", ebo="
        + this.getEbo() + ")";
  }
}
