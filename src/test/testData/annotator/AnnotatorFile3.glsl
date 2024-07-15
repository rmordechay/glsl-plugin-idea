int <info descr="null">f</info>();
int <info descr="null">f2</info>(int <info descr="null">a</info>);
int <info descr="null">a1</info> = <info descr="null">normalize</info>(2);
int <info descr="null">a2</info> = normaliz(2, 3);
int <info descr="null">a3</info> = <info descr="null">normalize</info>(<error descr="No matching function for call to normalize().">2, 3</error>);
int <info descr="null">a4</info> = <info descr="null">f</info>();
int <info descr="null">a5</info> = <info descr="null">f</info>(<error descr="No matching function for call to f().">2, 3</error>);
int <info descr="null">a6</info> = <info descr="null">f2</info><error descr="No matching function for call to f2().">()</error>;
int <info descr="null">a7</info> = <info descr="null">f2</info>(<error descr="No matching function for call to f2().">1, 2</error>);
int <info descr="null">a8</info> = <info descr="null">f2</info>(1);
#define <info descr="null">A</info> 0.70
int <info descr="null">a9</info> = vec3(1.0, <info descr="null">A</info>, 1.0);
float <info descr="null">f</info>();
float <info descr="null">g</info>();
float <info descr="null">h</info>();
float <info descr="null">a10</info> = <info descr="null">f</info>(<error descr="No matching function for call to f()."><info descr="null">g</info>(<error descr="No matching function for call to g()."><info descr="null">h</info>()</error>)</error>);