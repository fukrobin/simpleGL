package org.robin.gl;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
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
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
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
import java.util.Objects;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

/**
 * @author fkrobin
 */
public class HelloWorld {

  // The window handle
  private long window;

  public static void main(String[] args) {
    new HelloWorld().run();
  }

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
    // Setup an error callback. The default implementation
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
    window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
      }
    });

    // Get the thread stack and push a new frame
    try (MemoryStack stack = stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*

      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(window, pWidth, pHeight);

      // Get the resolution of the primary monitor
      GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));

      // Center the window
      glfwSetWindowPos(
          window,
          (vidmode.width() - pWidth.get(0)) / 2,
          (vidmode.height() - pHeight.get(0)) / 2
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

  private ShaderProgram shaderProgram;

  private void setupVao() {
    int vbo = glGenBuffers();
    int ebo = glGenBuffers();
    vao = glGenVertexArrays();

    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

    try (MemoryStack stack = MemoryStack.stackPush()) {
      FloatBuffer vertices = stack.floats(
          //---- 位置 ----  ---- 颜色 ----     - 纹理坐标 -
          0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // 右上角
          0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,// 右下角
          -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,// 左下角
          -0.5f, 0.5f, 0.0f, 0.5f, 0.5f, 0.5f, 0.0f, 1.0f   // 左上角
      );
      IntBuffer indices = stack.ints(
          0, 1, 3,
          1, 2, 3
      );

      glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
      // position attribute
      glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0);
      glEnableVertexAttribArray(0);
      // position attribute
      glVertexAttribPointer(1, 3, GL_FLOAT, false, 32, 12);
      glEnableVertexAttribArray(1);
      // texture coord attribute
      glVertexAttribPointer(2, 2, GL_FLOAT, false, 32, 24);
      glEnableVertexAttribArray(2);
    }
    glBindVertexArray(0);
  }

  private void setupShader() throws IOException {
    shaderProgram = new ShaderProgram();
    shaderProgram.createVertexShader(ResourcesUtil.getFileContent("vertexShader.glsl"));
    shaderProgram.createFragmentShader(ResourcesUtil.getFileContent("fragmentShader.glsl"));
    shaderProgram.link();
  }

  private int texture1, texture2;

  private int texture(String imageUrl) {
    try (MemoryStack stack = stackPush()) {
      IntBuffer width = stack.mallocInt(1);
      IntBuffer height = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);
      STBImage.stbi_set_flip_vertically_on_load(true);
      ByteBuffer imageData1 = STBImage.stbi_load(imageUrl, width, height, channels, 0);

      int texture = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, texture);
      // 为当前绑定的纹理对象设置环绕、过滤方式
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB,
          GL_UNSIGNED_BYTE, imageData1);
      glGenerateMipmap(GL_TEXTURE_2D);

      if (imageData1 != null) {
        STBImage.stbi_image_free(imageData1);
      }
      return texture;
    }
  }

  private void render() {
    shaderProgram.bind();

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture1);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, texture2);

    glBindVertexArray(vao);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    glBindVertexArray(0);
    shaderProgram.unbind();
  }

  private void loop() {
    GL.createCapabilities();
//    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

    try {
      setupShader();
      setupVao();
      createTexture();

      while (!glfwWindowShouldClose(window)) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        render();

        glfwSwapBuffers(window);
        glfwPollEvents();
      }
    } catch (IOException ignored) {

    } finally {
      if (shaderProgram != null) {
        shaderProgram.cleanup();
      }
    }
  }

  private void createTexture() {
    texture1 = texture("D:\\projects\\java\\simpleGL\\src\\main\\resources\\dog.jpg");
    texture2 = texture("D:\\projects\\java\\simpleGL\\src\\main\\resources\\linux.png");
    shaderProgram.bind();
    shaderProgram.setUniform("texture1", 0);
    shaderProgram.setUniform("texture2", 1);
  }

}
