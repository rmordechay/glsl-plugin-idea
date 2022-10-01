struct Inner {
    vec4 pos;
    vec4 vel;
};

layout (location) buffer Pos {
    Inner inner[];
};

void main() {
    int index = 1;
    inner[index].<caret>vel.xyz += 1;
}