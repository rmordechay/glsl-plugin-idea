struct A {
    vec4 vec;
};

in B {
    A a[];
} b;


void main() {
   b.a[0].vec.<caret>w;
}
