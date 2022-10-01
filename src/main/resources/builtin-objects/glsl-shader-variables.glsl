struct GLSL {
    vec4 gl_ClipVertex;
    vec4 gl_FrontColor;
    vec4 gl_BackColor;
    vec4 gl_FrontSecondaryColor;
    vec4 gl_BackSecondaryColor;
    vec4 gl_TexCoord[];
    float gl_FogFragCoord;
};
// Vertex Shader
struct VERT {
    in int gl_VertexID;
    in int gl_InstanceID;
    in int gl_VertexIndex;
    in int gl_InstanceIndex;
    in int gl_DrawID;
    in int gl_BaseVertex;
    in int gl_BaseInstance;
    vec4 gl_Position;
    float gl_PointSize;
    float gl_ClipDistance[];
    float gl_CullDistance[];
    in vec4 gl_Color;
    in vec4 gl_SecondaryColor;
    in vec3 gl_Normal;
    in vec4 gl_Vertex;
    in vec4 gl_MultiTexCoord0;
    in vec4 gl_MultiTexCoord1;
    in vec4 gl_MultiTexCoord2;
    in vec4 gl_MultiTexCoord3;
    in vec4 gl_MultiTexCoord4;
    in vec4 gl_MultiTexCoord5;
    in vec4 gl_MultiTexCoord6;
    in vec4 gl_MultiTexCoord7;
    in float gl_FogCoord;
};

// Fragment Shader
struct FRAG {
    in vec4 gl_FragCoord;
    in bool gl_FrontFacing;
    in float gl_ClipDistance[];
    in float gl_CullDistance[];
    in vec2 gl_PointCoord;
    in int gl_PrimitiveID;
    in int gl_SampleID;
    in vec2 gl_SamplePosition;
    in int gl_SampleMaskIn[];
    in int gl_Layer;
    in int gl_ViewportIndex;
    in bool gl_HelperInvocation;
    out float gl_FragDepth;
    out int gl_SampleMask[];
    out vec4 gl_FragColor;
    out vec4 gl_FragData[gl_MaxDrawBuffers];
};
// Geometry Shader
struct GEOM {
    vec4 gl_Position;
    float gl_PointSize;
    float gl_ClipDistance[];
    float gl_CullDistance[];
    in int gl_PrimitiveIDIn;
    in int gl_InvocationID;
    out int gl_PrimitiveID;
    out int gl_Layer;
    out int gl_ViewportIndex;
    struct gl_PerVertex {
        vec4 gl_Position;
        float gl_PointSize;
        float gl_ClipDistance[];
    } gl_in[];
};

// Compute Shader
struct COMP {
    in uvec3 gl_NumWorkGroups;
    const uvec3 gl_WorkGroupSize;
    in uvec3 gl_WorkGroupID;
    in uvec3 gl_LocalInvocationID;
    in uvec3 gl_GlobalInvocationID;
    in uint gl_LocalInvocationIndex;
};

// Tessellation Control Shader
struct TESC {
    vec4 gl_Position;
    float gl_PointSize;
    float gl_ClipDistance[];
    float gl_CullDistance[];
    in int gl_PatchVerticesIn;
    in int gl_PrimitiveID;
    in int gl_InvocationID;
    patch out float gl_TessLevelOuter[4];
    patch out float gl_TessLevelInner[2];
    struct gl_PerVertex {
        vec4 gl_Position;
        float gl_PointSize;
        float gl_ClipDistance[];
        float gl_CullDistance[];
    } gl_in[gl_MaxPatchVertices];
    struct gl_PerVertex {
        vec4 gl_Position;
        float gl_PointSize;
        float gl_ClipDistance[];
        float gl_CullDistance[];
    } gl_out[gl_MaxPatchVertices];
};

// Tessellation Evaluation Shader
struct TESE {
    vec4 gl_Position;
    float gl_PointSize;
    float gl_ClipDistance[];
    float gl_CullDistance[];
    in int gl_PatchVerticesIn;
    in int gl_PrimitiveID;
    in vec3 gl_TessCoord;
    patch in float gl_TessLevelOuter[4];
    patch in float gl_TessLevelInner[2];
};