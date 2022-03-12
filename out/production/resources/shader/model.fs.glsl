#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D diffuseMap0;
uniform sampler2D specularMap0;

void main()
{
    vec3 diffuse = texture(diffuseMap0, TexCoords).rgb;
    vec3 specular = texture(specularMap0, TexCoords).rgb;
    FragColor = vec4(diffuse, 1);
}