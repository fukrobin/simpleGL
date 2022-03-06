package org.robin.gl;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_MAXIMIZED;
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
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_TEXTURE0;
import static org.lwjgl.opengl.GL15.GL_TEXTURE1;
import static org.lwjgl.opengl.GL15.glActiveTexture;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.robin.gl.utils.Util.stringVectors;
import static org.robin.gl.utils.Util.texture2D;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.robin.gl.scene.Light;
import org.robin.gl.scene.ParallelLight;
import org.robin.gl.scene.PointLight;
import org.robin.gl.scene.SpotLight;
import org.robin.gl.utils.Util;

/**
 * Simple OpEnGL application.
 *
 * @author fkrobin.
 */
public class Application {

  private static final Vector3f DEST_VECTOR = new Vector3f();
  private static final Matrix4f DEST_MATRIX = new Matrix4f();

  private final Matrix4f projectionMatrix;
  private final Camera camera;

  private long window;
  private int width;
  private int height;

  private final Vector3f clearColor = new Vector3f(0.1f);

  Vector3f[] cubePositions = new Vector3f[]{
      new Vector3f(0.0f, 0.0f, 0.0f),
      new Vector3f(2.0f, 5.0f, -15.0f),
      new Vector3f(-1.5f, -2.2f, -2.5f),
      new Vector3f(-3.8f, -2.0f, -12.3f),
      new Vector3f(2.4f, -0.4f, -3.5f),
      new Vector3f(-1.7f, 3.0f, -7.5f),
      new Vector3f(1.3f, -2.0f, -2.5f),
      new Vector3f(1.5f, 2.0f, -2.5f),
      new Vector3f(1.5f, 0.2f, -1.5f),
      new Vector3f(-1.3f, 1.0f, -1.5f)
  };

  //////////////////////////////////////////////////
  // Cursor
  //////////////////////////////////////////////////

  private final AtomicBoolean firstCursor = new AtomicBoolean(true);
  private final AtomicBoolean cursorHidden = new AtomicBoolean(true);
  private double lastCursorPosX;
  private double lastCursorPosY;
  private float lastFrameTime;

  //////////////////////////////////////////////////
  // Shader
  //////////////////////////////////////////////////

  private int boxVao;
  private int lightVao;
  private int vbo;

  private ShaderProgram boxShader;
  private ShaderProgram parallelLightShader;
  private ShaderProgram pointLightShader;

  //////////////////////////////////////////////////
  // light
  //////////////////////////////////////////////////

  private final Light parallelLight;
  private final PointLight pointLight;
  private final SpotLight spotLight;

  private final float[] pointLightQuadratic = new float[]{0.0019f};
  private final float[] pointLightLiner = new float[]{0.022f};

  //////////////////////////////////////////////////
  // Material
  //////////////////////////////////////////////////

  private final float[] materialShininess = new float[]{0.5f};
  /**
   * 漫反射贴图的纹理ID.
   */
  private int diffuseMap;
  /**
   * 镜面反射贴图的纹理ID.
   */
  private int specularMap;

  private final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
  private final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();

