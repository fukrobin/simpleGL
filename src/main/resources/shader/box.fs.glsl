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
// 环境光几乎在所有时间都和漫反射相同，忽略此分量
// vec3 ambient;
// sampler2D 时不透明变量，具有此类型变量的结构体
// 不可通过除 uniform 方式以外的途径实例化
    sampler2D diffuseMap;
    sampler2D specularMap;
    float shininess;
};

uniform Material material;

// 法线向量
in vec3 Normal;
// 世界空间中的片段位置
in vec3 FragPos;
// 对漫反射贴图取样
in vec2 TextureCoordinates;

// 观察者的位置，即摄像机的位置
uniform vec3 viewPos;

void main() {
    vec3 materialSample = texture(material.diffuseMap, TextureCoordinates).rgb;

    // 环境光照
    vec3 ambient = light.ambient * materialSample;

    // 漫反射光照: 被光源直接照射（角度小于 90）的片段更亮
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(light.position - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = light.diffuse * diff * materialSample;

    // 镜面光照: 高光
    vec3 specularSample = texture(material.specularMap, TextureCoordinates).rgb;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess * 128);
    vec3 specular =  light.specular * specularSample * spec;

    vec3 result = (ambient + diffuse + specular);
    FragColor = vec4(result, 1.0);
}