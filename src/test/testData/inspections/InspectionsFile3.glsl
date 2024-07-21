int a = <error descr="Incompatible types in initialization (and no available implicit conversion).">normalize(2.0)</error>;
int a = <error descr="Incompatible types in initialization (and no available implicit conversion).">normalize(2)</error>;
float a = normalize(2);
float a = normalize(2.0f);
float a = normalize(2.f);
