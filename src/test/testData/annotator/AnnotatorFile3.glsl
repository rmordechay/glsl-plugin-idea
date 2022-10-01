struct MyType {
    int a;
} myType;

int funcInt(int x);
void funcFloat(float x);
void funcIntFloat(int x, float y);
void funcVec4(vec4 vec);
void funcIvec4(ivec4 vec);
void funcMat(ivec4 vec);
void funcMat(mat3 mat);
void funcMat3x2(mat3x2 mat);
void funcMyType(MyType myType);

struct light {
    float intensity;
    vec3 position;
};

light lightVar = light(3.0, vec3(1.0, 2.0, 3.0));

void main() {
    funcFloat(funcInt(1));
    cross(vec3(1), vec3(1));
    funcInt(1);
    funcMyType(myType);
    funcFloat(1.0);
    funcFloat(1.0f);
    funcIntFloat(1, 1.0);
    funcIntFloat(1, 1.0f);
    funcIntFloat(1, 1);
    funcVec4(vec4(1.0));
    funcVec4(ivec4(1));
    funcVec4(uvec4(1u));
    funcMat(mat3());
    funcMat3x2(mat3x2());
    abs(gl_FragCoord.x);
    float arr[24];
    abs(arr[4]);
    int c = 1;
    abs(c++);

    vec3(1.0);
    bvec2(true);
    bvec2(true, false);
    vec4(1.0, 0.0, 0.0, 1.0);
    vec4(1.0, vec3(1.0));
    vec4(1.0, 0.0, vec2(1.0));
    vec4(1.0, 0.0, vec2(1.0, 1.0));
    vec4((1.0 + 1.0), 1.0, 1.0, 1.0);

    vec2 vec = vec2(1.0);
    vec3 vec = vec3(1.0);
    vec4 vec = vec4(1.0);
    bool a = true;
    int b = 1;
    uint c = 1u;
    float d = 1.0;

    vec2 vec = ivec2(1);
    int a = 1;
    int b = 2;
    vec4 vec = vec4(vec2(1.0), 1.0, 1.0);
    vec2 rand_seed = vec2(0.0f, 0.0f);

    float x = fract((cos(dot(rand_seed, vec2(1., 1.))) * 1.));
    double x = fract((cos(dot(rand_seed, vec2(1., 1.))) * 1.));
    vec4 acceleration = vec4(0.0);
    acceleration.xyz += vec3(1.0);

    #define VAR 10
    int a = VAR + 12;

    mat4 a = mat4(1.0);
    vec4 b = a[0];

    int or = 2;
    or |= 2;
    float or2 = 2;
    vec3 vec = vec3(1.0);
    vec3 vec = vec3(1.0, 1.0, 1.0);
    vec3 vec = <error descr="Too few arguments to constructor of 'vec3'.">vec3</error>(1.0, 1.0);
    vec3 vec = <error descr="Too many arguments to constructor of 'vec3'.">vec3</error>(1.0, vec2(1.0), vec2(1.0));
    vec4 <error descr="Incompatible types in initialization (and no available implicit conversion).">a</error> = VAR + 12;
    <error descr="Incompatible types in initialization (and no available implicit conversion).">or2</error> |= 2;
    vec3 <error descr="Incompatible types in initialization (and no available implicit conversion).">b</error> = a[0];
    vec3 <error descr="Incompatible types in initialization (and no available implicit conversion).">b</error> = a[0][0];
    ivec2 <error descr="Incompatible types in initialization (and no available implicit conversion).">vec</error> = vec2(1.0);
    bool <error descr="Incompatible types in initialization (and no available implicit conversion).">x</error> = fract((cos(dot(rand_seed, vec2(1., 1.))) * 1.));
    int <error descr="Incompatible types in initialization (and no available implicit conversion).">x</error> = fract((cos(dot(rand_seed, vec2(1., 1.))) * 1.));
    vec4 <error descr="Incompatible types in initialization (and no available implicit conversion).">vec</error> = vec3(1.0);
    <error descr="No matching function for call to cross().">cross</error>();
    <error descr="No matching function for call to funcMat(mat4).">funcMat</error>(mat4());
    <error descr="No matching function for call to funcMat3x2(mat2x3).">funcMat3x2</error>(mat2x3());
    <error descr="No matching function for call to funcIntFloat(float, float).">funcIntFloat</error>(1.f, 1.0f);
    <error descr="No matching function for call to funcInt(float).">funcInt</error>(1.0);
}