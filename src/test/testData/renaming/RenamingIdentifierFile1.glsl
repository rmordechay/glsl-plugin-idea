struct Third {
    int i;
} third;

struct Second {
    Third third;
} second;

struct First {
    Second second;
} first;

int dummy = first.second.<caret>third;