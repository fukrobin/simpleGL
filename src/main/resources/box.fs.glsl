#version 330 core
out vec4 FragColor;

struct Light {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

uniform Light light;

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

uniform Material material;

// 法线向量
in vec3 Normal;
// 世界空间中的片段位置
in vec3 FragPos;

// 观察者的位置，即摄像机的位置
uniform vec3 viewPos;

void main() {
    // 环境光照
    vec3 ambient = light.ambient * material.ambient;

    // 漫反射光照: 被光源直接照射（角度小于 90）的片段更亮
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(light.position - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * (diff * material.diffuse);

    // 镜面光照: 高光
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess * 128);
    vec3 specular =  light.specular * (material.specular * spec);

    vec3 result = (ambient + diffuse + specular);
    FragColor = vec4(result, 1.0);
}