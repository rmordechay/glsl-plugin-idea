int funcInt(int x);
void funcFloat(float x);
void funcIntFloat(int x, float y);
void funcVec4(vec4 vec);
void funcIvec4(ivec4 vec);
void funcMat(ivec4 vec);
void funcMat(mat3 mat);
void funcMat3x2(mat3x2 mat);
struct MyType {int a;};
void funcMyType(MyType myType);


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

<error descr="No matching function for call to cross().">cross</error>();
<error descr="No matching function for call to funcMat(mat4).">funcMat</error>(mat4());
<error descr="No matching function for call to funcMat3x2(mat2x3).">funcMat3x2</error>(mat2x3());
<error descr="No matching function for call to funcIntFloat(float, float).">funcIntFloat</error>(1.f, 1.0f);
<error descr="No matching function for call to funcInt(float).">funcInt</error>(1.0);
}