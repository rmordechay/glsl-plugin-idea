#version 330 core
#define PI 3.14
#define func_updated(a, b) if (a > b) { int num = 2; }
#include "different/file.glsl"

precision mediump float;
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec2 TexCoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection2;

/**
*
*/
int addd2(int a, int b) {
    func_updated(1, 2)
    float v = normalize(a * PI);
    return a + b;
}

void main() {
    gl_Position = projection * view * model * vec4(abs(aPos), 1.0f);
    TexCoord = vec2(aTexCoord.x, aTexCoord.y);
}