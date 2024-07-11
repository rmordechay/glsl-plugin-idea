#version 330 core
#define PI 3.14
#define func(a, b) if (a > b) { int num = 2; }
#include "different/file.glsl"

precision mediump float;
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec2 TexCoordUpdated;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection2;

void main() {
    TexCoordUpdated = vec2(1, 1);
}