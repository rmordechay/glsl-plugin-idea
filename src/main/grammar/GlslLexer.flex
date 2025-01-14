package glsl;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static glsl.GlslTypes.*;
import java.util.ArrayList;
import java.util.List;

%%

%{
  public boolean afterStruct;
  public boolean afterType;
  public List<String> userDefinedTypes = new ArrayList<>();
  public _GlslLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _GlslLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%state MULITLINE_COMMENT_STATE
%state MACRO_IDENTIFIER_STATE
%state MACRO_FUNC_DEFINITION_STATE
%state MACRO_BODY_STATE
%state MACRO_INCLUDE_STATE
%state MACRO_IGNORE_STATE

WHITE_SPACE=[ \t\f]+
NEW_LINE=[\n\r]
BACKSLASH=\\{WHITE_SPACE}*{NEW_LINE}
LINE_COMMENT="//"+.*

DIGITS=\d+
HEXA_DIGIT=[\da-fA-F]
UNSIGNED="u"|"U"
HEXA_PREFIX="0"("x"|"X")
EXPONENT=("e"|"E")("+"|"-")?{DIGITS}
FLOATING_SUFFIX_FLOAT="f"|"F"
FLOATING_SUFFIX_DOUBLE="lf"|"LF"

HEXA={HEXA_PREFIX}{HEXA_DIGIT}+
INTCONSTANT={DIGITS}|{HEXA}
UINTCONSTANT={INTCONSTANT}{UNSIGNED}

FRACTIONAL=(({DIGITS}"."{DIGITS})|({DIGITS}".")|("."{DIGITS})){EXPONENT}?
FRACTIONAL2={DIGITS}{EXPONENT}
FLOATCONSTANT=({FRACTIONAL}|{FRACTIONAL2}){FLOATING_SUFFIX_FLOAT}?
DOUBLECONSTANT=({FRACTIONAL}|{FRACTIONAL2}){FLOATING_SUFFIX_DOUBLE}?


BOOLCONSTANT=false|true
STRING_LITERAL=(\"([^\"\\]|\\.)*\")
IDENTIFIER=[a-zA-Z_]+\w*
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
MACRO_LINE="__LINE__"
MACRO_FILE="__FILE__"
MACRO_VERSION="__VERSION__"
INCLUDE_PATH={IDENTIFIER}([\s\/]*{IDENTIFIER}\s*)*(\.{IDENTIFIER})?


%%

"IntellijIdeaRulezzz"    { return INTELLIJ_COMPLETION_DUMMY; }

<MULITLINE_COMMENT_STATE> {
  "*/"                             { yybegin(YYINITIAL); return MULTILINE_COMMENT; }
  [^*\n]+                          { return MULTILINE_COMMENT; }
  "*"                              { return MULTILINE_COMMENT; }
  {NEW_LINE}                       { return MULTILINE_COMMENT; }
}

<MACRO_INCLUDE_STATE> {
  {WHITE_SPACE}                    { return WHITE_SPACE; }
  {NEW_LINE}                       { yybegin(YYINITIAL); return END_INCLUDE; }
  "<"                              { return LEFT_ANGLE; }
  ">"                              { return RIGHT_ANGLE; }
  {STRING_LITERAL}                 { return STRING_LITERAL; }
  {INCLUDE_PATH}                   { return INCLUDE_PATH; }
}

<MACRO_IDENTIFIER_STATE> {
  {WHITE_SPACE}                    { return WHITE_SPACE; }
  {BACKSLASH}                      { return WHITE_SPACE; }
  {NEW_LINE}                       { yybegin(YYINITIAL); return PP_END; }
  {IDENTIFIER}                     { return IDENTIFIER; }
}

<MACRO_FUNC_DEFINITION_STATE> {
  {WHITE_SPACE}                    { return WHITE_SPACE; }
  {IDENTIFIER}                     { return MACRO_FUNC_PARAM; }
  "("                              { return LEFT_PAREN; }
  ")"                              { yybegin(MACRO_BODY_STATE); return RIGHT_PAREN_MACRO; }
  ","                              { return COMMA; }
}

<YYINITIAL, MACRO_BODY_STATE, MACRO_IGNORE_STATE> {
  {WHITE_SPACE}                    { return WHITE_SPACE; }
  {NEW_LINE}                       {
                                         if (yystate() == MACRO_BODY_STATE) {
                                            return PP_END;
                                         } else if (yystate() == MACRO_IGNORE_STATE) {
                                             yybegin(YYINITIAL);
                                             return PP_END;
                                         }
                                         return WHITE_SPACE;
                                       }
  {BACKSLASH}                      { return WHITE_SPACE; }
  "/*"                             { yybegin(MULITLINE_COMMENT_STATE);  return MULTILINE_COMMENT; }
  {LINE_COMMENT}                   { return LINE_COMMENT; }
  {PP_VERSION}                     { yybegin(MACRO_IGNORE_STATE); return PP_VERSION;}
  {PP_UNDEF}                       { yybegin(MACRO_IGNORE_STATE); return PP_UNDEF;}
  {PP_IFDEF}                       { yybegin(MACRO_IGNORE_STATE); return PP_IFDEF;}
  {PP_IFNDEF}                      { yybegin(MACRO_IGNORE_STATE); return PP_IFNDEF;}
  {PP_ELSE}                        { yybegin(MACRO_IGNORE_STATE); return PP_ELSE;}
  {PP_ENDIF}                       { yybegin(MACRO_IGNORE_STATE); return PP_ENDIF;}
  {PP_EXTENSION}                   { yybegin(MACRO_IGNORE_STATE); return PP_EXTENSION;}
  {PP_LINE}                        { yybegin(MACRO_IGNORE_STATE); return PP_LINE;}
  {MACRO_LINE}                     { yybegin(MACRO_IGNORE_STATE); return MACRO_LINE;}
  {MACRO_FILE}                     { yybegin(MACRO_IGNORE_STATE); return MACRO_FILE;}
  {MACRO_VERSION}                  { yybegin(MACRO_IGNORE_STATE); return MACRO_VERSION;}
  {PP_IF}                          { yybegin(MACRO_IGNORE_STATE); return PP_IF;}
  {PP_ELIF}                        { yybegin(MACRO_IGNORE_STATE); return PP_ELIF;}
  {PP_ERROR}                       { yybegin(MACRO_IGNORE_STATE); return PP_ERROR;}
  {PP_PRAGMA}                      { yybegin(MACRO_IGNORE_STATE); return PP_PRAGMA;}
  {PP_INCLUDE}                     { yybegin(MACRO_INCLUDE_STATE); return PP_INCLUDE;}
  {PP_DEFINE}                      { yybegin(MACRO_IDENTIFIER_STATE); return PP_DEFINE;}

  ";"                              { afterType = false; return SEMICOLON; }
  ","                              { afterType = false; return COMMA; }
  "="                              { afterType = false; return EQUAL; }
  "("                              { afterType = false; return LEFT_PAREN; }
  ")"                              { afterType = false; return RIGHT_PAREN; }
  "{"                              { afterStruct = false; return LEFT_BRACE; }
  "}"                              { return RIGHT_BRACE; }
  "["                              { return LEFT_BRACKET; }
  "]"                              { return RIGHT_BRACKET; }
  "#"                              { return HASH; }
  ":"                              { return COLON; }
  "."                              { return DOT; }
  "!"                              { return BANG; }
  "-"                              { return DASH; }
  "~"                              { return TILDE; }
  "+"                              { return PLUS; }
  "*"                              { return STAR; }
  "/"                              { return SLASH; }
  "%"                              { return PERCENT; }
  "<"                              { return LEFT_ANGLE; }
  ">"                              { return RIGHT_ANGLE; }
  "|"                              { return VERTICAL_BAR; }
  "^"                              { return CARET; }
  "&"                              { return AMPERSAND; }
  "?"                              { return QUESTION; }
  "+="                             { return ADD_ASSIGN; }
  "-="                             { return SUB_ASSIGN; }
  "*="                             { return MUL_ASSIGN; }
  "/="                             { return DIV_ASSIGN; }
  "%="                             { return MOD_ASSIGN; }
  ">>"                             { return RIGHT_OP; }
  "<<"                             { return LEFT_OP; }
  "&&"                             { return AND_OP; }
  "||"                             { return OR_OP; }
  "^^"                             { return XOR_OP; }
  ">>="                            { return RIGHT_ASSIGN; }
  "<<="                            { return LEFT_ASSIGN; }
  "&="                             { return AND_ASSIGN; }
  "|="                             { return OR_ASSIGN; }
  "^="                             { return XOR_ASSIGN; }
  "=="                             { return EQ_OP; }
  "!="                             { return GE_OP; }
  ">="                             { return NE_OP; }
  "<="                             { return LE_OP; }
  "--"                             { return DEC_OP; }
  "++"                             { return INC_OP; }
  "const"                          { return CONST; }
  "uniform"                        { return UNIFORM; }
  "buffer"                         { return BUFFER; }
  "in"                             { return IN; }
  "out"                            { return OUT; }
  "smooth"                         { return SMOOTH; }
  "flat"                           { return FLAT; }
  "centroid"                       { return CENTROID; }
  "invariant"                      { return INVARIANT; }
  "inout"                          { return INOUT; }
  "struct"                         { afterStruct = true; return STRUCT; }
  "break"                          { return BREAK; }
  "continue"                       { return CONTINUE; }
  "do"                             { return DO; }
  "for"                            { return FOR; }
  "while"                          { return WHILE; }
  "switch"                         { return SWITCH; }
  "case"                           { return CASE; }
  "default"                        { return DEFAULT; }
  "if"                             { return IF; }
  "else"                           { return ELSE; }
  "discard"                        { return DISCARD; }
  "terminateInvocation"            { return TERMINATE_INVOCATION; }
  "terminateRayEXT"                { return TERMINATE_RAY; }
  "ignoreIntersectionEXT"          { return IGNORE_INTERSECTION; }
  "__explicitInterpAMD"            { return EXPLICITINTERPAMD; }
  "pervertexNV"                    { return PERVERTEXNV; }
  "precise"                        { return PRECISE; }
  "return"                         { return RETURN; }
  "layout"                         { return LAYOUT; }
  "shared"                         { return SHARED; }
  "highp"                          { return HIGH_PRECISION; }
  "mediump"                        { return MEDIUM_PRECISION; }
  "lowp"                           { return LOW_PRECISION; }
  "precision"                      { return PRECISION; }
  "nonuniformEXT"                  { return NONUNIFORM; }
  "demote"                         { return DEMOTE; }
  "attribute"                      { return ATTR; }
  "varying"                        { return VARYING; }
  "noperspective"                  { return NOPERSPECTIVE; }
  "coherent"                       { return COHERENT; }
  "devicecoherent"                 { return DEVICECOHERENT; }
  "queuefamilycoherent"            { return QUEUEFAMILYCOHERENT; }
  "workgroupcoherent"              { return WORKGROUPCOHERENT; }
  "subgroupcoherent"               { return SUBGROUPCOHERENT; }
  "shadercallcoherent"             { return SHADERCALLCOHERENT; }
  "nonprivate"                     { return NONPRIVATE; }
  "restrict"                       { return RESTRICT; }
  "readonly"                       { return READONLY; }
  "writeonly"                      { return WRITEONLY; }
  "atomic_uint"                    { return ATOMIC_UINT; }
  "volatile"                       { return VOLATILE; }
  "patch"                          { return PATCH; }
  "sample"                         { return SAMPLE; }
  "subroutine"                     { return SUBROUTINE; }
  "callableDataNV"                 { return CALLDATANV; }
  "callableDataEXT"                { return CALLDATAEXT; }
  "callableDataInNV"               { return CALLDATAINNV; }
  "callableDataInEXT"              { return CALLDATAINEXT; }
  "spirv_instruction"              { return SPIRV_INSTRUCTION; }
  "spirv_execution_mode"           { return SPIRV_EXECUTION_MODE; }
  "spirv_execution_mode_id"        { return SPIRV_EXECUTION_MODE_ID; }
  "spirv_decorate"                 { return SPIRV_DECORATE; }
  "spirv_decorate_id"              { return SPIRV_DECORATE_ID; }
  "spirv_decorate_string"          { return SPIRV_DECORATE_STRING; }
  "spirv_type"                     { return SPIRV_TYPE; }
  "spirv_storage_class"            { return SPIRV_STORAGE_CLASS; }
  "spirv_by_reference"             { return SPIRV_BY_REFERENCE; }
  "spirv_literal"                  { return SPIRV_LITERAL; }
  "rayPayloadNV"                   { return PAYLOADNV; }
  "rayPayloadEXT"                  { return PAYLOADEXT; }
  "rayPayloadInNV"                 { return PAYLOADINNV; }
  "rayPayloadInEXT"                { return PAYLOADINEXT; }
  "hitAttributeNV"                 { return HITATTRNV; }
  "hitAttributeEXT"                { return HITATTREXT; }
  // Scalars
  "void"                           { afterType = true; return VOID; }
  "bool"                           { afterType = true; return BOOL; }
  "float"                          { afterType = true; return FLOAT; }
  "double"                         { afterType = true; return DOUBLE; }
  "int"                            { afterType = true; return INT; }
  "uint"                           { afterType = true; return UINT; }
  "int64_t"                        { afterType = true; return INT64_T; }
  "uint64_t"                       { afterType = true; return UINT64_T; }
  "int8_t"                         { afterType = true; return INT8_T; }
  "uint8_t"                        { afterType = true; return UINT8_T; }
  "int16_t"                        { afterType = true; return INT16_T; }
  "uint16_t"                       { afterType = true; return UINT16_T; }
  "int32_t"                        { afterType = true; return INT32_T; }
  "uint32_t"                       { afterType = true; return UINT32_T; }
  "float16_t"                      { afterType = true; return FLOAT16_T; }
  "float32_t"                      { afterType = true; return FLOAT32_T; }
  "float64_t"                      { afterType = true; return FLOAT64_T; }
  // Vectors
  "vec2"                           { afterType = true; return VEC2; }
  "vec3"                           { afterType = true; return VEC3; }
  "vec4"                           { afterType = true; return VEC4; }
  "bvec2"                          { afterType = true; return BVEC2; }
  "bvec3"                          { afterType = true; return BVEC3; }
  "bvec4"                          { afterType = true; return BVEC4; }
  "ivec2"                          { afterType = true; return IVEC2; }
  "ivec3"                          { afterType = true; return IVEC3; }
  "ivec4"                          { afterType = true; return IVEC4; }
  "uvec2"                          { afterType = true; return UVEC2; }
  "uvec3"                          { afterType = true; return UVEC3; }
  "uvec4"                          { afterType = true; return UVEC4; }
  "dvec2"                          { afterType = true; return DVEC2; }
  "dvec3"                          { afterType = true; return DVEC3; }
  "dvec4"                          { afterType = true; return DVEC4; }
  "i64vec2"                        { afterType = true; return I64VEC2; }
  "i64vec3"                        { afterType = true; return I64VEC3; }
  "i64vec4"                        { afterType = true; return I64VEC4; }
  "u64vec2"                        { afterType = true; return U64VEC2; }
  "u64vec3"                        { afterType = true; return U64VEC3; }
  "u64vec4"                        { afterType = true; return U64VEC4; }
  "i8vec2"                         { afterType = true; return I8VEC2; }
  "i8vec3"                         { afterType = true; return I8VEC3; }
  "i8vec4"                         { afterType = true; return I8VEC4; }
  "u8vec2"                         { afterType = true; return U8VEC2; }
  "u8vec3"                         { afterType = true; return U8VEC3; }
  "u8vec4"                         { afterType = true; return U8VEC4; }
  "i16vec2"                        { afterType = true; return I16VEC2; }
  "i16vec3"                        { afterType = true; return I16VEC3; }
  "i16vec4"                        { afterType = true; return I16VEC4; }
  "u16vec2"                        { afterType = true; return U16VEC2; }
  "u16vec3"                        { afterType = true;  return U16VEC3; }
  "u16vec4"                        { afterType = true;  return U16VEC4; }
  "i32vec2"                        { afterType = true;  return I32VEC2; }
  "i32vec3"                        { afterType = true;  return I32VEC3; }
  "i32vec4"                        { afterType = true;  return I32VEC4; }
  "u32vec2"                        { afterType = true;  return U32VEC2; }
  "u32vec3"                        { afterType = true;  return U32VEC3; }
  "u32vec4"                        { afterType = true;  return U32VEC4; }
  "f16vec2"                        { afterType = true;  return F16VEC2; }
  "f16vec3"                        { afterType = true;  return F16VEC3; }
  "f16vec4"                        { afterType = true;  return F16VEC4; }
  "f32vec2"                        { afterType = true;  return F32VEC2; }
  "f32vec3"                        { afterType = true;  return F32VEC3; }
  "f32vec4"                        { afterType = true;  return F32VEC4; }
  "f64vec2"                        { afterType = true;  return F64VEC2; }
  "f64vec3"                        { afterType = true;  return F64VEC3; }
  "f64vec4"                        { afterType = true;  return F64VEC4; }
  // Metrices
  "mat2"                           { afterType = true;  return MAT2; }
  "mat3"                           { afterType = true;  return MAT3; }
  "mat4"                           { afterType = true;  return MAT4; }
  "mat2x2"                         { afterType = true;  return MAT2X2; }
  "mat2x3"                         { afterType = true;  return MAT2X3; }
  "mat2x4"                         { afterType = true;  return MAT2X4; }
  "mat3x2"                         { afterType = true;  return MAT3X2; }
  "mat3x3"                         { afterType = true;  return MAT3X3; }
  "mat3x4"                         { afterType = true;  return MAT3X4; }
  "mat4x2"                         { afterType = true;  return MAT4X2; }
  "mat4x3"                         { afterType = true;  return MAT4X3; }
  "mat4x4"                         { afterType = true;  return MAT4X4; }
  "dmat2"                          { afterType = true;  return DMAT2; }
  "dmat3"                          { afterType = true;  return DMAT3; }
  "dmat4"                          { afterType = true;  return DMAT4; }
  "dmat2x2"                        { afterType = true;  return DMAT2X2; }
  "dmat2x3"                        { afterType = true;  return DMAT2X3; }
  "dmat2x4"                        { afterType = true;  return DMAT2X4; }
  "dmat3x2"                        { afterType = true;  return DMAT3X2; }
  "dmat3x3"                        { afterType = true;  return DMAT3X3; }
  "dmat3x4"                        { afterType = true;  return DMAT3X4; }
  "dmat4x2"                        { afterType = true;  return DMAT4X2; }
  "dmat4x3"                        { afterType = true;  return DMAT4X3; }
  "dmat4x4"                        { afterType = true;  return DMAT4X4; }
  "f16mat2x2"                      { afterType = true;  return F16MAT2X2; }
  "f16mat2x3"                      { afterType = true;  return F16MAT2X3; }
  "f16mat2x4"                      { afterType = true;  return F16MAT2X4; }
  "f16mat3x2"                      { afterType = true;  return F16MAT3X2; }
  "f16mat3x3"                      { afterType = true;  return F16MAT3X3; }
  "f16mat3x4"                      { afterType = true;  return F16MAT3X4; }
  "f16mat4x2"                      { afterType = true;  return F16MAT4X2; }
  "f16mat4x3"                      { afterType = true;  return F16MAT4X3; }
  "f16mat4x4"                      { afterType = true; return F16MAT4X4; }
  "f16mat2"                        { afterType = true; return F16MAT2; }
  "f16mat3"                        { afterType = true; return F16MAT3; }
  "f16mat4"                        { afterType = true; return F16MAT4; }
  "f32mat2"                        { afterType = true; return F32MAT2; }
  "f32mat3"                        { afterType = true; return F32MAT3; }
  "f32mat4"                        { afterType = true; return F32MAT4; }
  "f32mat2x2"                      { afterType = true; return F32MAT2X2; }
  "f32mat2x3"                      { afterType = true; return F32MAT2X3; }
  "f32mat2x4"                      { afterType = true; return F32MAT2X4; }
  "f32mat3x2"                      { afterType = true; return F32MAT3X2; }
  "f32mat3x3"                      { afterType = true; return F32MAT3X3; }
  "f32mat3x4"                      { afterType = true; return F32MAT3X4; }
  "f32mat4x2"                      { afterType = true; return F32MAT4X2; }
  "f32mat4x3"                      { afterType = true; return F32MAT4X3; }
  "f32mat4x4"                      { afterType = true; return F32MAT4X4; }
  "f64mat2"                        { afterType = true; return F64MAT2; }
  "f64mat3"                        { afterType = true; return F64MAT3; }
  "f64mat4"                        { afterType = true; return F64MAT4; }
  "f64mat2x2"                      { afterType = true; return F64MAT2X2; }
  "f64mat2x3"                      { afterType = true; return F64MAT2X3; }
  "f64mat2x4"                      { afterType = true; return F64MAT2X4; }
  "f64mat3x2"                      { afterType = true; return F64MAT3X2; }
  "f64mat3x3"                      { afterType = true; return F64MAT3X3; }
  "f64mat3x4"                      { afterType = true; return F64MAT3X4; }
  "f64mat4x2"                      { afterType = true; return F64MAT4X2; }
  "f64mat4x3"                      { afterType = true; return F64MAT4X3; }
  "f64mat4x4"                      { afterType = true; return F64MAT4X4; }
  // Images
  "image1D"                        { afterType = true; return IMAGE1D; }
  "iimage1D"                       { afterType = true; return IIMAGE1D; }
  "uimage1D"                       { afterType = true; return UIMAGE1D; }
  "image2D"                        { afterType = true; return IMAGE2D; }
  "iimage2D"                       { afterType = true; return IIMAGE2D; }
  "uimage2D"                       { afterType = true; return UIMAGE2D; }
  "image3D"                        { afterType = true; return IMAGE3D; }
  "iimage3D"                       { afterType = true; return IIMAGE3D; }
  "uimage3D"                       { afterType = true; return UIMAGE3D; }
  "image2DRect"                    { afterType = true; return IMAGE2DRECT; }
  "iimage2DRect"                   { afterType = true; return IIMAGE2DRECT; }
  "uimage2DRect"                   { afterType = true; return UIMAGE2DRECT; }
  "imageCube"                      { afterType = true; return IMAGECUBE; }
  "iimageCube"                     { afterType = true; return IIMAGECUBE; }
  "uimageCube"                     { afterType = true; return UIMAGECUBE; }
  "imageBuffer"                    { afterType = true; return IMAGEBUFFER; }
  "iimageBuffer"                   { afterType = true; return IIMAGEBUFFER; }
  "uimageBuffer"                   { afterType = true; return UIMAGEBUFFER; }
  "image1DArray"                   { afterType = true; return IMAGE1DARRAY; }
  "iimage1DArray"                  { afterType = true; return IIMAGE1DARRAY; }
  "uimage1DArray"                  { afterType = true; return UIMAGE1DARRAY; }
  "image2DArray"                   { afterType = true; return IMAGE2DARRAY; }
  "iimage2DArray"                  { afterType = true; return IIMAGE2DARRAY; }
  "uimage2DArray"                  { afterType = true; return UIMAGE2DARRAY; }
  "imageCubeArray"                 { afterType = true; return IMAGECUBEARRAY; }
  "iimageCubeArray"                { afterType = true; return IIMAGECUBEARRAY; }
  "uimageCubeArray"                { afterType = true; return UIMAGECUBEARRAY; }
  "image2DMS"                      { afterType = true; return IMAGE2DMS; }
  "iimage2DMS"                     { afterType = true; return IIMAGE2DMS; }
  "uimage2DMS"                     { afterType = true; return UIMAGE2DMS; }
  "image2DMSArray"                 { afterType = true; return IMAGE2DMSARRAY; }
  "iimage2DMSArray"                { afterType = true; return IIMAGE2DMSARRAY; }
  "uimage2DMSArray"                { afterType = true; return UIMAGE2DMSARRAY; }
  "f16image1D"                     { afterType = true; return F16IMAGE1D; }
  "f16image2D"                     { afterType = true; return F16IMAGE2D; }
  "f16image3D"                     { afterType = true; return F16IMAGE3D; }
  "f16image2DRect"                 { afterType = true; return F16IMAGE2DRECT; }
  "f16imageCube"                   { afterType = true; return F16IMAGECUBE; }
  "f16image1DArray"                { afterType = true; return F16IMAGE1DARRAY; }
  "f16image2DArray"                { afterType = true; return F16IMAGE2DARRAY; }
  "f16imageCubeArray"              { afterType = true; return F16IMAGECUBEARRAY; }
  "f16imageBuffer"                 { afterType = true; return F16IMAGEBUFFER; }
  "f16image2DMS"                   { afterType = true; return F16IMAGE2DMS; }
  "f16image2DMSArray"              { afterType = true; return F16IMAGE2DMSARRAY; }
  "i64image1D"                     { afterType = true; return I64IMAGE1D; }
  "u64image1D"                     { afterType = true; return U64IMAGE1D; }
  "i64image2D"                     { afterType = true; return I64IMAGE2D; }
  "u64image2D"                     { afterType = true; return U64IMAGE2D; }
  "i64image3D"                     { afterType = true; return I64IMAGE3D; }
  "u64image3D"                     { afterType = true; return U64IMAGE3D; }
  "i64image2DRect"                 { afterType = true; return I64IMAGE2DRECT; }
  "u64image2DRect"                 { afterType = true; return U64IMAGE2DRECT; }
  "i64imageCube"                   { afterType = true; return I64IMAGECUBE; }
  "u64imageCube"                   { afterType = true; return U64IMAGECUBE; }
  "i64imageBuffer"                 { afterType = true; return I64IMAGEBUFFER; }
  "u64imageBuffer"                 { afterType = true; return U64IMAGEBUFFER; }
  "i64image1DArray"                { afterType = true; return I64IMAGE1DARRAY; }
  "u64image1DArray"                { afterType = true; return U64IMAGE1DARRAY; }
  "i64image2DArray"                { afterType = true; return I64IMAGE2DARRAY; }
  "u64image2DArray"                { afterType = true; return U64IMAGE2DARRAY; }
  "i64imageCubeArray"              { afterType = true; return I64IMAGECUBEARRAY; }
  "u64imageCubeArray"              { afterType = true; return U64IMAGECUBEARRAY; }
  "i64image2DMS"                   { afterType = true; return I64IMAGE2DMS; }
  "u64image2DMS"                   { afterType = true; return U64IMAGE2DMS; }
  "i64image2DMSArray"              { afterType = true; return I64IMAGE2DMSARRAY; }
  "u64image2DMSArray"              { afterType = true; return U64IMAGE2DMSARRAY; }
  // Samplars
  "sampler2D"                      { afterType = true; return SAMPLER2D; }
  "samplerCube"                    { afterType = true; return SAMPLERCUBE; }
  "samplerCubeShadow"              { afterType = true; return SAMPLERCUBESHADOW; }
  "sampler2DArray"                 { afterType = true; return SAMPLER2DARRAY; }
  "sampler2DArrayShadow"           { afterType = true; return SAMPLER2DARRAYSHADOW; }
  "isampler2D"                     { afterType = true; return ISAMPLER2D; }
  "isampler3D"                     { afterType = true; return ISAMPLER3D; }
  "isamplerCube"                   { afterType = true; return ISAMPLERCUBE; }
  "isampler2DArray"                { afterType = true; return ISAMPLER2DARRAY; }
  "usampler2D"                     { afterType = true; return USAMPLER2D; }
  "usampler3D"                     { afterType = true; return USAMPLER3D; }
  "usamplerCube"                   { afterType = true; return USAMPLERCUBE; }
  "usampler2DArray"                { afterType = true; return USAMPLER2DARRAY; }
  "sampler3D"                      { afterType = true; return SAMPLER3D; }
  "sampler2DShadow"                { afterType = true; return SAMPLER2DSHADOW; }
  "sampler"                        { afterType = true; return SAMPLER; }
  "samplerShadow"                  { afterType = true; return SAMPLERSHADOW; }
  "textureCubeArray"               { afterType = true; return TEXTURECUBEARRAY; }
  "itextureCubeArray"              { afterType = true; return ITEXTURECUBEARRAY; }
  "utextureCubeArray"              { afterType = true; return UTEXTURECUBEARRAY; }
  "samplerCubeArray"               { afterType = true; return SAMPLERCUBEARRAY; }
  "samplerCubeArrayShadow"         { afterType = true; return SAMPLERCUBEARRAYSHADOW; }
  "isamplerCubeArray"              { afterType = true; return ISAMPLERCUBEARRAY; }
  "usamplerCubeArray"              { afterType = true; return USAMPLERCUBEARRAY; }
  "sampler1DArrayShadow"           { afterType = true; return SAMPLER1DARRAYSHADOW; }
  "isampler1DArray"                { afterType = true; return ISAMPLER1DARRAY; }
  "usampler1D"                     { afterType = true; return USAMPLER1D; }
  "isampler1D"                     { afterType = true; return ISAMPLER1D; }
  "usampler1DArray"                { afterType = true; return USAMPLER1DARRAY; }
  "samplerBuffer"                  { afterType = true; return SAMPLERBUFFER; }
  "isampler2DRect"                 { afterType = true; return ISAMPLER2DRECT; }
  "usampler2DRect"                 { afterType = true; return USAMPLER2DRECT; }
  "isamplerBuffer"                 { afterType = true; return ISAMPLERBUFFER; }
  "usamplerBuffer"                 { afterType = true; return USAMPLERBUFFER; }
  "sampler2DMS"                    { afterType = true; return SAMPLER2DMS; }
  "isampler2DMS"                   { afterType = true; return ISAMPLER2DMS; }
  "usampler2DMS"                   { afterType = true; return USAMPLER2DMS; }
  "sampler2DMSArray"               { afterType = true; return SAMPLER2DMSARRAY; }
  "isampler2DMSArray"              { afterType = true; return ISAMPLER2DMSARRAY; }
  "usampler2DMSArray"              { afterType = true; return USAMPLER2DMSARRAY; }
  "sampler1D"                      { afterType = true; return SAMPLER1D; }
  "sampler1DShadow"                { afterType = true; return SAMPLER1DSHADOW; }
  "sampler2DRect"                  { afterType = true; return SAMPLER2DRECT; }
  "sampler2DRectShadow"            { afterType = true; return SAMPLER2DRECTSHADOW; }
  "sampler1DArray"                 { afterType = true; return SAMPLER1DARRAY; }
  "samplerExternalOES"             { afterType = true; return SAMPLEREXTERNALOES; }
  "__samplerExternal2DY2YEXT"      { afterType = true; return SAMPLEREXTERNAL2DY2YEXT; }
  "f16sampler1D"                   { afterType = true; return F16SAMPLER1D; }
  "f16sampler2D"                   { afterType = true; return F16SAMPLER2D; }
  "f16sampler3D"                   { afterType = true; return F16SAMPLER3D; }
  "f16sampler2DRect"               { afterType = true; return F16SAMPLER2DRECT; }
  "f16samplerCube"                 { afterType = true; return F16SAMPLERCUBE; }
  "f16sampler1DArray"              { afterType = true; return F16SAMPLER1DARRAY; }
  "f16sampler2DArray"              { afterType = true; return F16SAMPLER2DARRAY; }
  "f16samplerCubeArray"            { afterType = true; return F16SAMPLERCUBEARRAY; }
  "f16samplerBuffer"               { afterType = true; return F16SAMPLERBUFFER; }
  "f16sampler2DMS"                 { afterType = true; return F16SAMPLER2DMS; }
  "f16sampler2DMSArray"            { afterType = true; return F16SAMPLER2DMSARRAY; }
  "f16sampler1DShadow"             { afterType = true; return F16SAMPLER1DSHADOW; }
  "f16sampler2DShadow"             { afterType = true; return F16SAMPLER2DSHADOW; }
  "f16sampler2DRectShadow"         { afterType = true; return F16SAMPLER2DRECTSHADOW; }
  "f16samplerCubeShadow"           { afterType = true; return F16SAMPLERCUBESHADOW; }
  "f16sampler1DArrayShadow"        { afterType = true; return F16SAMPLER1DARRAYSHADOW; }
  "f16sampler2DArrayShadow"        { afterType = true; return F16SAMPLER2DARRAYSHADOW; }
  "f16samplerCubeArrayShadow"      { afterType = true; return F16SAMPLERCUBEARRAYSHADOW; }
  // Textures
  "texture2DArray"                 { afterType = true; return TEXTURE2DARRAY; }
  "itexture2D"                     { afterType = true; return ITEXTURE2D; }
  "itexture3D"                     { afterType = true; return ITEXTURE3D; }
  "itextureCube"                   { afterType = true; return ITEXTURECUBE; }
  "itexture2DArray"                { afterType = true; return ITEXTURE2DARRAY; }
  "utexture2D"                     { afterType = true; return UTEXTURE2D; }
  "utexture3D"                     { afterType = true; return UTEXTURE3D; }
  "utextureCube"                   { afterType = true; return UTEXTURECUBE; }
  "utexture2DArray"                { afterType = true; return UTEXTURE2DARRAY; }
  "itexture1DArray"                { afterType = true; return ITEXTURE1DARRAY; }
  "utexture1D"                     { afterType = true; return UTEXTURE1D; }
  "itexture1D"                     { afterType = true; return ITEXTURE1D; }
  "utexture1DArray"                { afterType = true; return UTEXTURE1DARRAY; }
  "textureBuffer"                  { afterType = true; return TEXTUREBUFFER; }
  "itexture2DRect"                 { afterType = true; return ITEXTURE2DRECT; }
  "utexture2DRect"                 { afterType = true; return UTEXTURE2DRECT; }
  "itextureBuffer"                 { afterType = true; return ITEXTUREBUFFER; }
  "utextureBuffer"                 { afterType = true; return UTEXTUREBUFFER; }
  "texture2DMS"                    { afterType = true; return TEXTURE2DMS; }
  "itexture2DMS"                   { afterType = true; return ITEXTURE2DMS; }
  "utexture2DMS"                   { afterType = true; return UTEXTURE2DMS; }
  "texture2DMSArray"               { afterType = true; return TEXTURE2DMSARRAY; }
  "itexture2DMSArray"              { afterType = true; return ITEXTURE2DMSARRAY; }
  "utexture2DMSArray"              { afterType = true; return UTEXTURE2DMSARRAY; }
  "texture1D"                      { afterType = true; return TEXTURE1D; }
  "texture2DRect"                  { afterType = true; return TEXTURE2DRECT; }
  "texture1DArray"                 { afterType = true; return TEXTURE1DARRAY; }
  "f16texture1D"                   { afterType = true; return F16TEXTURE1D; }
  "f16texture2D"                   { afterType = true; return F16TEXTURE2D; }
  "f16texture3D"                   { afterType = true; return F16TEXTURE3D; }
  "f16texture2DRect"               { afterType = true; return F16TEXTURE2DRECT; }
  "f16textureCube"                 { afterType = true; return F16TEXTURECUBE; }
  "f16texture1DArray"              { afterType = true; return F16TEXTURE1DARRAY; }
  "f16texture2DArray"              { afterType = true; return F16TEXTURE2DARRAY; }
  "f16textureCubeArray"            { afterType = true; return F16TEXTURECUBEARRAY; }
  "f16textureBuffer"               { afterType = true; return F16TEXTUREBUFFER; }
  "f16texture2DMS"                 { afterType = true; return F16TEXTURE2DMS; }
  "f16texture2DMSArray"            { afterType = true; return F16TEXTURE2DMSARRAY; }
  "fcoopmatNV"                     { afterType = true; return FCOOPMATNV; }
  "icoopmatNV"                     { afterType = true; return ICOOPMATNV; }
  "ucoopmatNV"                     { afterType = true; return UCOOPMATNV; }
  "coopmat"                        { afterType = true; return COOPMAT; }
  "f16subpassInput"                { afterType = true; return F16SUBPASSINPUT; }
  "f16subpassInputMS"              { afterType = true; return F16SUBPASSINPUTMS; }

  "subpassInput"                   { return SUBPASSINPUT; }
  "subpassInputMS"                 { return SUBPASSINPUTMS; }
  "isubpassInput"                  { return ISUBPASSINPUT; }
  "isubpassInputMS"                { return ISUBPASSINPUTMS; }
  "usubpassInput"                  { return USUBPASSINPUT; }
  "usubpassInputMS"                { return USUBPASSINPUTMS; }
  "accelerationStructureNV"        { return ACCSTRUCTNV; }
  "accelerationStructureEXT"       { return ACCSTRUCTEXT; }
  "rayQueryEXT"                    { return RAYQUERYEXT; }
  "perprimitiveNV"                 { return PERPRIMITIVENV; }
  "perviewNV"                      { return PERVIEWNV; }
  "taskNV"                         { return PERTASKNV; }

  {FLOATCONSTANT}                  { return FLOATCONSTANT; }
  {DOUBLECONSTANT}                 { return DOUBLECONSTANT; }
  {INTCONSTANT}                    { return INTCONSTANT; }
  {UINTCONSTANT}                   { return UINTCONSTANT; }
  {BOOLCONSTANT}                   { return BOOLCONSTANT; }
  {STRING_LITERAL}                 { return STRING_LITERAL; }
  {IDENTIFIER}                     {
    if (!afterType && !afterStruct && userDefinedTypes.contains(yytext().toString())){
        afterType = true;
        return USER_TYPE_NAME;
    } else if (afterStruct) {
        afterStruct = false;
        userDefinedTypes.add(yytext().toString());
    }
    return IDENTIFIER;
  }
}
[^] { return BAD_CHARACTER; }
