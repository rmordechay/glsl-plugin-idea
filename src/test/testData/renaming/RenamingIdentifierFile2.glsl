struct Third {
    vec4 v;
} third;

struct Second {
    Third third;
} second;

struct First {
    Second second;
} first;

int dummy = first.second.third.v.<caret>xyz;