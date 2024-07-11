int <info descr="null">f</info>();
int <info descr="null">f2</info>(int <info descr="null">a</info>);
int <info descr="null">a1</info> = <info descr="null">normalize</info>(2);
int <info descr="null">a2</info> = normaliz(2, 3);
int <info descr="null">a3</info> = <info descr="null">normalize</info>(<error descr="Incorrect number of parameters">2, 3</error>);
int <info descr="null">a4</info> = <info descr="null">f</info>();
int <info descr="null">a5</info> = <info descr="null">f</info>(<error descr="Incorrect number of parameters">2, 3</error>);
int <info descr="null">a6</info> = <info descr="null">f2</info><error descr="Incorrect number of parameters">()</error>;
int <info descr="null">a7</info> = <info descr="null">f2</info>(<error descr="Incorrect number of parameters">1, 2</error>);
int <info descr="null">a8</info> = <info descr="null">f2</info>(1);
#define <info descr="null">A</info> 0.70
int <info descr="null">a9</info> = vec3(1.0, <info descr="null">A</info>, 1.0);