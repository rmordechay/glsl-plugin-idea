int <caret>a;
int b = a;
int main() {
    int b = abs(a);
    if (b) {
        a;
    }
}