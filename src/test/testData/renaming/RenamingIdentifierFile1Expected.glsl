struct Third {
    int i;
} third;

struct Second {
    Third newName;
} second;

struct First {
    Second second;
} first;

int dummy = first.second.newName;