layout (location) in Pos {
    vec4 vec;
};

void main() {
    vec4 dummy = <caret>vec;
}