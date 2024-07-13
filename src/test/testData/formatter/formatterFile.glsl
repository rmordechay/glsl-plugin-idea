#define VAR void a;
#define VAR2( a  ,  b  ) a  +  b;

int c = VAR2( 1   ,   2 )
#version    330
// One line comment
/*
* Multi line comment
*/

int mouse2 = vec2( -   iTime   /   148.  , cos( iTime   ) / 24.);
int mouse3  =   vec2 (  -  iTime  )  ;

#define DEF void f(int aa, float a);
#define DEF2(roi, roi2) void f(int roi, float roi2);
#define DEF3 if (a > 2) { a = 2; a = 2; a = 2; }
void main() {
DEF
        DEF2(  a   ,   b  )
  DEF3
}


#define FUN(a, b) a + b;
#define DEF4 int a = 2;
#define PI 3.14

void   main(  )   {
DEF
   FUN(1, 2)
    float a = normalize(  a  *   PI  );
}

        #define BINDLESS_TEX(ty, name) \
 layout (set = BINDLESS_SET, binding = BINDLESS_TEX_BINDING) \
 uniform ty name[BINDLESS_TEX_COUNT];

#include "file.h"
__LINE__ 10

#include < shaders/func.frag >

#define f(a, b) a + \
b

        int dummy = f(  1,  2   ) ;

// One line comment
    struct  DummyStruct     {
                // One line comment
            int struct_declarator1  ;
    int struct_declarator2[   ]  ;
       int         struct_declarator3   [  ]  []   ;
    int struct_declarator4, struct_declarator5;
}   dummyStruct       ;

void func(int a,    int a,    int    a, int a, int a);

void func(int a,         int a,         int     a,
int a,              int a);

void func(int a,
           int a,
    int a,
int a,
int a);

void func(
int a,
                int a,
    int a,
                int a,
int a
);

void func(
            int a,
int a,
int a,
int a,
    int a);

int a = func(
                vec2(0.0, 0.8),
    vec2(-0.6, -0.8),
vec2(0.6, -0.8)
);

const float CONSTANT_DECLRATION = 0.2 ;
    float singl_declration[ ] = {1, 2    ,  3    };
float   singl_declration  [ 2] = 0.2;
int a =     a.b.c.i;

    // One line comment
float a[1   ]  [2][3], b   [2]  [1]   =    2     ;

int a =   a;
int a = a  [1]  ;
int a = a   [1]    [2];
int a = a [1]    [2][3];


float _a = { 1.0, 2.0, 3.0, 4.0 };
float _a =  { 1.0 + 2, 2.0 * 2, 3.0 / 2, 4.0 - 2.0 };

float[] a = float[](    1, 2, 3);
float[] a = float[3 ](  1,     2, 3  );

int a =     -       (-1);
int a =     -       1;
int a = -   1u + -1 -  1   -   -2;
int a =    -    dot(vec4(), vec4());
int a =  -   a    [   0    ];
int a =    -     a   ++  ;
int a =    -    a.a    ;
int a = 1 -   1u;
int a = 1;
int a = 1  +  2 /   (2  +  2) + 1 * 2   ;

  int   a    = a(  )   ;

layout (location) in    vec2      layout_declration;
layout (location = 2) in vec2    layout_declration;
layout (location = 1,    max_vertices   )  in vec4   layout_declration   ;
layout (location = 1, max_vertices   =    2 > a) in vec4    layout_declration  ;
layout (triangles_strip, max_vertices = 3) out  ;
layout (triangles_strip, max_vertices = 3)  ;

layout (location =   3  ) centroid  out float   layout_declration;
layout  (   location) uniform UniformType        {
       mat3 a   ;
    mat4    b ;
}   uni   ;


    void    main(  UserType   a      , UserType b)  {}
    UserType main   () {}


    noperspective   centroid out    vec2 a  ;
centroid   out    vec2 a;
smooth   out vec2 a[];
  // One line comment
int     function_definition     (    float x  , float y     );
int function_definition(    DummyBlock x  , float y     )      {
    int a = 2   == b   ?    b    :     abs()  ;
    void main();
    expr_statement();
// One line comment
    if ( selection_statement == 1    ) {
// One line comment
        if (    inner_id < 6    ) {
            dummy   ;
        }
    } else if (  selection_statement_else   ) {
        dummy;
    } else {
        dummy;
    }

    if (_x % int(a.x / 2)       == 0 ||          _y % int(b.y / 2) == 0     ) {
        a = vec4        (  0, 0, 1, 1);
        a = abs        (  0, 0, 1, 1);
    }

    for (  int i; i    >         20; i++  ) {
        int a = i       ;
                a = i +   2;
    }

    while (shouldRun)    {
                if (!shouldRun)      {
                call()  ;
        } else      {
                    call();
            continue        ;
        }
                    shouldRun = 1;
    }

                      do {
        int shouldRun = 1;
        shouldRun   = 0;
    }   while (  shouldRun);

switch        (  switch_statement        ) {
                                        case 1          :
                int         a = 1;
            return      1        ;
case 2              :
    if  (           true    )       {
                                doSomthing()    ;
            }
                        return 2;
    default     :
            int     a       ;
                                return 3        ;
    }

    float declaration_statement  [  2  ]    = func(   g   (   h(x, y), z));

        demote ;
      continue  ;
                break  ;
    return   jump_statement  ;
    EmitVertex();
    gl_Position    = a.b   * vec4(  (   x * vec3(    5.0,   5.0,  0.0)   ) +    y[0].xyz,   1.0);
}

