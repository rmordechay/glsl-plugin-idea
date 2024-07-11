const shaderCode = `glsl:
    #version 330 core
    #define PI 3.14
    #define f(a, b) if (a > b) { int num = 2; }
    #include "different/file.glsl"
    
    precision mediump float;
    layout (location = 0) in vec3 aPos;
    layout (location = 1) in vec2 aTexCoord;
    
    out vec2 TexCoord;
    
    uniform mat4 model;
    uniform mat4 view;
    uniform mat4 projection;
    
    /**
    *
    */
    int add(int a, int b) {
        f(1, 2)
        float v = normalize(a * PI);
        return a + b;
    }
    
    void main() {
        gl_Position = projection * view * model * vec4(abs(aPos), 1.0f);
        TexCoord = vec2(aTexCoord.x, aTexCoord.y);
    }
`