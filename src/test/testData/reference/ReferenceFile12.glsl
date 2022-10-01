void f1(int param1, int param2) {
    if (param2 == 1) {
        while (true) {
            int a = <caret>param1;
        }
    }
}
