struct A {
    vec4 v;
} a;

struct B {
    vec4 first;
} b;

int main() {
    int b = b.first.xy.<caret>;
}