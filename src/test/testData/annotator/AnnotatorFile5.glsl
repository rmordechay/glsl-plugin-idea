#define DEF void f(int aa, float a)
#define DEF2(roi, roi2) void f(int roi, float roi2)
void main() {
    DEF;
    DEF2(a, b);
}