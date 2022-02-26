package org.robin.gl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Scanner;
import org.lwjgl.system.MemoryUtil;

/**
 * load resource from classpath.
 */
public class ResourcesUtil {

  /**
   * load file content to single string.
   *
   * @param fileName classpath filename.
   * @return file content
   * @throws IOException throw if file no found.
   */
  public static String getFileContent(String fileName) throws IOException {
    String result;
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try (InputStream in = Objects.requireNonNull(classLoader.getResourceAsStream(fileName));
        Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
      result = scanner.useDelimiter("\\A").next();
    } catch (NullPointerException e) {
      throw new IOException("Cannot find file '" + fileName + "'");
    }

    return result;
  }

  /**
   * load file content to single string.
   *
   * @param stream file input stream
   * @return file content string.
   */
  public static String getFileContent(InputStream stream) {
    String result;
    try (Scanner scanner = new Scanner(stream, java.nio.charset.StandardCharsets.UTF_8.name())) {
      result = scanner.useDelimiter("\\A").next();
    }

    return result;
  }

  /**
   * 加载资源，以ByteBuffer返回结果，此方法返回的 ByteBuffer 不安全，必须调用 {@link MemoryUtil#memFree(Buffer)}手动释放.
   *
   * @param resourcePath 资源路径
   * @return {@link ByteBuffer}
   */
  public static ByteBuffer getFileToByteBuffer(String resourcePath) throws IOException {
    URL url = Thread.currentThread().getContextClassLoader().getResource(resourcePath);
    if (url == null) {
      throw new IOException("cannot find resource from '" + resourcePath + "'");
    }

    int resourceSize = url.openConnection().getContentLength();

    ByteBuffer resource = MemoryUtil.memAlloc(resourceSize);

    try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
      int b;
      do {
        b = bis.read();
        if (b != -1) {
          resource.put((byte) (b & 0xff));
        }
      } while (b != -1);
    } catch (IOException e) {
      MemoryUtil.memFree(resource);
      throw new IOException("load resource from '" + resourcePath + "' fail");
    }

    resource.flip();
    return resource;
  }
}
