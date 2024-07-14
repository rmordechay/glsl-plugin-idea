int f() {<error descr="Missing return for function 'f'.">}</error>
int f() {return 0;}
int f() {if (true) { return 0; }}
void f() {}
void f() {if (true) { }}
