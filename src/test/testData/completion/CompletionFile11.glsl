struct A {
    vec4 v;
} a;

struct B {
    A first;
} b;

int main() {
    int b = b.first.v.rgb.<caret>;
}