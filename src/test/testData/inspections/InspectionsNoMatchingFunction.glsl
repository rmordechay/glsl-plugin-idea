#include "InspectionsNoMatchingFunction2.glsl"

float a = <error descr="No matching function for call to normalize(int, int).">normalize(2, 2)</error>;

float smoothFunc(float a, float b, float x) {
    float a = smoothFunc(2, 2);
    return smoothFunc(2);
}