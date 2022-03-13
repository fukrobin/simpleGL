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
import static org.lwjgl.glfw.GLFW.glfwSetMonitorCallback;
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
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddressSafe;
import static org.robin.gl.utils.Util.stringVectors;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.robin.gl.scene.Camera;
import org.robin.gl.scene.Light;
import org.robin.gl.scene.ParallelLight;
import org.robin.gl.scene.PointLight;
import org.robin.gl.scene.SpotLight;
import org.robin.gl.utils.ImGuiImplGlfw;
import org.robin.gl.utils.MemoryManager;
import org.robin.gl.utils.Util;

/**
 * Simple OpEnGL application.
 *
 * @author fkrobin.
 */
public class Application {

  private static final Vector3f DEST_VECTOR3 = new Vector3f();
  private static final Matrix4f DEST_MATRIX  = new Matrix4f();

  private final Camera camera;

  private long window;
  private int  width;
  private int  height;

  private final Vector3f clearColor = new Vector3f(1);

  //////////////////////////////////////////////////
  // Cursor
  //////////////////////////////////////////////////

  private final AtomicBoolean firstCursor  = new AtomicBoolean(true);
  private final AtomicBoolean cursorHidden = new AtomicBoolean(true);
  private       double        lastCursorPosX;
  private       double        lastCursorPosY;
  private       float         lastFrameTime;

  //////////////////////////////////////////////////
  // Shader
  //////////////////////////////////////////////////

  private int boxVao;
  private int lightVao;
  private int vbo;

  private ShaderProgram parallelLightShader;
  private ShaderProgram pointLightShader;

  //////////////////////////////////////////////////
  // light
  //////////////////////////////////////////////////

  private final Light      parallelLight;
  private final PointLight pointLight;
  private final SpotLight  spotLight;

  //////////////////////////////////////////////////
  // Material
  //////////////////////////////////////////////////

  private final float[] materialShininess = new float[]{0.25f};

  //////////////////////////////////////////////////
  // ImGui
  //////////////////////////////////////////////////

  private final ImGuiImplGl3  imGuiImplGl3  = new ImGuiImplGl3();
  private final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();

  /**
   * 设置 Window 的一些初始值.
   */
  public Application() {
    width  = 1000;
    height = 600;
    camera = new Camera(new Vector3f(0, 0, 3),
                        new Vector3f(0, 1, 0),
                        new Vector3f(0, -90, 0));

    parallelLight = new ParallelLight(new Vector3f(1), new Vector3f(-0.2f, -1.0f, -0.3f));
    parallelLight.setAmbientPercent(0.05f);
    parallelLight.setDiffusePercent(0.4f);
    parallelLight.updateColor();

    pointLight = new PointLight(new Vector3f(1), new Vector3f(0.7f, 0.2f, 2.0f));
    pointLight.setAmbientPercent(0.05f);
    pointLight.setDiffusePercent(0.8f);
    pointLight.updateColor();

    spotLight = new SpotLight(new Vector3f(1), camera.getTarget(), camera.getPosition());
    spotLight.setAmbientPercent(0);
    spotLight.setDiffusePercent(1);
    spotLight.updateColor();
    spotLight.setCutOff(8);
    spotLight.setOuterCutOff(12);
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
    } catch (Exception e) {
      e.printStackTrace();
    } finally {

      MemoryManager.cleanup();

      glDeleteBuffers(boxVao);
      glDeleteBuffers(vbo);

      imGuiImplGl3.dispose();
      imGuiImplGlfw.dispose();
      ImGui.destroyContext();

      Callback.free(memAddressSafe(glfwSetMonitorCallback(null)));
      glfwFreeCallbacks(window);
      glfwDestroyWindow(window);

      glfwTerminate();
      Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
  }

  private void init() {
    initGLFW();
    initOpenGL();
    initImGui();
    initBlend();

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
      IntBuffer widthBuffer  = stack.mallocInt(1);
      IntBuffer heightBuffer = stack.mallocInt(1);
      glfwGetWindowSize(window, widthBuffer, heightBuffer);
      width  = widthBuffer.get(0);
      height = heightBuffer.get(0);
      camera.setViewWidth(width);
      camera.setViewHeight(height);
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
  private void initOpenGL() {
    GL.createCapabilities();
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LEQUAL);

    glEnable(GL_STENCIL_TEST);

    setupShader();
    setupVao();
  }

