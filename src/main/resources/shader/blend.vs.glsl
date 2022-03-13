#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTextureCoordinates;

out vec2 TextureCoordinates;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    TextureCoordinates = aTextureCoordinates;
    gl_Position = projection * view * model * vec4(aPos, 1);
}