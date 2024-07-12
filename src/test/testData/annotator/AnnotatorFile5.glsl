#define f(a, b) a + \
b
int dummy = f(1, 2);

#define f2(a, b) a + b
int dummy = f2(g(1, c), h(2, 3));