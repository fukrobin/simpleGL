plugins {
    java
}

repositories {
    mavenCentral()
}

val LWJGL_VERSION = "3.3.1"
val JOML_VERSION = "1.10.4"
val LWJGL_NATIVES = "natives-windows"
val IMGUI_VERSION = "1.86.2"
val COMMONS_CSV_VERSION = "1.9.0"
val LOMBOK_VERSION = "1.18.22"
val lwjglVersion = "3.3.1"
val jomlVersion = "1.10.4"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$LWJGL_VERSION"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)

    implementation("io.github.spair:imgui-java-binding:$IMGUI_VERSION")
    implementation("io.github.spair:imgui-java-lwjgl3:$IMGUI_VERSION")
    runtimeOnly("io.github.spair:imgui-java-natives-windows:$IMGUI_VERSION")

    implementation("org.apache.commons:commons-csv:$COMMONS_CSV_VERSION")

    // lombok
    compileOnly("org.projectlombok:lombok:$LOMBOK_VERSION")
    annotationProcessor("org.projectlombok:lombok:$LOMBOK_VERSION")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}