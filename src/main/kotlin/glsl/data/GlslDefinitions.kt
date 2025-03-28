package glsl.data

import glsl.GlslTypes.*


/**
 *
 */
enum class ShaderType {
    GLSL, VERT, TESC, TESE, GEOM, FRAG, COMP
}

object GlslDefinitions {

    /**
     *
     */
    const val BACKSLASH = "\\"

    /**
     *
     */
    val VERSIONS = arrayOf(
        "110", "120", "130", "140", "150", "330", "340", "350", "360", "370", "380", "390", "400", "410", "420",
        "430", "440", "450", "460"
    )

    /**
     *
     */
    val VERSION_MODES = arrayOf(
        "core", "compatibility", "es"
    )

    /**
     *
     */
    val VEC_MAT_CONSTRUCTORS = listOf(
        "bvec2", "bvec3", "bvec4", "vec2", "vec3", "vec4", "ivec2", "ivec3", "ivec4", "uvec2", "uvec3", "uvec4", "dvec2", "dvec3",
        "dvec4", "mat2", "mat3", "mat4", "mat2x2", "mat2x3", "mat2x4", "mat3x2", "mat3x3", "mat3x4", "mat4x2", "mat4x3", "mat4x4",
        "dmat2", "dmat3", "dmat4", "dmat2x2", "dmat2x3", "dmat2x4", "dmat3x2", "dmat3x3", "dmat3x4", "dmat4x2", "dmat4x3", "dmat4x4",
    )

    /**
     *
     */
    val SCALARS = mapOf(
        INT to listOf(INT, UINT, FLOAT, DOUBLE),
        UINT to listOf(UINT, FLOAT, DOUBLE),
        FLOAT to listOf(FLOAT, DOUBLE),
        DOUBLE to listOf(DOUBLE),
        BOOL to null,
    )

    /**
     *
     */
    val VECTORS = mapOf(
        VEC2 to listOf(VEC2, DVEC2),
        IVEC2 to listOf(IVEC2, VEC2, DVEC2),
        UVEC2 to listOf(UVEC2, VEC2, DVEC2),
        VEC3 to listOf(VEC3, DVEC3),
        IVEC3 to listOf(IVEC3, VEC3, DVEC3),
        UVEC3 to listOf(UVEC3, VEC3, DVEC3),
        VEC4 to listOf(VEC4, DVEC4),
        IVEC4 to listOf(IVEC4, VEC4, DVEC4),
        UVEC4 to listOf(UVEC4, VEC4, DVEC4),
        BVEC2 to null, BVEC3 to null, BVEC4 to null, DVEC2 to null, DVEC3 to null, DVEC4 to null,
        I64VEC2 to null, I64VEC3 to null, I64VEC4 to null, U64VEC2 to null, U64VEC3 to null,
        U64VEC4 to null, I8VEC2 to null, I8VEC3 to null, I8VEC4 to null, U8VEC2 to null,
        U8VEC3 to null, U8VEC4 to null, I16VEC2 to null, I16VEC3 to null, I16VEC4 to null,
        U16VEC2 to null, U16VEC3 to null, U16VEC4 to null, I32VEC2 to null, I32VEC3 to null,
        I32VEC4 to null, U32VEC2 to null, U32VEC3 to null, U32VEC4 to null, F16VEC2 to null,
        F16VEC3 to null, F16VEC4 to null, F32VEC2 to null, F32VEC3 to null, F32VEC4 to null,
        F64VEC2 to null, F64VEC3 to null, F64VEC4 to null,
    )

    /**
     *
     */
    val MATRICES = mapOf(
        MAT2 to listOf(DMAT2),
        MAT3 to listOf(DMAT3),
        MAT4 to listOf(DMAT4),
        MAT2X3 to listOf(DMAT2X3),
        MAT2X4 to listOf(DMAT2X4),
        MAT3X2 to listOf(DMAT3X2),
        MAT3X4 to listOf(DMAT3X4),
        MAT4X2 to listOf(DMAT4X2),
        MAT4X3 to listOf(DMAT4X3),
        DMAT2 to null, DMAT3 to null, DMAT4 to null, DMAT2X2 to null, DMAT2X3 to null, DMAT2X4 to null,
        DMAT3X2 to null, DMAT3X3 to null, DMAT3X4 to null, DMAT4X2 to null, DMAT4X3 to null, DMAT4X4 to null,
        F16MAT2 to null, F16MAT3 to null, F16MAT4 to null, F16MAT2X2 to null, F16MAT2X3 to null,
        F16MAT2X4 to null, F16MAT3X2 to null, F16MAT3X3 to null, F16MAT3X4 to null, F16MAT4X2 to null,
        F16MAT4X3 to null, F16MAT4X4 to null, F32MAT2 to null, F32MAT3 to null, F32MAT4 to null,
        F32MAT2X2 to null, F32MAT2X3 to null, F32MAT2X4 to null, F32MAT3X2 to null, F32MAT3X3 to null,
        F32MAT3X4 to null, F32MAT4X2 to null, F32MAT4X3 to null, F32MAT4X4 to null, F64MAT2 to null,
        F64MAT3 to null, F64MAT4 to null, F64MAT2X2 to null, F64MAT2X3 to null, F64MAT2X4 to null,
        F64MAT3X2 to null, F64MAT3X3 to null, F64MAT3X4 to null, F64MAT4X2 to null, F64MAT4X3 to null,
        F64MAT4X4 to null,
    )
}



