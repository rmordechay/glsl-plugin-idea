#define BINDLESS_SET 1
#define BINDLESS_TEX_BINDING 0
#define BINDLESS_TEX_COUNT 1024
#define BINDLESS_TEX(ty, name) \
    layout (set = BINDLESS_SET, binding = BINDLESS_TEX_BINDING) \
    uniform ty name[BINDLESS_TEX_COUNT];

BINDLESS_TEX(sampler2D, u_global_textures)