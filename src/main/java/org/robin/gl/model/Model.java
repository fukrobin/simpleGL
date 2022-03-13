package org.robin.gl.model;

import static org.lwjgl.assimp.Assimp.AI_SCENE_FLAGS_INCOMPLETE;
import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_FindInvalidData;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_SPECULAR;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryStack;
import org.robin.gl.ShaderProgram;
import org.robin.gl.model.Texture.TextureType;
import org.robin.gl.utils.Util;

/**
 * 加载并转换模型数据，使用 Assimp.
 *
 * @author fkrobin
 * @date 2022/3/7
 */
@Getter
public class Model {

  private static final Vector4f DEST_VECTOR = new Vector4f();

  private final List<Mesh> meshes      = new ArrayList<>();
  private       String     directory;
  private final Matrix4f   modelMatrix = new Matrix4f();

  private float    scale       = 1.0f;
  private Vector3f translation = new Vector3f();

  public Model(String path) {
    loadModel(path);
  }

  public void scale(float scale) {
    this.scale = scale;
  }

  public void translation(Vector3f translation) {
    this.translation = translation;
  }

  public void translation(float x, float y, float z) {
    this.translation.set(x, y, z);
  }

  public Matrix4f getModelMatrix() {
    modelMatrix.identity().translate(translation).scale(this.scale);
    return modelMatrix;
  }

  public void draw(ShaderProgram shader) {
    meshes.forEach(m -> m.draw(shader));
  }

  public void cleanup() {
    meshes.forEach(Mesh::cleanup);
  }

  /**
   * flags:
   * <ul>
   *   <li>aiProcess_JoinIdenticalVertices: 识别并连接相同的顶点，适用于使用索引顶点绘图</li>
   *   <li>aiProcess_FixInfacingNormals: 修复朝内的法向量，简单有效</li>
   *   <li>aiProcess_GenNormals： 不存在法向量时，生成法向量</li>
   * </ul>.
   *
   * @param path 模型的路径，继续项目路径
   */
  private void loadModel(String path) {
    AIScene aiScene = aiImportFile(path,
                                   aiProcess_Triangulate
                                       | aiProcess_FindInvalidData
                                       | aiProcess_JoinIdenticalVertices
                                       | aiProcess_FixInfacingNormals
                                       | aiProcess_GenNormals);
    if (aiScene == null
        || aiScene.mFlags() == AI_SCENE_FLAGS_INCOMPLETE
        || aiScene.mRootNode() == null) {
      System.err.println("Model.loadScene()" + aiGetErrorString());
      return;
    }
    directory = path.substring(0, path.lastIndexOf('/') + 1);
    processNode(aiScene.mRootNode(), aiScene);
  }

  private void processNode(final AINode node, final AIScene aiScene) {
    if (aiScene != null) {
      PointerBuffer aiMeshes = aiScene.mMeshes();
      if (aiMeshes == null) {
        return;
      }

      for (int i = 0; i < node.mNumMeshes(); i++) {
        IntBuffer buffer = node.mMeshes();
        Objects.requireNonNull(buffer,
                               "Node mesh count > 0 but unable to get Mesh when index = "
                                   + i);
        AIMesh aiMesh = AIMesh.create(aiMeshes.get(buffer.get(i)));
        Mesh   mesh   = processMesh(aiMesh, aiScene);
        meshes.add(mesh);
      }
    }
    PointerBuffer children = node.mChildren();
    if (children != null) {
      for (int i = 0; i < node.mNumChildren(); i++) {
        processNode(AINode.create(children.get(i)), aiScene);
      }
    }
  }

  private Mesh processMesh(final AIMesh aiMesh, final AIScene aiScene) {

    List<Float>   vertices        = processVertices(aiMesh);
    List<Float>   normals         = processNormals(aiMesh);
    List<Float>   textCoordinates = processTextCoordinates(aiMesh);
    List<Integer> indices         = processIndices(aiMesh);
    List<Texture> textures        = processTextures(aiMesh, aiScene);
    return new Mesh(vertices, normals, textCoordinates, indices, textures);
  }

  private List<Float> processVertices(AIMesh aiMesh) {
    List<Float>       vertices   = new ArrayList<>(aiMesh.mNumVertices() * 3);
    AIVector3D.Buffer aiVertices = aiMesh.mVertices();
    for (int i = 0; i < aiMesh.mNumVertices(); i++) {
      AIVector3D vector3D = aiVertices.get(i);
      vertices.add(vector3D.x());
      vertices.add(vector3D.y());
      vertices.add(vector3D.z());
    }
    return vertices;
  }

