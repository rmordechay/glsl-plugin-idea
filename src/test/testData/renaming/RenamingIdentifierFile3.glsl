#define f(a, b) 10

void main() {
    int a = <caret>f(1, 2) + 10;
    int b = abs(f(1, 2));
}
