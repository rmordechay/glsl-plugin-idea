struct MyType {
    int a;
} myType;

struct light {
    float intensity;
    vec3 position;
};

light lightVar = light(3.0, vec3(1.0, 2.0, 3.0));

void main() {
    cross(vec3(1), vec3(1));
    abs(gl_FragCoord.x);
    float arr[24];
    abs(arr[4]);
    int c = 1;
    abs(c++);


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
    vec4 <error descr="Incompatible types in initialization (and no available implicit conversion). Required: vec4; Found: int.">a</error> = VAR + 12;
    or2 |= 2;
    vec3 <error descr="Incompatible types in initialization (and no available implicit conversion). Required: vec3; Found: float.">b</error> = a[0];
    vec3 <error descr="Incompatible types in initialization (and no available implicit conversion). Required: vec3; Found: float.">b</error> = a[0][0];
    ivec2 <error descr="Incompatible types in initialization (and no available implicit conversion). Required: ivec2; Found: vec2.">vec</error> = vec2(1.0);
    vec4 <error descr="Incompatible types in initialization (and no available implicit conversion). Required: vec4; Found: vec3.">vec</error> = vec3(1.0);
}