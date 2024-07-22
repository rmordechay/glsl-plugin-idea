void main() {
    int a = <error descr="Constructor of primitive type must have at least one argument.">int()</error>;
    int a = int(1);
    mat3 a = <error descr="Constructor of primitive type must have at least one argument.">mat3()</error>;
    mat3 a = mat3(1);
    vec3 a = <error descr="Constructor of primitive type must have at least one argument.">vec3()</error>;
    vec3 a = vec3(1);
}