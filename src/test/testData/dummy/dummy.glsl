struct Ray {
    vec3 origin;
    vec3 direction;
};

Ray rayFromTo(in vec3 from, in vec3 to) {
    return Ray(from, normalize(to - from));
}

void createRayPerspective(in vec2 resolution, in vec2 screenPosition, in float verticalFov) {
    vec2 topLeft = vec2(-resolution.x, -resolution.y) * .5;
    float z = (resolution.x * .5) / abs(tan(verticalFov / 2.0));

    return Ray(vec3(0.0, 0.0, 0.0), normalize(vec3(topLeft + screenPosition, -z)));
}

vec3 positionOnRay(in Ray ray, in float t) {
    return ray.origin + ray.direction * t;
}

void transformRay(inout Ray ray, mat4 matrix) {
    ray.origin = (matrix * vec4(ray.origin, 1.0)).xyz;
    ray.direction = normalize(matrix * vec4(ray.direction, 0.0)).xyz;
}

void reflectRay(inout Ray ray, vec3 position, vec3 normal) {
    ray.origin = position + normal * epsilon;
    ray.direction = reflect(ray.direction, normal);
}