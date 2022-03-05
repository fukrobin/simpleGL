#version 330 core
out vec4 FragColor;

struct Light {
    vec3 position;
    vec3 direction;
    float cutOff;
    float outerCutOff;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

uniform Light parallelLight;
uniform Light pointLight;
uniform Light spotLight;

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

void computeParallelLight(out vec3 color) {
    vec3 materialSample = texture(material.diffuseMap, TextureCoordinates).rgb;
    // 环境光照
    vec3 ambient = parallelLight.ambient * materialSample;

    // 漫反射光照: 被光源直接照射（角度小于 90）的片段更亮
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(-parallelLight.direction);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = parallelLight.diffuse * diff * materialSample;

    // 镜面光照: 高光
    vec3 specularSample = texture(material.specularMap, TextureCoordinates).rgb;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess * 128);
    vec3 specular =  parallelLight.specular * specularSample * spec;

    color = ambient + diffuse + specular;
}

void computePointLight(out vec3 color) {
    float distance    = length(pointLight.position - FragPos);
    float attenuation = 1.0 / (pointLight.constant + pointLight.linear * distance +
    pointLight.quadratic * (distance * distance));

    vec3 materialSample = texture(material.diffuseMap, TextureCoordinates).rgb;
    // 环境光照
    vec3 ambient = pointLight.ambient * materialSample;

    // 漫反射光照: 被光源直接照射（角度小于 90）的片段更亮
    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(pointLight.position - FragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = pointLight.diffuse * diff * materialSample;

    // 镜面光照: 高光
    vec3 specularSample = texture(material.specularMap, TextureCoordinates).rgb;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess * 128);
    vec3 specular =  pointLight.specular * specularSample * spec;

    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    color = ambient + diffuse + specular;
}

// 聚光灯
void computeSpotlight(out vec3 color) {
    vec3 materialSample = texture(material.diffuseMap, TextureCoordinates).rgb;

    vec3 lightDir = normalize(spotLight.position - FragPos);
    float theta = dot(lightDir, normalize(-spotLight.direction));
    float epsilon   = spotLight.cutOff - spotLight.outerCutOff;
    float intensity = clamp((theta - spotLight.outerCutOff) / epsilon, 0.0, 1.0);

    // 环境光照
    vec3 ambient = spotLight.ambient * materialSample;

    float distance    = length(spotLight.position - FragPos);
    float attenuation = 1.0 / (spotLight.constant + spotLight.linear * distance +
    spotLight.quadratic * (distance * distance));

    // 漫反射光照: 被光源直接照射（角度小于 90）的片段更亮
    vec3 norm = normalize(Normal);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = spotLight.diffuse * diff * materialSample;

    // 镜面光照: 高光
    vec3 specularSample = texture(material.specularMap, TextureCoordinates).rgb;
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess * 128);
    vec3 specular =  spotLight.specular * specularSample * spec;

    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    color = ambient + diffuse * intensity + specular * intensity;
}

void main() {
    vec3 color1, color2, color3;
    computeParallelLight(color1);
    computePointLight(color2);
    computeSpotlight(color3);

        vec3 result = color1 + color2 + color3;
//    vec3 result = color3;
    FragColor = vec4(result, 1.0);
}