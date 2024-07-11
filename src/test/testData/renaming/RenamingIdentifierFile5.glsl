struct Third {
    int i;
} third;

struct Second {
    Third third;
} second;

struct First {
    Second second;
} first;

<caret>Third dummy = first.second.third;