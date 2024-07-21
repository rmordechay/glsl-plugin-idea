int a = <error descr="Incompatible types in initialization (and no available implicit conversion).">normalize(2.0)</error>;
int a = <error descr="Incompatible types in initialization (and no available implicit conversion).">normalize(2)</error>;
float a = normalize(2);
float a = normalize(2.0f);
float a = normalize(2.f);
float a = normalize(2);
int b, c = <error descr="Incompatible types in initialization (and no available implicit conversion).">a</error>;
int b, c = <error descr="Incompatible types in initialization (and no available implicit conversion).">2.0f</error>;
