#version 330 core
out vec4 FragColor;

// 法线向量
in vec3 Normal;
// 世界空间中的片段位置
in vec3 FragPos;

uniform vec3 objectColor;
uniform vec3 lightColor;
// 用于计算漫反射和镜面光照
uniform vec3 lightPos;
// 观察者的位置，即摄像机的位置
uniform vec3 viewPos;
// 反光度
uniform int shininess;
// 环境光强度
uniform float ambientStrength;
// 反光强度
uniform float specularStrength;

void main() {
    // 环境光照
    vec3 ambient = ambientStrength * lightColor;

    // 漫反射光照
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(lightPos - FragPos);
    vec3 diffuse = max(dot(norm, lightDir), 0.0) * lightColor;

    // 镜面光照
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
//    float spec = max(dot(viewDir, reflectDir), 0.0);
    vec3 specular = specularStrength * spec * lightColor;

    vec3 result = (ambient + diffuse + specular) * objectColor;
    FragColor = vec4(result, 1.0);
}