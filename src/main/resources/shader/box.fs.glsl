#version 330 core
out vec4 FragColor;

struct Material {
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

struct ParallelLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct PointLight {
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct SpotLight {
    vec3 position;
    vec3 direction;
    float cutOff;
    float outerCutOff;

    vec3 ambient;
    vec3 diffuse;
    sampler2D diffuseMap;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

uniform vec3 cameraRight;

//#define NR_POINT_LIGHTS 4
uniform ParallelLight parallelLight;
//uniform PointLight pointLights[NR_POINT_LIGHTS];
uniform PointLight pointLight;
uniform SpotLight spotLight;

vec3 calcParallelLight(ParallelLight light, vec3 normal, vec3 viewDir);
vec3 calcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);
vec3 calcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir);

vec3 calcParallelLight(ParallelLight light, vec3 normal, vec3 viewDir) {
    vec3 lightDir = normalize(-light.direction);
    // 漫反射着色
    float diff = max(dot(normal, lightDir), 0.0);
    // 镜面光着色
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // 合并结果
    vec3 diffuseSample = texture(material.diffuseMap, TextureCoordinates).rgb;
    vec3 ambient  = light.ambient  * diffuseSample;
    vec3 diffuse  = light.diffuse  * diff * diffuseSample;
    vec3 specular = light.specular * spec * vec3(texture(material.specularMap, TextureCoordinates));
    return (ambient + diffuse + specular);
}
vec3 calcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(light.position - fragPos);
    // 漫反射着色
    float diff = max(dot(normal, lightDir), 0.0);
    // 镜面光着色
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // 衰减
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance +
    light.quadratic * (distance * distance));
    // 合并结果
    vec3 diffuseSample = texture(material.diffuseMap, TextureCoordinates).rgb;
    vec3 ambient  = light.ambient  * diffuseSample;
    vec3 diffuse  = light.diffuse  * diff * diffuseSample;
    vec3 specular = light.specular * spec * vec3(texture(material.specularMap, TextureCoordinates));
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}

vec3 projectVector(vec3 a, vec3 b) {
    return (dot(a, b) / pow(length(b), 2)) * b;
}

float projectVectorScala(vec3 a, vec3 b) {
    return dot(a, b) / length(b);
}

vec3 calcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 toLightDir = normalize(light.position - fragPos);
    // 漫反射光照: 被光源直接照射（角度小于 90）的片段更亮
    float diff = max(dot(normal, toLightDir), 0.0);

    // 镜面光照: 高光
    vec3 reflectDir = reflect(-toLightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess * 128);

    vec3 cameraToFrag = fragPos - light.position;
    vec3 cameraToCenter = projectVector(cameraToFrag, light.direction);
    float radius = tan(acos(light.outerCutOff)) * length(cameraToCenter);
    vec3 centerToFrag = cameraToFrag - cameraToCenter;
    vec3 centerToR = projectVector(centerToFrag, cameraRight);
    float tx = length(centerToR) / radius;
    float ty = length(centerToFrag - centerToR) / radius;
    if (centerToFrag.x < 0) tx = -tx;
    if (centerToFrag.y < 0) ty = -ty;
    tx = (tx + 1) / 2;
    ty = (ty + 1) / 2;

    vec3 diffuseSample = texture(light.diffuseMap, vec2(tx, ty)).rgb;
    vec3 specularSample = texture(material.specularMap, TextureCoordinates).rgb;

    vec3 ambient = light.ambient * diffuseSample;
    vec3 diffuse = light.diffuse * diff * diffuseSample;
    vec3 specular =  light.specular * specularSample * spec;

    // 衰减
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance +
    light.quadratic * (distance * distance));

    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;

    // 柔化边缘
    float theta = dot(toLightDir, normalize(-light.direction));
    float epsilon   = light.cutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0, 1.0);

    return ambient + diffuse * intensity + specular * intensity;
}

void main() {
    // 属性
    vec3 norm = normalize(Normal);
    vec3 viewDir = normalize(viewPos - FragPos);

    // 第一阶段：定向光照
    vec3 result = calcParallelLight(parallelLight, norm, viewDir);
    // 第二阶段：点光源
//    for(int i = 0; i < NR_POINT_LIGHTS; i++) {
//        result += calcPointLight(pointLights[i], norm, FragPos, viewDir);
//    }
    result += calcPointLight(pointLight, norm, FragPos, viewDir);
    // 第三阶段：聚光
    result += calcSpotLight(spotLight, norm, FragPos, viewDir);

    FragColor = vec4(result, 1.0);
}