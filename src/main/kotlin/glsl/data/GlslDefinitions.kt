package glsl.data


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
    val VEC_MAT_CONSTRUCTORS = listOf(
        "bvec2", "bvec3", "bvec4", "vec2", "vec3", "vec4", "ivec2", "ivec3", "ivec4", "uvec2", "uvec3", "uvec4", "dvec2", "dvec3",
        "dvec4", "mat2", "mat3", "mat4", "mat2x2", "mat2x3", "mat2x4", "mat3x2", "mat3x3", "mat3x4", "mat4x2", "mat4x3", "mat4x4",
        "dmat2", "dmat3", "dmat4", "dmat2x2", "dmat2x3", "dmat2x4", "dmat3x2", "dmat3x3", "dmat3x4", "dmat4x2", "dmat4x3", "dmat4x4",
    )

    /**
     *
     */
    val SCALARS = mapOf(
        "int" to listOf("uint", "float", "double"),
        "uint" to listOf("float", "double"),
        "float" to listOf("double"),
        "double" to listOf("double"),
        "bool" to null,
    )

    /**
     *
     */
    val VECTORS = mapOf(
        "vec2" to listOf("dvec2"),
        "fvec2" to listOf("dvec2"),
        "ivec2" to listOf("vec2", "dvec2"),
        "uvec2" to listOf("vec2", "dvec2"),
        "vec3" to listOf("dvec3"),
        "fvec3" to listOf("dvec3"),
        "ivec3" to listOf("vec3", "dvec3"),
        "uvec3" to listOf("vec3", "dvec3"),
        "vec4" to listOf("dvec4"),
        "fvec4" to listOf("dvec4"),
        "ivec4" to listOf("vec4", "dvec4"),
        "uvec4" to listOf("vec4", "dvec4"),
        "bvec2" to null, "bvec3" to null, "bvec4" to null, "dvec2" to null, "dvec3" to null, "dvec4" to null,
        "i64vec2" to null, "i64vec3" to null, "i64vec4" to null, "u64vec2" to null, "u64vec3" to null,
        "u64vec4" to null, "i8vec2" to null, "i8vec3" to null, "i8vec4" to null, "u8vec2" to null,
        "u8vec3" to null, "u8vec4" to null, "i16vec2" to null, "i16vec3" to null, "i16vec4" to null,
        "u16vec2" to null, "u16vec3" to null, "u16vec4" to null, "i32vec2" to null, "i32vec3" to null,
        "i32vec4" to null, "u32vec2" to null, "u32vec3" to null, "u32vec4" to null, "f16vec2" to null,
        "f16vec3" to null, "f16vec4" to null, "f32vec2" to null, "f32vec3" to null, "f32vec4" to null,
        "f64vec2" to null, "f64vec3" to null, "f64vec4" to null,

    )

    /**
     *
     */
    val MATRICES = mapOf(
        "mat2" to listOf("dmat2"),
        "mat3" to listOf("dmat3"),
        "mat4" to listOf("dmat4"),
        "mat2x3" to listOf("dmat2x3"),
        "mat2x4" to listOf("dmat2x4"),
        "mat3x2" to listOf("dmat3x2"),
        "mat3x4" to listOf("dmat3x4"),
        "mat4x2" to listOf("dmat4x2"),
        "mat4x3" to listOf("dmat4x3"),
        "dmat2" to null, "dmat3" to null, "dmat4" to null, "dmat2x2" to null, "dmat2x3" to null, "dmat2x4" to null,
        "dmat3x2" to null, "dmat3x3" to null, "dmat3x4" to null, "dmat4x2" to null, "dmat4x3" to null, "dmat4x4" to null,
        "f16mat2" to null, "f16mat3" to null, "f16mat4" to null, "f16mat2x2" to null, "f16mat2x3" to null,
        "f16mat2x4" to null, "f16mat3x2" to null, "f16mat3x3" to null, "f16mat3x4" to null, "f16mat4x2" to null,
        "f16mat4x3" to null, "f16mat4x4" to null, "f32mat2" to null, "f32mat3" to null, "f32mat4" to null,
        "f32mat2x2" to null, "f32mat2x3" to null, "f32mat2x4" to null, "f32mat3x2" to null, "f32mat3x3" to null,
        "f32mat3x4" to null, "f32mat4x2" to null, "f32mat4x3" to null, "f32mat4x4" to null, "f64mat2" to null,
        "f64mat3" to null, "f64mat4" to null, "f64mat2x2" to null, "f64mat2x3" to null, "f64mat2x4" to null,
        "f64mat3x2" to null, "f64mat3x3" to null, "f64mat3x4" to null, "f64mat4x2" to null, "f64mat4x3" to null,
        "f64mat4x4" to null,
    )

    /**
     *
     */
    val SCALARS_CONSTRUCTORS = setOf(
        *SCALARS.keys.toTypedArray(),
        *VECTORS.keys.toTypedArray(),
        *MATRICES.keys.toTypedArray(),
    )
}



