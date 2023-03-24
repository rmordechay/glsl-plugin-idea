package glsl.data

import com.intellij.psi.tree.TokenSet
import glsl.GlslTypes.*



object GlslDefinitions {

    enum class ShaderType {
        GLSL, VERT, TESC, TESE, GEOM, FRAG, COMP
    }

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

/**
 *
 */
object GlslTokenSets {
    val NUMBER_SET = TokenSet.create(FLOATCONSTANT, INTCONSTANT, UINTCONSTANT, DOUBLECONSTANT)

    val SELECTION = TokenSet.create(IF, ELSE, SWITCH, CASE, DEFAULT)

    val ITERATION = TokenSet.create(FOR, WHILE, DO)

    val FUNC_JUMPS = TokenSet.create(RETURN, DISCARD)

    val ITERATION_JUMPS = TokenSet.create(BREAK, CONTINUE)

    val KEYWORDS = TokenSet.create(BREAK, CONTINUE, DO, FOR, WHILE, SWITCH, CASE, DEFAULT, IF, ELSE, DISCARD, CENTROID, INOUT, IN, OUT, CONST, UNIFORM, ATTR,
        VARYING, BUFFER, VOLATILE, PATCH, SAMPLE, SUBROUTINE, COHERENT, DEVICECOHERENT, QUEUEFAMILYCOHERENT, WORKGROUPCOHERENT, SUBGROUPCOHERENT, SHADERCALLCOHERENT,
        NONPRIVATE, RESTRICT, READONLY, WRITEONLY, INVARIANT, SHARED, STRUCT, TERMINATE_INVOCATION, TERMINATE_RAY, IGNORE_INTERSECTION, RETURN,
        LAYOUT, HIGH_PRECISION, MEDIUM_PRECISION, LOW_PRECISION, PRECISION, PRECISE, NONUNIFORM, DEMOTE, PAYLOADNV, PAYLOADEXT, PAYLOADINNV, PAYLOADINEXT,
        HITATTRNV, HITATTREXT, CALLDATANV, CALLDATAEXT, CALLDATAINNV, CALLDATAINEXT, SMOOTH, FLAT, NOPERSPECTIVE,
        EXPLICITINTERPAMD, PERVERTEXNV, PERPRIMITIVENV, PERVIEWNV, PERTASKNV, SPIRV_INSTRUCTION, SPIRV_EXECUTION_MODE, SPIRV_EXECUTION_MODE_ID, SPIRV_DECORATE,
        SPIRV_DECORATE_ID, SPIRV_DECORATE_STRING, SPIRV_TYPE, SPIRV_STORAGE_CLASS, SPIRV_BY_REFERENCE, SPIRV_LITERAL,
    )

    val PREPROCESSORS = TokenSet.create(
        HASH, PP_DEFINE, PP_ELIF, PP_ELSE, PP_ENDIF, PP_ERROR, PP_EXTENSION, PP_IF, PP_IFDEF, PP_IFNDEF,
        PP_LINE, PP_PRAGMA, PP_UNDEF, PP_VERSION, PP_INCLUDE, MACRO_LINE, MACRO_FILE, MACRO_VERSION
    )

    val ALL_OPERATORS = TokenSet.create(PLUS, DASH, SLASH, PERCENT, EQ_OP, GE_OP, NE_OP, LE_OP, OR_OP, XOR_OP, VERTICAL_BAR,
        CARET, RIGHT_OP, LEFT_OP, INC_OP, DEC_OP,)

    val ADD_OPERATORS = TokenSet.create(PLUS, DASH)

    val MUL_OPERATORS = TokenSet.create(STAR, SLASH, PERCENT)

    val EQUALITY_OPERATORS = TokenSet.create(EQ_OP, GE_OP)

    val RELATIONAL_OPERATORS = TokenSet.create(LEFT_ANGLE, RIGHT_ANGLE, NE_OP, LE_OP)

    val LOGICAL_OPERATORS = TokenSet.create(AND_OP, OR_OP, XOR_OP)

    val BITWISE_OPERATORS = TokenSet.create(AMPERSAND, VERTICAL_BAR, CARET)

    val SHIFT_OPERATORS = TokenSet.create(RIGHT_OP, LEFT_OP)

    val UNARY_OPERATORS = TokenSet.create(INC_OP, DEC_OP)