  private List<Float> processNormals(AIMesh aiMesh) {
    List<Float> normals = new ArrayList<>(aiMesh.mNumVertices() * 3);
    AIVector3D.Buffer aiNormals =
        Objects.requireNonNull(aiMesh.mNormals(),
                               "Assimp should generate normals with aiProcess_GenNormals flag"
                                   + " when mesh doesn't has normals");
    for (int i = 0; i < aiMesh.mNumVertices(); i++) {
      AIVector3D vector3D = aiNormals.get(i);
      normals.add(vector3D.x());
      normals.add(vector3D.y());
      normals.add(vector3D.z());
    }
    return normals;
  }

  private List<Float> processTextCoordinates(AIMesh aiMesh) {
    AIVector3D.Buffer textCoordinates = aiMesh.mTextureCoords(0);
    List<Float>       coordinates     = new ArrayList<>(aiMesh.mNumVertices() * 2);
    if (textCoordinates != null) {
      for (int i = 0; i < aiMesh.mNumVertices(); i++) {
        AIVector3D vector3D = textCoordinates.get(i);
        coordinates.add(vector3D.x());
        coordinates.add(vector3D.y());
      }
      return coordinates;
    }

    for (int i = 0; i < aiMesh.mNumVertices(); i++) {
      coordinates.add(0f);
      coordinates.add(0f);
    }
    return coordinates;
  }

  private List<Integer> processIndices(AIMesh aiMesh) {
    int           numFaces = aiMesh.mNumFaces();
    AIFace.Buffer aiFaces  = aiMesh.mFaces();
    List<Integer> indices  = new ArrayList<>();
    for (int i = 0; i < numFaces; i++) {
      AIFace    aiFace = aiFaces.get(i);
      IntBuffer buffer = aiFace.mIndices();
      while (buffer.remaining() > 0) {
        indices.add(buffer.get());
      }
    }
    return indices;
  }

  private List<Texture> processTextures(AIMesh aiMesh, AIScene aiScene) {
    List<Texture> textures = new ArrayList<>();
    if (aiMesh.mMaterialIndex() >= 0) {
      PointerBuffer materials = Objects.requireNonNull(aiScene.mMaterials());
      AIMaterial    material  = AIMaterial.create(materials.get(aiMesh.mMaterialIndex()));
      List<Texture> diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE,
                                                       TextureType.DIFFUSE_MAP);
      List<Texture> specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR,
                                                        TextureType.SPECULAR_MAP);
      textures.addAll(diffuseMaps);
      textures.addAll(specularMaps);
    }
    return textures;
  }

  private final Map<String, Integer> cacheTextures = new HashMap<>();

  private List<Texture> loadMaterialTextures(final AIMaterial material, int textureType,
                                             TextureType type) {
    List<Texture> textures = new ArrayList<>();
    try (MemoryStack stack = MemoryStack.stackPush()) {
      for (int i = 0; i < Assimp.aiGetMaterialTextureCount(material, textureType); i++) {
        AIString  path   = AIString.malloc(stack);
        IntBuffer buffer = stack.mallocInt(1);
        Assimp.aiGetMaterialTexture(material, textureType, i, path, null,
                                    buffer, null, null, null, null);
        String textPath = path.dataString();
        if (textPath.length() > 0) {
          textPath = parseTexturePath(textPath);
          int textureId = cacheTextures.computeIfAbsent(textPath,
                                                        s -> Util.texture2D(directory + s));
          Texture texture = new Texture();
          texture.setId(textureId);
          texture.setType(type);
          texture.setPath(textPath);
          textures.add(texture);
        }
      }
    }
    return textures;
  }

  /**
   * 解析纹理文件路径，将其转化为本引擎可用的位置.
   *
   * @param texturePath 纹理路径，也许是相对路径，也许是绝对路径，但本方法都只会截取文件名字，路径位置使用实例化时传入的参数
   * @return 解析后可用的纹理路径位置
   */
  private String parseTexturePath(String texturePath) {
    texturePath = texturePath.replaceAll("\\\\", "/");
    texturePath = texturePath.replaceAll("//", "/");
    int lastIdx = texturePath.lastIndexOf("/");
    if (lastIdx > 0) {
      texturePath = texturePath.substring(lastIdx);
    }

    return texturePath;
  }
}
