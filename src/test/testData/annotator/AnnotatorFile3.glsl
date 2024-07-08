#define BINDLESS_TEX(ty, name) \
 layout (set = 0, binding = 0) \
 uniform ty name[0];
BINDLESS_TEX(sampler2D, u_global_textures)


