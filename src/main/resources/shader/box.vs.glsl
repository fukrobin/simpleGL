#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTextureCoordinates;

// 法线向量
out vec3 Normal;
// 世界空间中的片段位置
out vec3 FragPos;
out vec2 TextureCoordinates;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    FragPos = vec3(model * vec4(aPos, 1));
    Normal = aNormal;
    TextureCoordinates = aTextureCoordinates;
    gl_Position = projection * view * vec4(FragPos, 1);
}