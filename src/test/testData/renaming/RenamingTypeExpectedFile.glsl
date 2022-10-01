struct NewName {
    int i;
} third;

struct Second {
    NewName third;
} second;

struct First {
    Second second;
} first;

NewName dummy = first.second.third;