  /**
   * 设置 Window 的一些初始值.
   */
  public Application() {
    projectionMatrix = new Matrix4f();

    width = 1000;
    height = 600;
    camera = new Camera(new Vector3f(0, 0, 3),
        new Vector3f(0, 1, 0),
        new Vector3f(0, -90, 0));

    parallelLight = new ParallelLight(new Vector3f(1), new Vector3f(-0.2f, -1.0f, -0.3f));
    pointLight = new PointLight(new Vector3f(1, 0, 0), new Vector3f(0.7f, 0.2f, 2.0f));
    spotLight = new SpotLight(new Vector3f(0, 0, 1), camera.getTarget(), camera.getPosition());
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
    initImGui();
    initLightMap();

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
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

    window = glfwCreateWindow(width, height, "Hello World!", NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    glfwSetKeyCallback(window, keyCallback());
    glfwSetWindowSizeCallback(window, windowSizeCallback());
    glfwSetFramebufferSizeCallback(window, framebufferSizeCallback());

    disableCursor();
    glfwSetCursorPosCallback(window, cursorPosCallback());
    glfwSetMouseButtonCallback(window, mouseButtonCallback());

    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer widthBuffer = stack.mallocInt(1);
      IntBuffer heightBuffer = stack.mallocInt(1);
      glfwGetWindowSize(window, widthBuffer, heightBuffer);
      width = widthBuffer.get(0);
      height = heightBuffer.get(0);
    }

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);
  }

  private void initImGui() {
    ImGui.createContext();
    ImGui.styleColorsDark();
    ImGui.getIO();
    imGuiImplGlfw.init(window, true);
    imGuiImplGl3.init("#version 330 core");
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

    setupShader();
    setupVao();
  }

  private void initLightMap() {
    diffuseMap = texture2D("texture/box_diffuse_map.png");
    specularMap = texture2D("texture/box_specular_map.png");
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
      if (!cursorHidden.get()) {
        return;
      }

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

  private GLFWMouseButtonCallbackI mouseButtonCallback() {
    return (window1, button, action, mods) -> {

    };
  }

  public void disableCursor() {
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
  }

  private GLFWFramebufferSizeCallbackI framebufferSizeCallback() {
    return (window1, sizeX, sizeY) -> glViewport(0, 0, sizeX, sizeY);
  }

  private GLFWKeyCallbackI keyCallback() {
    return (window1, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true);
      }
      if (key == GLFW_KEY_LEFT_ALT && action == GLFW_RELEASE) {
        if (cursorHidden.getAndSet(false)) {
          glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
          disableCursor();
          glfwSetCursorPos(window, lastCursorPosX, lastCursorPosY);
          cursorHidden.set(true);
        }
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
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0);
    glEnableVertexAttribArray(0);
    // normal 法线
    glVertexAttribPointer(1, 3, GL_FLOAT, false, 32, 12);
    glEnableVertexAttribArray(1);
    // texture 纹理坐标
    glVertexAttribPointer(2, 2, GL_FLOAT, false, 32, 24);
    glEnableVertexAttribArray(2);

    glBindVertexArray(0);
  }

  private void setupLightVao() {
    lightVao = glGenVertexArrays();
    glBindVertexArray(lightVao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0);
    glEnableVertexAttribArray(0);

    glBindVertexArray(0);
  }

  private void setupShader() throws IOException {
    boxShader = new ShaderProgram();
    boxShader.createVertexShader(ResourcesUtil.getFileContent("shader/box.vs.glsl"));
    boxShader.createFragmentShader(ResourcesUtil.getFileContent("shader/box.fs.glsl"));
    boxShader.link();

    parallelLightShader = new ShaderProgram();
    parallelLightShader.createVertexShader(
        ResourcesUtil.getFileContent("shader/parallelLight.vs.glsl"));
    parallelLightShader.createFragmentShader(
        ResourcesUtil.getFileContent("shader/parallelLight.fs.glsl"));
    parallelLightShader.link();

    pointLightShader = new ShaderProgram();
    pointLightShader.createVertexShader(ResourcesUtil.getFileContent("shader/pointLight.vs.glsl"));
    pointLightShader.createFragmentShader(
        ResourcesUtil.getFileContent("shader/pointLight.fs.glsl"));
    pointLightShader.link();
  }

  private void initShader() {
    boxShader.bind();
    boxShader.setUniform("material.shininess", materialShininess[0]);
    boxShader.setUniform("material.diffuseMap", 0);
    boxShader.setUniform("material.specularMap", 1);

    glBindVertexArray(boxVao);
    glDrawArrays(GL_TRIANGLES, 0, 36);
  }

  @SuppressWarnings("checkstyle:EmptyCatchBlock")
  private void loop() {
    try {

      updateProjectionMatrix();
      initShader();

      while (!glfwWindowShouldClose(window)) {
        preFrame();

        render();
        renderImGui();

        postFrame();
        glfwSwapBuffers(window);
        glfwPollEvents();
      }
    } finally {
      imGuiImplGl3.dispose();
      imGuiImplGlfw.dispose();
      ImGui.destroyContext();
      if (boxShader != null) {
        boxShader.cleanup();
      }
      glDeleteBuffers(boxVao);
      glDeleteBuffers(vbo);
    }
  }

  //////////////////////////////////////////////////
  // Render
  //////////////////////////////////////////////////

  private final Matrix4f modelMatrix = new Matrix4f();
  private final Vector3f axis = new Vector3f(1.0f, 0.3f, 0.5f).normalize();

  private void render() {
    renderItem();

    renderLight();

    glBindVertexArray(0);
  }

  /**
   * 渲染可见的物体.
   */
  private void renderItem() {
    boxShader.bind();
    boxShader.setUniform("view", camera.getViewMatrix());
    boxShader.setUniform("viewPos", camera.getPosition());
    boxShader.setUniform("material.shininess", materialShininess[0]);

    boxShader.setUniform("parallelLight.direction", parallelLight.getDirection());
    boxShader.setUniform("parallelLight.ambient", parallelLight.getAmbient());
    boxShader.setUniform("parallelLight.diffuse", parallelLight.getDiffuse());
    boxShader.setUniform("parallelLight.specular", parallelLight.getSpecular());

    boxShader.setUniform("pointLight.position", pointLight.getPosition());
    boxShader.setUniform("pointLight.ambient", pointLight.getAmbient());
    boxShader.setUniform("pointLight.diffuse", pointLight.getDiffuse());
    boxShader.setUniform("pointLight.specular", pointLight.getSpecular());
    boxShader.setUniform("pointLight.constant", 1.0f);
    boxShader.setUniform("pointLight.linear", pointLightLiner[0]);
    boxShader.setUniform("pointLight.quadratic", pointLightQuadratic[0]);

    boxShader.setUniform("spotLight.position", spotLight.getPosition());
    boxShader.setUniform("spotLight.direction", spotLight.getDirection());
    boxShader.setUniform("spotLight.cutOff", spotLight.getCutOff());
    boxShader.setUniform("spotLight.outerCutOff", spotLight.getOuterCutOff());
    boxShader.setUniform("spotLight.ambient", spotLight.getAmbient());
    boxShader.setUniform("spotLight.diffuse", spotLight.getDiffuse());
    boxShader.setUniform("spotLight.specular", spotLight.getSpecular());
    boxShader.setUniform("spotLight.constant", 1.0f);
    boxShader.setUniform("spotLight.linear", pointLightLiner[0]);
    boxShader.setUniform("spotLight.quadratic", pointLightQuadratic[0]);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, diffuseMap);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, specularMap);

    glBindVertexArray(boxVao);
    for (int i = 0, cubePositionsLength = cubePositions.length; i < cubePositionsLength; i++) {
      Vector3f cubePosition = cubePositions[i];
      modelMatrix.identity().translate(cubePosition)
                 .rotate(Math.toRadians(20.0f * i), axis);
      boxShader.setUniform("model", modelMatrix);
      glDrawArrays(GL_TRIANGLES, 0, 36);
    }
  }

  /**
   * 渲染所有的光源.
   */
  private void renderLight() {
    glBindVertexArray(lightVao);

    parallelLightShader.bind();
    parallelLightShader.setUniform("view", camera.getViewMatrix());
    glDrawArrays(GL_TRIANGLES, 0, 36);

    pointLightShader.bind();
    DEST_MATRIX.identity().translate(pointLight.getPosition()).scale(0.2f);
    pointLightShader.setUniform("model", DEST_MATRIX);
    pointLightShader.setUniform("view", camera.getViewMatrix());
    pointLightShader.setUniform("lightColor", pointLight.getColor());
    glDrawArrays(GL_TRIANGLES, 0, 36);
  }

  private void renderImGui() {
    imGuiImplGlfw.newFrame();
    ImGui.newFrame();
    // ImGui.showDemoWindow();

    renderAttributeEditor();

    ImGui.render();
    imGuiImplGl3.renderDrawData(ImGui.getDrawData());

    if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
      final long backupWindowPtr = GLFW.glfwGetCurrentContext();
      ImGui.updatePlatformWindows();
      ImGui.renderPlatformWindowsDefault();
      GLFW.glfwMakeContextCurrent(backupWindowPtr);
    }
  }

  private void renderAttributeEditor() {
    ImGui.setNextWindowSize(350, 400, ImGuiCond.Once);
    ImGui.setNextWindowPos(width - 360, 10, ImGuiCond.Once);
    ImGui.begin("Attribute Editor");

    ImGui.labelText("Camera position", stringVectors(camera.getPosition()));
    ImGui.labelText("Camera rotation", stringVectors(camera.getRotation()));

    ImGui.text("Materials");
    ImGui.sliderFloat("shininess", materialShininess, 0, 1);

    ImGui.separator();
    ImGui.text("Parallel Light");
    if (ImGui.colorEdit3("Parallel-Color", parallelLight.getColorFloats())) {
      parallelLight.updateColor();
    }
    if (ImGui.sliderFloat3("Parallel-Direction", parallelLight.getDirectionFloats(), -1, 1)) {
      parallelLight.getDirection().set(parallelLight.getDirectionFloats());
    }

    ImGui.text("Point Light");
    if (ImGui.colorEdit3("Point-Color", pointLight.getColorFloats())) {
      pointLight.updateColor();
    }
    if (ImGui.sliderFloat3("Point-position", pointLight.getPositionFloats(), -5, 5)) {
      pointLight.getPosition().set(pointLight.getPositionFloats());
    }

    ImGui.text("Spot Light");
    if (ImGui.colorEdit3("Spot-Color", spotLight.getColorFloats())) {
      spotLight.updateColor();
    }
    ImGui.sliderFloat("Spot-liner", pointLightLiner, 0, 0.1f);
    ImGui.sliderFloat("Spot-quadratic", pointLightQuadratic, 0, 0.1f);

    ImGui.end();
  }

  private void updateProjectionMatrix() {
    projectionMatrix.setPerspective(Math.toRadians(45.0f), (float) width / (float) height, 0.1f,
        100f);

    boxShader.bind();
    boxShader.setUniform("projection", projectionMatrix);

    parallelLightShader.bind();
    parallelLightShader.setUniform("projection", projectionMatrix);

    pointLightShader.bind();
    pointLightShader.setUniform("projection", projectionMatrix);
  }

  protected void preFrame() {
    glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    float currentFrameTime = (float) glfwGetTime();
    float deltaTime = currentFrameTime - lastFrameTime;
    lastFrameTime = currentFrameTime;
    camera.setSpeed(2.5f * deltaTime);

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
