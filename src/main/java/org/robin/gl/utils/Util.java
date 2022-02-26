package org.robin.gl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.lwjgl.BufferUtils;

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
        csvRecord.stream().map(Float::valueOf).forEach(verticesList::add);
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
        csvRecord.stream().map(Integer::valueOf).forEach(verticesList::add);
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

}
