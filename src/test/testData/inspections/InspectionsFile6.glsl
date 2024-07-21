// Scalar-Scalar
float a0 = 1 + 1;
float a1 = 1 - 1;
float a2 = 1 * 1;
float a3 = 1 / 1;
// Scalar-Vector
vec3 a12 = 1 + vec3(1);
vec3 a13 = 1 - vec3(1);
vec3 a14 = 1 * vec3(1);
vec3 a15 = 1 / vec3(1);
// Scalar-Matrix
mat3 a20 = 1 + mat3(1);
mat3 a21 = 1 - mat3(1);
mat3 a22 = 1 * mat3(1);
mat3 a23 = 1 / mat3(1);
// Vector-Scalar
vec3 a16 = vec3(1) + 1;
vec3 a17 = vec3(1) - 1;
vec3 a18 = vec3(1) * 1;
vec3 a19 = vec3(1) / 1;
// Vector-Vector
vec3 a4 = vec3(1) + vec3(1);
vec3 a5 = vec3(1) - vec3(1);
vec3 a6 = vec3(1) * vec3(1);
vec3 a7 = vec3(1) / vec3(1);
// Vector-Matrix
mat3 a28 = <error descr="'+' does not operate on 'vec3' and 'mat3'.">vec3(1) + mat3(1)</error>;
mat3 a29 = <error descr="'-' does not operate on 'vec3' and 'mat3'.">vec3(1) - mat3(1)</error>;
vec3 a30 = vec3(1) * mat3(1);
mat3 a31 = <error descr="'/' does not operate on 'vec3' and 'mat3'.">vec3(1) / mat3(1)</error>;
// Matrix-Scalar
mat3 a24 = mat3(1) + 1;
mat3 a25 = mat3(1) - 1;
mat3 a26 = mat3(1) * 1;
mat3 a27 = mat3(1) / 1;
// Matrix-Vector
mat3 a32 = <error descr="'+' does not operate on 'mat3' and 'vec3'.">mat3(1) + vec3(1)</error>;
mat3 a33 = <error descr="'-' does not operate on 'mat3' and 'vec3'.">mat3(1) - vec3(1)</error>;
vec3 a34 = mat3(1) * vec3(1);
mat3 a35 = <error descr="'/' does not operate on 'mat3' and 'vec3'.">mat3(1) / vec3(1)</error>;
// Matrix-Matrix
mat3 a8 = mat3(1) + mat3(1);
mat3 a9 = mat3(1) - mat3(1);
mat3 a10 = mat3(1) * mat3(1);
mat3 a11 = mat3(1) / mat3(1);