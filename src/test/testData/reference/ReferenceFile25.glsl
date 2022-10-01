layout (location) in Pos {
    vec4 vec;
} a;

void main() {
    vec4 dummy = <caret>vec;
}