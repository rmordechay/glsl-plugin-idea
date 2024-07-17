
vec2 a = <error descr="Incompatible types in initialization (and no available implicit conversion).">normalize(2)</error>;
void bezier(float cp0, float cp1, float cp2, vec4 t) {
    float p0 = <error descr="Incompatible types in initialization (and no available implicit conversion).">mix(cp0, cp1, t)</error>;
}