package org.robin.gl.utils;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

/**
 * Util.
 *
 * @author fkrobin.
 * @date 2022/2/26
 */
public class Util {

  /**
   * 从数据文件中加载数据，并返回为 {@link FloatBuffer}，数据文件应该是简单的 csv 文件.
   *
   * @param verticesFilename 顶点数据文件名称
   * @return {@link FloatBuffer}
   */
  public static FloatBuffer loadCsvToFloatBuffer(String verticesFilename) {
    try {
      InputStream stream = Objects.requireNonNull(
          Util.class.getClassLoader().getResourceAsStream(verticesFilename));
      CSVParser csvRecords = CSVFormat.DEFAULT.parse(new InputStreamReader(stream));
      List<Float> verticesList = new ArrayList<>();
      for (CSVRecord csvRecord : csvRecords) {
        csvRecord.stream().map(String::trim).map(Float::valueOf).forEach(verticesList::add);
      }
      FloatBuffer buffer = BufferUtils.createFloatBuffer(verticesList.size());
      verticesList.forEach(buffer::put);
      buffer.flip();
      return buffer;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 从数据文件中加载数据，并返回为 {@link IntBuffer}，数据文件应该是简单的 csv 文件.
   *
   * @param filename 数据文件名称
   * @return {@link IntBuffer}
   */
  public static IntBuffer loadCsvToIntBuffer(String filename) {
    try {
      InputStream stream = Objects.requireNonNull(load(filename));
      CSVParser csvRecords = CSVFormat.DEFAULT.parse(new InputStreamReader(stream));
      List<Integer> verticesList = new ArrayList<>();
      for (CSVRecord csvRecord : csvRecords) {
        csvRecord.stream().map(String::trim).map(Integer::valueOf).forEach(verticesList::add);
      }
      IntBuffer buffer = BufferUtils.createIntBuffer(verticesList.size());
      verticesList.forEach(buffer::put);
      buffer.flip();
      return buffer;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 从 classpath 下加载文件.
   *
   * @param filename 需要加载的文件名
   * @return {@link InputStream}
   */
  public static InputStream load(String filename) {
    return Util.class.getClassLoader().getResourceAsStream(filename);
  }


  public static String stringVectors(Vector3f vector3f) {
    return String.format("x: %.1f, y: %.1f, z: %.1f", vector3f.x, vector3f.y, vector3f.z);
  }

  /**
   * 创建一个 {@link org.lwjgl.opengl.GL11#GL_TEXTURE_2D} 类型的纹理.
   *
   * @param imageUrl 纹理路径
   * @return 纹理ID
   */
  public static int texture2D(String imageUrl) {
    ByteBuffer imageData = null;
    try (MemoryStack stack = stackPush()) {
      IntBuffer width = stack.mallocInt(1);
      IntBuffer height = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);
      STBImage.stbi_set_flip_vertically_on_load(true);
      imageData = STBImage.stbi_load(imageUrl, width, height, channels, 0);
      if (imageData == null) {
        throw new RuntimeException("Could not load texture: " + imageUrl);
      }
      int c = channels.get(0);
      int format = 0;
      if (c == 1) {
        format = GL_RED;
      } else if (c == 3) {
        format = GL_RGB;
      } else if (c == 4) {
        format = GL_RGBA;
      }

      int texture = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, texture);
      glTexImage2D(GL_TEXTURE_2D, 0, format, width.get(0), height.get(0), 0, format,
                   GL_UNSIGNED_BYTE, imageData);
      glGenerateMipmap(GL_TEXTURE_2D);
      // 为当前绑定的纹理对象设置环绕、过滤方式
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      return texture;
    } finally {
      if (imageData != null) {
        STBImage.stbi_image_free(imageData);
      }
    }
  }

  /**
   * Convert {@link List} to array.
   */
  public static float[] toArray(List<Float> list) {
    int size = list != null ? list.size() : 0;
    float[] floatArr = new float[size];
    for (int i = 0; i < size; i++) {
      floatArr[i] = list.get(i);
    }
    return floatArr;
  }
}
