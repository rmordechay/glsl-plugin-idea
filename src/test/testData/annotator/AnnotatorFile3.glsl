int f();
int f2(int a);
int a1 = normalize(2);
int a2 = normaliz(2, 3);
int a3 = normalize<error descr="No matching function for call to normalize().">(2, 3)</error>;
int a4 = f();
int a5 = f<error descr="No matching function for call to f().">(2, 3)</error>;
int a6 = f2<error descr="No matching function for call to f2().">()</error>;
int a7 = f2<error descr="No matching function for call to f2().">(1, 2)</error>;
int a8 = f2(1);
#define A 0.70
int a9 = vec3(1.0, A, 1.0);
float f();
float g();
float h();
float a10 = f<error descr="No matching function for call to f().">(g<error descr="No matching function for call to g().">(h())</error>)</error>;

struct Ray {
    int origin;
    int direction;
};

void transformRay(inout Ray ray, mat4 matrix) {
    Ray rab = Ray<error descr="Too few arguments to constructor of 'Ray'.">(1)</error>;
    Ray rab = Ray(1, 2);
    Ray rab = Ray<error descr="Too many arguments to constructor of 'Ray'.">(1, 2, 3)</error>;
}