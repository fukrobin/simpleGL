package org.robin.gl;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.robin.gl.utils.Util;

/**
 * Simple OpEnGL application.
 *
 * @author fkrobin.
 */
public class Application {

  // The window handle
  private long window;

  private int width;
  private int height;

  private int texture1;
  private int texture2;

  private final Matrix4f viewMatrix;
  private final Matrix4f projectionMatrix;

  public static void main(String[] args) {
    new Application().run();
  }

  /**
   * 设置 Window 的一些初始值.
   */
  public Application() {
    viewMatrix = new Matrix4f();
    projectionMatrix = new Matrix4f();

    width = 800;
    height = 600;
  }

  /**
   * 运行 Application，自动调用所有生命周期方法.
   */
  public void run() {
    System.out.println("Hello LWJGL " + Version.getVersion() + "!");

    init();
    loop();

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    Objects.requireNonNull(glfwSetErrorCallback(null)).free();
  }

  private void init() {
    // Set up an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    // Configure GLFW
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

    // Create the window
    window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    // Set up a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true);
      }
      boolean pressed = action == GLFW_PRESS || action == GLFW_REPEAT;
      if (pressed) {
        float step = 0.1f;
        switch (key) {
          case GLFW_KEY_UP:
            viewMatrix.translate(0, 0, step);
            break;
          case GLFW_KEY_DOWN:
            viewMatrix.translate(0, 0, -step);
            break;
          case GLFW_KEY_LEFT:
            viewMatrix.translate(step, 0, 0);
            break;
          case GLFW_KEY_RIGHT:
            viewMatrix.translate(-step, 0, 0);
            break;
          case GLFW_KEY_Z:
            viewMatrix.translate(0, step, 0);
            break;
          case GLFW_KEY_X:
            viewMatrix.translate(0, -step, 0);
            break;
          default:
        }
      }
    });
    glfwSetWindowSizeCallback(window, (window1, width1, height1) -> {
      this.width = width1;
      this.height = height1;
      updateProjectionMatrix();
    });
    glfwSetFramebufferSizeCallback(window,
        (window1, width1, height1) -> glViewport(0, 0, width1, height1));

    // Get the thread stack and push a new frame
    try (MemoryStack stack = stackPush()) {
      IntBuffer width = stack.mallocInt(1); // int*
      IntBuffer height = stack.mallocInt(1); // int*
      glfwGetWindowSize(window, width, height);
      GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));

      glfwSetWindowPos(
          window,
          (videoMode.width() - width.get(0)) / 2,
          (videoMode.height() - height.get(0)) / 2
      );
    } // the stack frame is popped automatically

    // Make the OpenGL context current
    glfwMakeContextCurrent(window);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(window);
  }

  private int vao;
  private int vbo;

  private ShaderProgram shaderProgram;

  private void setupVao() {
    vao = glGenVertexArrays();
    glBindVertexArray(vao);

    vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);

//    int ebo = glGenBuffers();
//    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

    FloatBuffer vertices = Objects.requireNonNull(Util.loadCsvToFloatBuffer("vertices.csv"));
    IntBuffer indices = Objects.requireNonNull(Util.loadCsvToIntBuffer("indices.csv"));

    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    // position attribute
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
    glEnableVertexAttribArray(0);
    // texture coordinate attribute
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
    glEnableVertexAttribArray(1);
    glBindVertexArray(0);
  }

  private void setupShader() throws IOException {
    shaderProgram = new ShaderProgram();
    shaderProgram.createVertexShader(ResourcesUtil.getFileContent("vertexShader.glsl"));
    shaderProgram.createFragmentShader(ResourcesUtil.getFileContent("fragmentShader.glsl"));
    shaderProgram.link();
  }

  private int texture(String imageUrl) {
    try (MemoryStack stack = stackPush()) {
      int texture = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, texture);
      // 为当前绑定的纹理对象设置环绕、过滤方式
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      IntBuffer width = stack.mallocInt(1);
      IntBuffer height = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);
      STBImage.stbi_set_flip_vertically_on_load(true);
      ByteBuffer imageData1 = STBImage.stbi_load(imageUrl, width, height, channels, 0);
      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB,
          GL_UNSIGNED_BYTE, imageData1);
      glGenerateMipmap(GL_TEXTURE_2D);

      if (imageData1 != null) {
        STBImage.stbi_image_free(imageData1);
      }
      return texture;
    }
  }

  private static final List<Vector3f> positions = new ArrayList<>() {
    {
      add(new Vector3f(0.0f, 0.0f, 0.0f));
      add(new Vector3f(2.0f, 5.0f, -15.0f));
      add(new Vector3f(-1.5f, -2.2f, -2.5f));
      add(new Vector3f(-3.8f, -2.0f, -12.3f));
      add(new Vector3f(2.4f, -0.4f, -3.5f));
      add(new Vector3f(-1.7f, 3.0f, -7.5f));
      add(new Vector3f(1.3f, -2.0f, -2.5f));
      add(new Vector3f(1.5f, 2.0f, -2.5f));
      add(new Vector3f(1.5f, 0.2f, -1.5f));
      add(new Vector3f(-1.3f, 1.0f, -1.5f));
    }
  };

  private void render() {
    shaderProgram.bind();
    shaderProgram.setUniform("view", viewMatrix);
    shaderProgram.setUniform("projection", projectionMatrix);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture1);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, texture2);

    glBindVertexArray(vao);
//    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

    for (int i = 0; i < 10; i++) {
      Matrix4f modelMatrix = new Matrix4f();
      modelMatrix.translate(positions.get(i));
      modelMatrix.rotate(Math.toRadians(20.0f * i), new Vector3f(1.0f, 0.3f, 0.5f).normalize());
      shaderProgram.setUniform("model", modelMatrix);
      glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    glBindVertexArray(0);
    shaderProgram.unbind();
  }

  @SuppressWarnings("checkstyle:EmptyCatchBlock")
  private void loop() {
    GL.createCapabilities();
    glEnable(GL_DEPTH_TEST);
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

    try {
      setupShader();
      setupVao();
      createTexture();

      viewMatrix.translate(0, 0, -3);
      updateProjectionMatrix();

      while (!glfwWindowShouldClose(window)) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        render();

        glfwSwapBuffers(window);
        glfwPollEvents();
      }
    } catch (IOException ignored) {
      //noinspection CheckStyle
    } finally {
      if (shaderProgram != null) {
        shaderProgram.cleanup();
      }
      glDeleteBuffers(vao);
      glDeleteBuffers(vbo);
    }
  }

  private void updateProjectionMatrix() {
    projectionMatrix.setPerspective(Math.toRadians(45.0f), (float) width / (float) height, 0.1f,
        100f);
  }

  private void createTexture() {
    texture1 = texture("D:\\projects\\java\\simpleGL\\src\\main\\resources\\R-C.jfif");
    texture2 = texture("D:\\projects\\java\\simpleGL\\src\\main\\resources\\2.jpg");
    shaderProgram.bind();
    shaderProgram.setUniform("texture1", 0);
    shaderProgram.setUniform("texture2", 1);
  }

}
