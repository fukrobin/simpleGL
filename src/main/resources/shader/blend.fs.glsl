#version 330 core

uniform sampler2D texture1;

in vec2 TextureCoordinates;

out vec4 FragColor;

void main() {
    vec4 color= texture(texture1, TextureCoordinates);
    FragColor = color;
    if (color.w < 0.1f) {
        gl_FragDepth = 0;
    }
}