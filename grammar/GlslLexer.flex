package glsl;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import java.util.Set;
import java.util.HashSet;
import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static glsl.GlslTypes.*;

%%

%{
  public boolean afterType = false;
  public boolean afterTypeQualifier = false;
  public boolean afterDot = false;
  public boolean afterDefine = false;
  public Set<CharSequence> userTypesTable = new HashSet<>();

    public _GlslLexer() {
    this((java.io.Reader) null);
    }
    
    public void reset() {
      afterType = false;
      afterTypeQualifier = false;
      afterDot = false;
    }
  
    public void afterType() {
        afterType = true;
        afterTypeQualifier = false;
    }
    
    public void afterTypeQualifier() {
        afterTypeQualifier = true;
        afterType = false;
    }
  
%}

%public
%class _GlslLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%state IN_MULITLINE_COMMENT
%state PREPROCESSOR_IGNORE
%state PREPROCESSOR_DEFINE
%state PREPROCESSOR

WHITE_SPACE=[ \t\f]+
NEW_LINE=[\n\r]+
BACKSLASH="\\"{NEW_LINE}

IDENTIFIER=[a-zA-Z_]\w*
BOOLCONSTANT=false|true
STRING_LITERAL=(\"([^\"\\]|\\.)*\")
LINE_COMMENT="//"+.*
MULTILINE_COMMENT="/*"~"*/"|"/**"+"/"
PP_VERSION="#version"
PP_DEFINE="#define"
PP_UNDEF="#undef"
PP_IF="#if"
PP_IFDEF="#ifdef"
PP_IFNDEF="#ifndef"
PP_ELSE="#else"
PP_ELIF="#elif"
PP_ENDIF="#endif"
PP_ERROR="#error"
PP_PRAGMA="#pragma"
PP_EXTENSION="#extension"
PP_INCLUDE="#include"
PP_LINE="#line"
PP_TEXT=[^\\\n]+
MACRO_LINE="__LINE__"
MACRO_FILE="__FILE__"
MACRO_VERSION="__VERSION__"

DIGITS=\d+
HEXA_DIGIT=[\da-fA-F]
UNSIGNED="u"|"U"
HEXA_PREFIX="0"("x"|"X")
EXPONENT=("e"|"E")("+"|"-")?{DIGITS}
FLOATING_SUFFIX_FLOAT="f"|"F"
HEXA={HEXA_PREFIX}{HEXA_DIGIT}+
INTCONSTANT={DIGITS}|{HEXA}
UINTCONSTANT={INTCONSTANT}{UNSIGNED}
FRACTIONAL=(({DIGITS}"."{DIGITS})|({DIGITS}".")|("."{DIGITS})){EXPONENT}?
FRACTIONAL2={DIGITS}{EXPONENT}
FLOATCONSTANT=({FRACTIONAL}|{FRACTIONAL2}){FLOATING_SUFFIX_FLOAT}?

%%

<IN_MULITLINE_COMMENT> {
    "*/"                           { yybegin(YYINITIAL); return MULTILINE_COMMENT; }
    {NEW_LINE}                     { return MULTILINE_COMMENT; }
    .+                             { return MULTILINE_COMMENT; }
}

<PREPROCESSOR_IGNORE> {
    {NEW_LINE}                     { yybegin(YYINITIAL); return PP_END; }
    {BACKSLASH}                    { return BACKSLASH; }
    {WHITE_SPACE}                  { return WHITE_SPACE; }
    {PP_TEXT}                      { return PP_TEXT; }
}

<YYINITIAL, PREPROCESSOR, PREPROCESSOR_DEFINE> {
    {WHITE_SPACE}                  { return WHITE_SPACE; }
    {NEW_LINE} {
        if (zzLexicalState == PREPROCESSOR || zzLexicalState == PREPROCESSOR_DEFINE) {
            yybegin(YYINITIAL);
            return PP_END;
        }
        return WHITE_SPACE;
    }
    {BACKSLASH}                    { return BACKSLASH; }
    {MULTILINE_COMMENT}            { return MULTILINE_COMMENT; }
    {LINE_COMMENT}                 { return LINE_COMMENT; }
    // Preprocessors
    "#"                            { yybegin(PREPROCESSOR); return HASH; }
    {PP_VERSION}                   { yybegin(PREPROCESSOR); return PP_VERSION;}
    {PP_UNDEF}                     { yybegin(PREPROCESSOR); return PP_UNDEF;}
    {PP_IFDEF}                     { yybegin(PREPROCESSOR); return PP_IFDEF;}
    {PP_IFNDEF}                    { yybegin(PREPROCESSOR); return PP_IFNDEF;}
    {PP_ELSE}                      { yybegin(PREPROCESSOR); return PP_ELSE;}
    {PP_ENDIF}                     { yybegin(PREPROCESSOR); return PP_ENDIF;}
    {PP_INCLUDE}                   { yybegin(PREPROCESSOR); return PP_INCLUDE;}
    {PP_EXTENSION}                 { yybegin(PREPROCESSOR); return PP_EXTENSION;}
    {PP_LINE}                      { yybegin(PREPROCESSOR); return PP_LINE;}
    {MACRO_LINE}                   { yybegin(PREPROCESSOR); return MACRO_LINE;}
    {MACRO_FILE}                   { yybegin(PREPROCESSOR); return MACRO_FILE;}
    {MACRO_VERSION}                { yybegin(PREPROCESSOR); return MACRO_VERSION;}
    {PP_IF}                        { yybegin(PREPROCESSOR); return PP_IF;}
    {PP_ELIF}                      { yybegin(PREPROCESSOR); return PP_ELIF;}
    {PP_DEFINE}                    { afterDefine = true; yybegin(PREPROCESSOR_DEFINE); return PP_DEFINE;}
    {PP_ERROR}                     { yybegin(PREPROCESSOR_IGNORE); return PP_ERROR;}
    {PP_PRAGMA}                    { yybegin(PREPROCESSOR_IGNORE); return PP_PRAGMA;}
    // Punctuation
    ";"                            { reset(); return SEMICOLON; }
    "{"                            { reset(); return LEFT_BRACE; }
    "}"                            { reset(); return RIGHT_BRACE; }
    "("                            { reset(); return LEFT_PAREN; }
    ")"                            { reset(); return RIGHT_PAREN; }
    ","                            { reset(); return COMMA; }
    "+="                           { return ADD_ASSIGN; }
    "-="                           { return SUB_ASSIGN; }
    "*="                           { return MUL_ASSIGN; }
    "/="                           { return DIV_ASSIGN; }
    "%="                           { return MOD_ASSIGN; }
    ">>"                           { return RIGHT_OP; }
    "<<"                           { return LEFT_OP; }
    "&&"                           { return AND_OP; }
    "||"                           { return OR_OP; }
    "^^"                           { return XOR_OP; }
    ">>="                          { return RIGHT_ASSIGN; }
    "<<="                          { return LEFT_ASSIGN; }
    "&="                           { return AND_ASSIGN; }
    "|="                           { return OR_ASSIGN; }
    "^="                           { return XOR_ASSIGN; }
    ":"                            { return COLON; }
    "="                            { return EQUAL; }
    "."                            { afterDot = true; return DOT; }
    "!"                            { return BANG; }
    "-"                            { return DASH; }
    "~"                            { return TILDE; }
    "+"                            { return PLUS; }
    "*"                            { return STAR; }
    "/"                            { return SLASH; }
    "%"                            { return PERCENT; }
    "<"                            { return LEFT_ANGLE; }
    ">"                            { return RIGHT_ANGLE; }
    "|"                            { return VERTICAL_BAR; }
    "^"                            { return CARET; }
    "&"                            { return AMPERSAND; }
    "?"                            { return QUESTION; }
    "["                            { return LEFT_BRACKET; }
    "]"                            { return RIGHT_BRACKET; }
    "=="                           { return EQ_OP; }
    "!="                           { return GE_OP; }
    ">="                           { return NE_OP; }
    "<="                           { return LE_OP; }
    "--"                           { return DEC_OP; }
    "++"                           { return INC_OP; }
    // Control
    "if"                           { return IF; }
    "else"                         { return ELSE; }
    "switch"                       { return SWITCH; }
    "case"                         { return CASE; }
    "default"                      { return DEFAULT; }
    "while"                        { return WHILE; }
    "do"                           { return DO; }
    "for"                          { return FOR; }
    "continue"                     { return CONTINUE; }
    "break"                        { return BREAK; }
    "return"                       { return RETURN; }
    "discard"                      { return DISCARD; }
    // Types
    "void"                         { afterType(); return VOID; }
    "float"                        { afterType(); return FLOAT; }
    "double"                       { afterType(); return DOUBLE; }
    "int"                          { afterType(); return INT; }
    "uint"                         { afterType(); return UINT; }
    "bool"                         { afterType(); return BOOL; }
    "float16_t"                    { afterType(); return FLOAT16_T; }
    "float32_t"                    { afterType(); return FLOAT32_T; }
    "float64_t"                    { afterType(); return FLOAT64_T; }
    "int64_t"                      { afterType(); return INT64_T; }
    "uint64_t"                     { afterType(); return UINT64_T; }
    "int32_t"                      { afterType(); return INT32_T; }
    "uint32_t"                     { afterType(); return UINT32_T; }
    "int16_t"                      { afterType(); return INT16_T; }
    "uint16_t"                     { afterType(); return UINT16_T; }
    "int8_t"                       { afterType(); return INT8_T; }
    "uint8_t"                      { afterType(); return UINT8_T; }
    "vec2"                         { afterType(); return VEC2; }
    "vec3"                         { afterType(); return VEC3; }
    "vec4"                         { afterType(); return VEC4; }
    "dvec2"                        { afterType(); return DVEC2; }
    "dvec3"                        { afterType(); return DVEC3; }
    "dvec4"                        { afterType(); return DVEC4; }
    "bvec2"                        { afterType(); return BVEC2; }
    "bvec3"                        { afterType(); return BVEC3; }
    "bvec4"                        { afterType(); return BVEC4; }
    "ivec2"                        { afterType(); return IVEC2; }
    "ivec3"                        { afterType(); return IVEC3; }
    "ivec4"                        { afterType(); return IVEC4; }
    "uvec2"                        { afterType(); return UVEC2; }
    "uvec3"                        { afterType(); return UVEC3; }
    "uvec4"                        { afterType(); return UVEC4; }
    "mat2"                         { afterType(); return MAT2; }
    "mat3"                         { afterType(); return MAT3; }
    "mat4"                         { afterType(); return MAT4; }
    "mat2x2"                       { afterType(); return MAT2X2; }
    "mat2x3"                       { afterType(); return MAT2X3; }
    "mat2x4"                       { afterType(); return MAT2X4; }
    "mat3x2"                       { afterType(); return MAT3X2; }
    "mat3x3"                       { afterType(); return MAT3X3; }
    "mat3x4"                       { afterType(); return MAT3X4; }
    "mat4x2"                       { afterType(); return MAT4X2; }
    "mat4x3"                       { afterType(); return MAT4X3; }
    "mat4x4"                       { afterType(); return MAT4X4; }
    "dmat2"                        { afterType(); return DMAT2; }
    "dmat3"                        { afterType(); return DMAT3; }
    "dmat4"                        { afterType(); return DMAT4; }
    "dmat2x2"                      { afterType(); return DMAT2X2; }
    "dmat2x3"                      { afterType(); return DMAT2X3; }
    "dmat2x4"                      { afterType(); return DMAT2X4; }
    "dmat3x2"                      { afterType(); return DMAT3X2; }
    "dmat3x3"                      { afterType(); return DMAT3X3; }
    "dmat3x4"                      { afterType(); return DMAT3X4; }
    "dmat4x2"                      { afterType(); return DMAT4X2; }
    "dmat4x3"                      { afterType(); return DMAT4X3; }
    "dmat4x4"                      { afterType(); return DMAT4X4; }
    "atomic_uint"                  { afterType(); return ATOMIC_UINT; }
    "sampler2D"                    { afterType(); return SAMPLER2D; }
    "sampler3D"                    { afterType(); return SAMPLER3D; }
    "samplerCube"                  { afterType(); return SAMPLERCUBE; }
    "sampler2DShadow"              { afterType(); return SAMPLER2DSHADOW; }
    "samplerCubeShadow"            { afterType(); return SAMPLERCUBESHADOW; }
    "sampler2DArray"               { afterType(); return SAMPLER2DARRAY; }
    "sampler2DArrayShadow"         { afterType(); return SAMPLER2DARRAYSHADOW; }
    "samplerCubeArray"             { afterType(); return SAMPLERCUBEARRAY; }
    "samplerCubeArrayShadow"       { afterType(); return SAMPLERCUBEARRAYSHADOW; }
    "isampler2D"                   { afterType(); return ISAMPLER2D; }
    "isampler3D"                   { afterType(); return ISAMPLER3D; }
    "isamplerCube"                 { afterType(); return ISAMPLERCUBE; }
    "isampler2DArray"              { afterType(); return ISAMPLER2DARRAY; }
    "isamplerCubeArray"            { afterType(); return ISAMPLERCUBEARRAY; }
    "usampler2D"                   { afterType(); return USAMPLER2D; }
    "usampler3D"                   { afterType(); return USAMPLER3D; }
    "usamplerCube"                 { afterType(); return USAMPLERCUBE; }
    "usampler2DArray"              { afterType(); return USAMPLER2DARRAY; }
    "usamplerCubeArray"            { afterType(); return USAMPLERCUBEARRAY; }
    "sampler1D"                    { afterType(); return SAMPLER1D; }
    "sampler1DShadow"              { afterType(); return SAMPLER1DSHADOW; }
    "sampler1DArray"               { afterType(); return SAMPLER1DARRAY; }
    "sampler1DArrayShadow"         { afterType(); return SAMPLER1DARRAYSHADOW; }
    "isampler1D"                   { afterType(); return ISAMPLER1D; }
    "isampler1DArray"              { afterType(); return ISAMPLER1DARRAY; }
    "usampler1D"                   { afterType(); return USAMPLER1D; }
    "usampler1DArray"              { afterType(); return USAMPLER1DARRAY; }
    "sampler2DRect"                { afterType(); return SAMPLER2DRECT; }
    "sampler2DRectShadow"          { afterType(); return SAMPLER2DRECTSHADOW; }
    "isampler2DRect"               { afterType(); return ISAMPLER2DRECT; }
    "usampler2DRect"               { afterType(); return USAMPLER2DRECT; }
    "samplerBuffer"                { afterType(); return SAMPLERBUFFER; }
    "isamplerBuffer"               { afterType(); return ISAMPLERBUFFER; }
    "usamplerBuffer"               { afterType(); return USAMPLERBUFFER; }
    "sampler2DMS"                  { afterType(); return SAMPLER2DMS; }
    "isampler2DMS"                 { afterType(); return ISAMPLER2DMS; }
    "usampler2DMS"                 { afterType(); return USAMPLER2DMS; }
    "sampler2DMSArray"             { afterType(); return SAMPLER2DMSARRAY; }
    "isampler2DMSArray"            { afterType(); return ISAMPLER2DMSARRAY; }
    "usampler2DMSArray"            { afterType(); return USAMPLER2DMSARRAY; }
    "image2D"                      { afterType(); return IMAGE2D; }
    "iimage2D"                     { afterType(); return IIMAGE2D; }
    "uimage2D"                     { afterType(); return UIMAGE2D; }
    "image3D"                      { afterType(); return IMAGE3D; }
    "iimage3D"                     { afterType(); return IIMAGE3D; }
    "uimage3D"                     { afterType(); return UIMAGE3D; }
    "imageCube"                    { afterType(); return IMAGECUBE; }
    "iimageCube"                   { afterType(); return IIMAGECUBE; }
    "uimageCube"                   { afterType(); return UIMAGECUBE; }
    "imageBuffer"                  { afterType(); return IMAGEBUFFER; }
    "iimageBuffer"                 { afterType(); return IIMAGEBUFFER; }
    "uimageBuffer"                 { afterType(); return UIMAGEBUFFER; }
    "image1D"                      { afterType(); return IMAGE1D; }
    "iimage1D"                     { afterType(); return IIMAGE1D; }
    "uimage1D"                     { afterType(); return UIMAGE1D; }
    "image1DArray"                 { afterType(); return IMAGE1DARRAY; }
    "iimage1DArray"                { afterType(); return IIMAGE1DARRAY; }
    "uimage1DArray"                { afterType(); return UIMAGE1DARRAY; }
    "image2DRect"                  { afterType(); return IMAGE2DRECT; }
    "iimage2DRect"                 { afterType(); return IIMAGE2DRECT; }
    "uimage2DRect"                 { afterType(); return UIMAGE2DRECT; }
    "image2DArray"                 { afterType(); return IMAGE2DARRAY; }
    "iimage2DArray"                { afterType(); return IIMAGE2DARRAY; }
    "uimage2DArray"                { afterType(); return UIMAGE2DARRAY; }
    "imageCubeArray"               { afterType(); return IMAGECUBEARRAY; }
    "iimageCubeArray"              { afterType(); return IIMAGECUBEARRAY; }
    "uimageCubeArray"              { afterType(); return UIMAGECUBEARRAY; }
    "image2DMS"                    { afterType(); return IMAGE2DMS; }
    "iimage2DMS"                   { afterType(); return IIMAGE2DMS; }
    "uimage2DMS"                   { afterType(); return UIMAGE2DMS; }
    "image2DMSArray"               { afterType(); return IMAGE2DMSARRAY; }
    "iimage2DMSArray"              { afterType(); return IIMAGE2DMSARRAY; }
    "uimage2DMSArray"              { afterType(); return UIMAGE2DMSARRAY; }
    // storage_qualifier
    "const"                        { afterTypeQualifier(); return CONST; }
    "inout"                        { afterTypeQualifier(); return INOUT; }
    "in"                           { afterTypeQualifier(); return IN; }
    "out"                          { afterTypeQualifier(); return OUT; }
    "centroid"                     { afterTypeQualifier(); return CENTROID; }
    "patch"                        { afterTypeQualifier(); return PATCH; }
    "sample"                       { afterTypeQualifier(); return SAMPLE; }
    "uniform"                      { afterTypeQualifier(); return UNIFORM; }
    "shared"                       { afterTypeQualifier(); return SHARED; }
    "buffer"                       { afterTypeQualifier(); return BUFFER; }
    "varying"                      { afterTypeQualifier(); return VARYING; }
    "coherent"                     { afterTypeQualifier(); return COHERENT; }
    "volatile"                     { afterTypeQualifier(); return VOLATILE; }
    "restrict"                     { afterTypeQualifier(); return RESTRICT; }
    "readonly"                     { afterTypeQualifier(); return READONLY; }
    "writeonly"                    { afterTypeQualifier(); return WRITEONLY; }
    "subroutine"                   { afterTypeQualifier(); return SUBROUTINE; }
    "nonprivate"                   { afterTypeQualifier(); return NONPRIVATE; }
    "attr"                         { afterTypeQualifier(); return ATTR; }
    "hitattrnv"                    { afterTypeQualifier(); return HITATTRNV; }
    "hitattrext"                   { afterTypeQualifier(); return HITATTREXT; }
    "payloadnv"                    { afterTypeQualifier(); return PAYLOADNV; }
    "payloadext"                   { afterTypeQualifier(); return PAYLOADEXT; }
    "payloadinnv"                  { afterTypeQualifier(); return PAYLOADINNV; }
    "payloadinext"                 { afterTypeQualifier(); return PAYLOADINEXT; }
    "calldatanv"                   { afterTypeQualifier(); return CALLDATANV; }
    "calldataext"                  { afterTypeQualifier(); return CALLDATAEXT; }
    "calldatainnv"                 { afterTypeQualifier(); return CALLDATAINNV; }
    "calldatainext"                { afterTypeQualifier(); return CALLDATAINEXT; }
    "devicecoherent"               { afterTypeQualifier(); return DEVICECOHERENT; }
    "queuefamilycoherent"          { afterTypeQualifier(); return QUEUEFAMILYCOHERENT; }
    "workgroupcoherent"            { afterTypeQualifier(); return WORKGROUPCOHERENT; }
    "subgroupcoherent"             { afterTypeQualifier(); return SUBGROUPCOHERENT; }
    "shadercallcoherent"           { afterTypeQualifier(); return SHADERCALLCOHERENT; }
    // precision_qualifier
    "highp"                        { afterTypeQualifier(); return HIGH_PRECISION; }
    "mediump"                      { afterTypeQualifier(); return MEDIUM_PRECISION; }
    "lowp"                         { afterTypeQualifier(); return LOW_PRECISION; }
    // interpolation_qualifier
    "smooth"                       { afterTypeQualifier(); return SMOOTH; }
    "flat"                         { afterTypeQualifier(); return FLAT; }
    "noperspective"                { afterTypeQualifier(); return NOPERSPECTIVE; }
    "__explicitInterpAMD"          { afterTypeQualifier(); return EXPLICITINTERPAMD; }
    "pervertexnv"                  { afterTypeQualifier(); return PERVERTEXNV; }
    "perprimitivenv"               { afterTypeQualifier(); return PERPRIMITIVENV; }
    "perviewnv"                    { afterTypeQualifier(); return PERVIEWNV; }
    "pertasknv"                    { afterTypeQualifier(); return PERTASKNV; }
    // Rest
    "struct"                       { afterTypeQualifier(); return STRUCT; }
    "layout"                       { return LAYOUT; }
    "precision"                    { return PRECISION; }
    "demote"                       { return DEMOTE; }
    "invariant"                    { return INVARIANT; }
    "precise"                      { return PRECISE; }
    "nonuniform"                   { return NONUNIFORM; }
    "terminateInvocation"          { return TERMINATE_INVOCATION; }
    "terminateRayEXT"              { return TERMINATE_RAY; }
    "ignoreIntersectionEXT"        { return IGNORE_INTERSECTION; }
    // sprirv
    "spirv_execution_mode"         { return SPIRV_EXECUTION_MODE; }
    "spirv_execution_mode_id"      { return SPIRV_EXECUTION_MODE_ID; }
    "spirv_storage_class"          { return SPIRV_STORAGE_CLASS; }
    "spirv_decorate"               { return SPIRV_DECORATE; }
    "spirv_decorate_id"            { return SPIRV_DECORATE_ID; }
    "spirv_decorate_string"        { return SPIRV_DECORATE_STRING; }
    "spirv_type"                   { return SPIRV_TYPE; }
    "spirv_instruction"            { return SPIRV_INSTRUCTION; }
    "spirv_by_reference"           { return SPIRV_BY_REFERENCE; }
    "spirv_literal"                { return SPIRV_LITERAL; }
    // User-defined
    {FLOATCONSTANT}                { return FLOATCONSTANT; }
    {INTCONSTANT}                  { return INTCONSTANT; }
    {UINTCONSTANT}                 { return UINTCONSTANT; }
    {BOOLCONSTANT}                 { return BOOLCONSTANT; }
    {STRING_LITERAL}               { return STRING_LITERAL; }
    {IDENTIFIER}                   { return IDENTIFIER; }
}
[^]                               { return BAD_CHARACTER; }
