#version 100
int add(int x, int y) {
    return x + y;
}
void main() {
    int i = int(10);
    int i = int(10.0);
    int i = int(true);
    float i = float(10);
    float i = float(10.0);
    float i = float(true);
    double i = double(10);
    double i = double(10.0);
    double i = double(true);
    int i = int(double(true));
    float i = float(add(1, 2));
    int i = <error descr="Too many arguments to constructor of 'int'.">int</error>(true, true);
    struct A {
        int a;
    } aa;
    int i = <error descr="Invalid type 'A' as argument 1 of constructor of 'int'.">int</error>(aa);
}