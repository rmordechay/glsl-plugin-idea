struct Ray {
    vec3 origin;
    vec3 direction;
};

Ray rayFromTo(in vec3 from, in vec3 to) {
    return Ray(from, normalize(to - from), 3, 5, 6);
}