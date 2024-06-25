#define EPSILON_NRM iResolution

void main() {
    vec3 p<caret>;
    heightMapTracing(p);
    vec3 dist = p - ori;
    vec3 n = getNormal(p, dot(dist,dist) * EPSILON_NRM);
}