  private GLFWWindowSizeCallbackI windowSizeCallback() {
    return (window1, width1, height1) -> {
      this.width  = width1;
      this.height = height1;
      camera.setViewWidth(width1);
      camera.setViewHeight(height1);
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

  private void setupShader() {
    parallelLightShader = MemoryManager.createShader("shader/parallelLight.vs.glsl",
                                                     "shader/parallelLight.fs.glsl");
    pointLightShader    = MemoryManager.createShader("shader/pointLight.vs.glsl",
                                                     "shader/pointLight.fs.glsl");
  }

  private ShaderProgram shader;

  private void initShader() {
    shader = MemoryManager.createShader("shader/blend.vs.glsl",
                                        "shader/blend.fs.glsl");
    shader.bind();
    shader.setUniform("texture1", 0);
  }

  //////////////////////////////////////////////////
  // Blend test
  //////////////////////////////////////////////////

  private void initBlend() {
    glEnable(GL_BLEND);
    glEnable(GL_DEPTH_TEST);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    initFloorPlane();
    initGlassWindow();
  }

  //////////////////////////////////////////////////
  // Glass window
  //////////////////////////////////////////////////

  private final float[] windowVertices = new float[]{
      0.0f, 0.5f, 0.0f, 0.0f, 0.0f,
      0.0f, -0.5f, 0.0f, 0.0f, 1.0f,
      1.0f, -0.5f, 0.0f, 1.0f, 1.0f,

      0.0f, 0.5f, 0.0f, 0.0f, 0.0f,
      1.0f, -0.5f, 0.0f, 1.0f, 1.0f,
      1.0f, 0.5f, 0.0f, 1.0f, 0.0f
  };

  private final Vector3f[] windowPositions = new Vector3f[]{
      new Vector3f(-1.5f, 0.0f, -0.48f),
      new Vector3f(1.5f, 0.0f, 0.51f),
      new Vector3f(0.0f, 0.0f, 0.7f),
      new Vector3f(-0.3f, 0.0f, -2.3f),
      new Vector3f(0.5f, 0.0f, -0.6f)
  };

  private int transparentVao;
  private int transparentTexture;

  private void initGlassWindow() {
    transparentVao = glGenVertexArrays();
    int transparentVbo = glGenBuffers();

    glBindVertexArray(transparentVao);
    glBindBuffer(GL_ARRAY_BUFFER, transparentVbo);
    glBufferData(GL_ARRAY_BUFFER, windowVertices, GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glBindVertexArray(0);

    transparentTexture = Util.texture2D("asserts/texture/window.png");
  }

  private void renderGlassWindow() {
    glBindVertexArray(transparentVao);
    glBindTexture(GL_TEXTURE_2D, transparentTexture);

    for (Vector3f position : windowPositions) {
      shader.setUniform("model", DEST_MATRIX.identity().translate(position));
      glDrawArrays(GL_TRIANGLES, 0, 6);
    }
  }

  //////////////////////////////////////////////////
  // Floor plane
  //////////////////////////////////////////////////

  private final float[] planeVertices = new float[]{
      5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
      -5.0f, -0.5f, 5.0f, 0.0f, 0.0f,
      -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,

      5.0f, -0.5f, 5.0f, 2.0f, 0.0f,
      -5.0f, -0.5f, -5.0f, 0.0f, 2.0f,
      5.0f, -0.5f, -5.0f, 2.0f, 2.0f
  };

  private int floorPlaneVao;
  private int floorTexture;

  private void initFloorPlane() {
    floorPlaneVao = glGenVertexArrays();
    int planeVbo = glGenBuffers();

    glBindVertexArray(floorPlaneVao);
    glBindBuffer(GL_ARRAY_BUFFER, planeVbo);
    glBufferData(GL_ARRAY_BUFFER, planeVertices, GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glBindVertexArray(0);

    floorTexture = Util.texture2D("asserts/texture/metal.png");
  }

  private void renderFloor() {
    shader.setUniform("model", DEST_MATRIX.identity());

    glBindVertexArray(floorPlaneVao);
    glBindTexture(GL_TEXTURE_2D, floorTexture);
    glDrawArrays(GL_TRIANGLES, 0, 6);
  }

  @SuppressWarnings("checkstyle:EmptyCatchBlock")
  private void loop() {
    initShader();

    while (!glfwWindowShouldClose(window)) {
      preFrame();

      render();
      renderImGui();

      postFrame();
      glfwSwapBuffers(window);
      glfwPollEvents();
    }
  }

  //////////////////////////////////////////////////
  // Render
  //////////////////////////////////////////////////

  private void render() {
    renderBlend();
    renderLight();

    glBindVertexArray(0);
  }

  private void renderBlend() {
    shader.bind();
    shader.setUniform("view", camera.getViewMatrix());
    shader.setUniform("projection", camera.getProjectionMatrix());

    renderFloor();
    renderGlassWindow();
  }

  private void setLightUniforms(ShaderProgram shader, Light light, String name) {
    shader.setUniform(name + ".ambient", light.getAmbient());
    shader.setUniform(name + ".diffuse", light.getDiffuse());
    shader.setUniform(name + ".specular", light.getSpecular());
    if (light instanceof ParallelLight) {
      shader.setUniform(name + ".direction", light.getDirection());
    } else {
      if (light instanceof PointLight) {
        shader.setUniform(name + ".constant", 1.0f);
        shader.setUniform(name + ".linear", ((PointLight) light).getLinear());
        shader.setUniform(name + ".quadratic", ((PointLight) light).getQuadratic());
        shader.setUniform(name + ".position", light.getPosition());
      }
      if (light instanceof SpotLight) {
        shader.setUniform(name + ".cutOff", ((SpotLight) light).getCutOff());
        shader.setUniform(name + ".outerCutOff", ((SpotLight) light).getOuterCutOff());
        shader.setUniform(name + ".direction", light.getDirection());
      }
    }
  }

  /**
   * 渲染所有的光源.
   */
  private void renderLight() {
    glBindVertexArray(lightVao);

    parallelLightShader.bind();
    parallelLightShader.setUniform("view", camera.getViewMatrix());
    parallelLightShader.setUniform("projection", camera.getProjectionMatrix());
    glDrawArrays(GL_TRIANGLES, 0, 36);

    pointLightShader.bind();
    DEST_MATRIX.identity().translate(pointLight.getPosition()).scale(0.2f);
    pointLightShader.setUniform("model", DEST_MATRIX);
    pointLightShader.setUniform("view", camera.getViewMatrix());
    pointLightShader.setUniform("projection", camera.getProjectionMatrix());
    pointLightShader.setUniform("lightColor", pointLight.getColor());
    glDrawArrays(GL_TRIANGLES, 0, 36);
  }

  //////////////////////////////////////////////////
  // ImGui
  //////////////////////////////////////////////////

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
    ImGui.setNextWindowPos(10, 10, ImGuiCond.Once);
    ImGui.begin("Attribute Editor", ImGuiWindowFlags.AlwaysAutoResize);

    imGui_cameraInfo();
    imGui_Material();
    imGui_lightSettings();
    imGui_Model();

    ImGui.end();
  }

  private void imGui_Material() {
    ImGui.text("Materials");
    ImGui.sliderFloat("shininess", materialShininess, 0, 1);
  }

  private void imGui_cameraInfo() {
    if (ImGui.collapsingHeader("Camera Info", ImGuiTreeNodeFlags.DefaultOpen)) {
      ImGui.labelText("Camera position", stringVectors(camera.getPosition()));
      ImGui.labelText("Camera Target", stringVectors(camera.getTarget()));
      ImGui.labelText("Camera rotation", stringVectors(camera.getRotation()));
    }
  }

  private void imGui_lightSettings() {
    ImGui.separator();
    if (ImGui.collapsingHeader("Light Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
      if (ImGui.treeNodeEx("Parallel Light", ImGuiTreeNodeFlags.DefaultOpen)) {
        if (ImGui.colorEdit3("Parallel-Color", parallelLight.getColorFloats())) {
          parallelLight.updateColor();
        }
        if (ImGui.sliderFloat3("Parallel-Direction", parallelLight.getDirectionFloats(), -1, 1)) {
          parallelLight.getDirection().set(parallelLight.getDirectionFloats());
        }
        ImGui.treePop();
        ImGui.separator();
      }
      if (ImGui.treeNodeEx("Point Light", ImGuiTreeNodeFlags.DefaultOpen)) {
        if (ImGui.colorEdit3("Point-Color", pointLight.getColorFloats())) {
          pointLight.updateColor();
        }
        if (ImGui.sliderFloat3("Point-position", pointLight.getPositionFloats(), -10, 10)) {
          pointLight.getPosition().set(pointLight.getPositionFloats());
        }
        ImGui.treePop();
        ImGui.separator();
      }
      if (ImGui.treeNodeEx("Spot Light", ImGuiTreeNodeFlags.DefaultOpen)) {
        if (ImGui.colorEdit3("Spot-Color", spotLight.getColorFloats())) {
          spotLight.updateColor();
        }
        ImGui.treePop();
        ImGui.separator();
      }
    }
  }

  private final float[] modelScale        = new float[]{0.01f};
  private final float[] modelStencilColor = new float[]{1, 0, 0};

  private void imGui_Model() {
    ImGui.separator();
    if (ImGui.collapsingHeader("Model Settings", ImGuiTreeNodeFlags.DefaultOpen)) {
      ImGui.sliderFloat("Scale", modelScale, 0, 0.1f);
      ImGui.colorEdit3("Stencil Color", modelStencilColor);
    }
  }

  protected void preFrame() {
    glClearColor(clearColor.x, clearColor.y, clearColor.z, 1);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

    glStencilMask(0x00);

    float currentFrameTime = (float) glfwGetTime();
    float deltaTime        = currentFrameTime - lastFrameTime;
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
