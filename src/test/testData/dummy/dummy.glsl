struct Ray {
    vec3 origin;
    vec3 direction;
};

void transformRay(inout Ray ray, mat4 matrix) {
    ray.<caret>origin = (matrix * vec4(ray.origin, 1.0)).xyz;
    ray.direction = normalize(matrix * vec4(ray.direction, 0.0)).xyz;
}