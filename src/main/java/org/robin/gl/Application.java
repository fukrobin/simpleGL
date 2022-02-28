package org.robin.gl;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
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

  private final Vector3f cameraInc = new Vector3f();

  private final Matrix4f projectionMatrix;
  private final Camera camera;
  private final AtomicBoolean firstCursor = new AtomicBoolean(true);

  private long window;
  private int width;
  private int height;

  private double lastCursorPosX;
  private double lastCursorPosY;
  private float lastFrameTime;

  private int boxVao;
  private int lightVao;
  private int vbo;


  private final Vector3f objectColor = new Vector3f(1.0f, 0.5f, 0.31f);
  private final Vector3f lightColor = new Vector3f(1.0f, 0.0f, 1.0f);

  private ShaderProgram boxShader;
  private ShaderProgram lightShader;

  /**
   * 设置 Window 的一些初始值.
   */
  public Application() {
    projectionMatrix = new Matrix4f();

    width = 800;
    height = 600;
    camera = new Camera(new Vector3f(0, 0, 3));
  }

  public static void main(String[] args) {
    new Application().run();
  }

  /**
   * 运行 Application，自动调用所有生命周期方法.
   */
  public void run() {
    System.out.println("Hello LWJGL " + Version.getVersion() + "!");

    try {
      init();
      loop();

      // Free the window callbacks and destroy the window
      glfwFreeCallbacks(window);
      glfwDestroyWindow(window);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      glfwTerminate();
      Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
  }

  private void init() throws Exception {
    initGLFW();
    initOpenGL();

    // Make the window visible
    glfwShowWindow(window);
  }

  /**
   * 初始化 GLFW.
   */
  @SuppressWarnings("CheckStyle")
  private void initGLFW() {
    GLFWErrorCallback.createPrint(System.err).set();
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

    window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    glfwSetKeyCallback(window, keyCallback());
    glfwSetWindowSizeCallback(window, windowSizeCallback());
    glfwSetFramebufferSizeCallback(window, framebufferSizeCallback());

    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    glfwSetCursorPosCallback(window, cursorPosCallback());

    centerWindow();

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);
  }

  /**
   * 将 Window 在屏幕中居中放置.
   */
  public void centerWindow() {
    GLFWVidMode videoMode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));

    glfwSetWindowPos(
        window,
        (videoMode.width() - width) / 2,
        (videoMode.height() - height) / 2
    );
  }

  @SuppressWarnings("CheckStyle")
  private void initOpenGL() throws IOException {
    GL.createCapabilities();
    glEnable(GL_DEPTH_TEST);
    glClearColor(0.3f, 1.0f, 1.0f, 0.0f);
    setupShader();
    setupVao();
  }

  private GLFWWindowSizeCallbackI windowSizeCallback() {
    return (window1, width1, height1) -> {
      this.width = width1;
      this.height = height1;
      updateProjectionMatrix();
    };
  }

  private GLFWCursorPosCallbackI cursorPosCallback() {
    return (window1, posX, posY) -> {
      if (firstCursor.getAndSet(false)) {
        lastCursorPosX = posX;
        lastCursorPosY = posY;
      }
      double offsetX = posX - lastCursorPosX;
      double offsetY = lastCursorPosY - posY;
      lastCursorPosX = posX;
      lastCursorPosY = posY;
      camera.rotate(offsetY, offsetX);
    };
  }

  private GLFWFramebufferSizeCallbackI framebufferSizeCallback() {
    return (window1, sizeX, sizeY) -> glViewport(0, 0, sizeX, sizeY);
  }

  private GLFWKeyCallbackI keyCallback() {
    return (window1, key, scancode, action, mods) -> {

      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true);
      }
    };
  }

  private void setupVao() {
    setupBoxVao();
    setupLightVao();
  }

  private void setupBoxVao() {
    boxVao = glGenVertexArrays();
    glBindVertexArray(boxVao);

    vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);

    FloatBuffer vertices = Objects.requireNonNull(Util.loadCsvToFloatBuffer("vertices.csv"));
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    // position attribute
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
    glEnableVertexAttribArray(0);

    glBindVertexArray(0);
  }

  private void setupLightVao() {
    lightVao = glGenVertexArrays();
    glBindVertexArray(lightVao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
    glEnableVertexAttribArray(0);

    glBindVertexArray(0);
  }

  private void setupShader() throws IOException {
    boxShader = new ShaderProgram();
    boxShader.createVertexShader(ResourcesUtil.getFileContent("box.vs.glsl"));
    boxShader.createFragmentShader(ResourcesUtil.getFileContent("box.fs.glsl"));
    boxShader.link();

    lightShader = new ShaderProgram();
    lightShader.createVertexShader(ResourcesUtil.getFileContent("box.vs.glsl"));
    lightShader.createFragmentShader(ResourcesUtil.getFileContent("light.fs.glsl"));
    lightShader.link();
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

  private void render() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    boxShader.bind();
    Matrix4f viewMatrix = camera.getViewMatrix();
    boxShader.setUniform("view", viewMatrix);

    glBindVertexArray(boxVao);
    glDrawArrays(GL_TRIANGLES, 0, 36);

    lightShader.bind();
    lightShader.setUniform("view", viewMatrix);

    glBindVertexArray(lightVao);
    glDrawArrays(GL_TRIANGLES, 0, 36);

    glBindVertexArray(0);
  }

  @SuppressWarnings("checkstyle:EmptyCatchBlock")
  private void loop() {
    try {

      updateProjectionMatrix();
      initShader();

      while (!glfwWindowShouldClose(window)) {
        preFrame();

        render();

        postFrame();
        glfwSwapBuffers(window);
        glfwPollEvents();
      }
    } finally {
      if (boxShader != null) {
        boxShader.cleanup();
      }
      glDeleteBuffers(boxVao);
      glDeleteBuffers(vbo);
    }
  }

  private void initShader() {
    boxShader.bind();
    boxShader.setUniform("objectColor", objectColor);
    boxShader.setUniform("lightColor", lightColor);
    Matrix4f modelMatrix = new Matrix4f();
    boxShader.setUniform("model", modelMatrix);

    glBindVertexArray(boxVao);
    glDrawArrays(GL_TRIANGLES, 0, 36);

    lightShader.bind();
    modelMatrix.identity().translate(1.2f, 1.0f, 2.0f).scale(0.2f);
    lightShader.setUniform("model", modelMatrix);
    lightShader.setUniform("lightColor", lightColor);
  }

  private void updateProjectionMatrix() {
    projectionMatrix.setPerspective(Math.toRadians(45.0f), (float) width / (float) height, 0.1f,
        100f);

    boxShader.bind();
    boxShader.setUniform("projection", projectionMatrix);

    lightShader.bind();
    lightShader.setUniform("projection", projectionMatrix);
  }

  protected void preFrame() {
    float currentFrameTime = (float) glfwGetTime();
    float deltaTime = currentFrameTime - lastFrameTime;
    lastFrameTime = currentFrameTime;
    camera.setSpeed(2.5f * deltaTime);

    cameraInc.set(0);
    if (isKeyPressed(GLFW_KEY_W)) {
      camera.forward();
    } else if (isKeyPressed(GLFW_KEY_S)) {
      camera.backward();
    }
    if (isKeyPressed(GLFW_KEY_A)) {
      camera.toLeft();
    } else if (isKeyPressed(GLFW_KEY_D)) {
      camera.toRight();
    }
    if (isKeyPressed(GLFW_KEY_Z)) {
      camera.toUp();
    } else if (isKeyPressed(GLFW_KEY_X)) {
      camera.toDown();
    }
  }

  public boolean isKeyPressed(int key) {
    return glfwGetKey(window, key) == GLFW_PRESS;
  }

  protected void postFrame() {

  }

}
