struct A {
    int c;
} a;

struct B {
    A a;
} b;

int dummy = b.a.<caret>c;