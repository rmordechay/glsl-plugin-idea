#version 330
struct Ray {
    int origin;
    int direction;
};

void transformRay(inout Ray ray, mat4 matrix) {
    Ray rab = Ray<error descr="Too few arguments to constructor of 'Ray'.">(1)</error>;
    Ray rab = Ray(1, 2);
    Ray rab = Ray<error descr="Too many arguments to constructor of 'Ray'.">(1, 2, 3)</error>;
}