    val BUILTIN_TYPES = setOf(
        VOID, FLOAT, DOUBLE, INT, UINT, BOOL, FLOAT16_T, FLOAT32_T, FLOAT64_T, INT64_T, UINT64_T, INT32_T, UINT32_T, INT16_T,
        UINT16_T, INT8_T, UINT8_T , VEC2, VEC3, VEC4, BVEC2, BVEC3, BVEC4, IVEC2, IVEC3, IVEC4, UVEC2, UVEC3, UVEC4,
        MAT2, MAT3, MAT4, MAT2X2, MAT2X3, MAT2X4, MAT3X2, MAT3X3, MAT3X4, MAT4X2, MAT4X3, MAT4X4,
        DVEC2, DVEC3, DVEC4, DMAT2, DMAT3, DMAT4, DMAT2X2, DMAT2X3, DMAT2X4, DMAT3X2, DMAT3X3,
        DMAT3X4, DMAT4X2, DMAT4X3, DMAT4X4, ATOMIC_UINT, SAMPLER1D, SAMPLER2D, SAMPLER3D, SAMPLERCUBE,
        SAMPLER2DSHADOW, SAMPLERCUBESHADOW, SAMPLER2DARRAY, SAMPLER2DARRAYSHADOW, SAMPLER1DSHADOW, SAMPLER1DARRAY,
        SAMPLER1DARRAYSHADOW, SAMPLERCUBEARRAY, SAMPLERCUBEARRAYSHADOW, ISAMPLER1D, ISAMPLER2D, ISAMPLER3D, ISAMPLERCUBE, ISAMPLER2DARRAY,
        USAMPLER2D, USAMPLER3D, USAMPLERCUBE, ISAMPLER1DARRAY, ISAMPLERCUBEARRAY, USAMPLER1D, USAMPLER1DARRAY,
        USAMPLERCUBEARRAY, USAMPLER2DARRAY, SAMPLER2DRECT, SAMPLER2DRECTSHADOW, ISAMPLER2DRECT, USAMPLER2DRECT, SAMPLERBUFFER,
        ISAMPLERBUFFER, USAMPLERBUFFER, SAMPLER2DMS, ISAMPLER2DMS, USAMPLER2DMS, SAMPLER2DMSARRAY,
        ISAMPLER2DMSARRAY, USAMPLER2DMSARRAY, IMAGE1D, IIMAGE1D, UIMAGE1D, IMAGE2D, IIMAGE2D, UIMAGE2D, IMAGE3D, IIMAGE3D,
        UIMAGE3D, IMAGE2DRECT, IIMAGE2DRECT, UIMAGE2DRECT, IMAGECUBE, IIMAGECUBE, UIMAGECUBE, IMAGEBUFFER, IIMAGEBUFFER,
        UIMAGEBUFFER, IMAGE1DARRAY, IIMAGE1DARRAY, UIMAGE1DARRAY, IMAGE2DARRAY, IIMAGE2DARRAY, UIMAGE2DARRAY, IMAGECUBEARRAY,
        IIMAGECUBEARRAY, UIMAGECUBEARRAY, IMAGE2DMS, IIMAGE2DMS, UIMAGE2DMS, IMAGE2DMSARRAY, IIMAGE2DMSARRAY, UIMAGE2DMSARRAY,
    )

    val TYPE_QUALIFIERS = TokenSet.create(
        CONST, INOUT, IN, OUT, CENTROID, UNIFORM, SHARED, BUFFER, ATTR, VARYING, PATCH, SAMPLE, HITATTRNV,
        HITATTREXT, PAYLOADNV, PAYLOADEXT, PAYLOADINNV, PAYLOADINEXT, CALLDATANV, CALLDATAEXT, CALLDATAINNV,
        CALLDATAINEXT, COHERENT, DEVICECOHERENT, QUEUEFAMILYCOHERENT, WORKGROUPCOHERENT, SUBGROUPCOHERENT,
        NONPRIVATE, SHADERCALLCOHERENT, VOLATILE, RESTRICT, READONLY, WRITEONLY, SUBROUTINE,
        HIGH_PRECISION, MEDIUM_PRECISION, LOW_PRECISION, SMOOTH, FLAT, NOPERSPECTIVE, EXPLICITINTERPAMD,
        PERVERTEXNV, PERPRIMITIVENV, PERVIEWNV, PERTASKNV, INVARIANT, PRECISE, NONUNIFORM, SPIRV_STORAGE_CLASS,
        SPIRV_DECORATE, SPIRV_BY_REFERENCE, SPIRV_LITERAL
    )
}



