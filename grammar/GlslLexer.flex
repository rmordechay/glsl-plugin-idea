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
  public Set<CharSequence> userTypesTable = new HashSet<>();

  public _GlslLexer() {
    this((java.io.Reader) null);
  }

  void reset() {
      afterType = false;
      afterTypeQualifier = false;
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
%state PREPROCESSOR

WHITE_SPACE=[ \t\f]+
NEW_LINE=[\n\r]+
BACKSLASH="\\"{NEW_LINE}

IDENTIFIER=[a-zA-Z_]+\w*
BOOLCONSTANT=false|true
STRING_LITERAL=(\"([^\"\\]|\\.)*\")
LINE_COMMENT="//"+.*
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

<YYINITIAL, PREPROCESSOR> {
    {WHITE_SPACE}                  { return WHITE_SPACE; }
    {NEW_LINE} {
        if (zzLexicalState == PREPROCESSOR) {
            yybegin(YYINITIAL);
            return PP_END;
        }
        return WHITE_SPACE;
    }
    {BACKSLASH}                    { return BACKSLASH; }
    "/*"                           { yybegin(IN_MULITLINE_COMMENT); return MULTILINE_COMMENT; }
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
    {PP_DEFINE}                    { yybegin(PREPROCESSOR_IGNORE); return PP_DEFINE;}
    {PP_ERROR}                     { yybegin(PREPROCESSOR_IGNORE); return PP_ERROR;}
    {PP_PRAGMA}                    { yybegin(PREPROCESSOR_IGNORE); return PP_PRAGMA;}
    // Punctuation
    ";"                            { reset(); return SEMICOLON; }
    "{"                            { reset(); return LEFT_BRACE; }
    "}"                            { reset(); return RIGHT_BRACE; }
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
    ","                            { return COMMA; }
    ":"                            { return COLON; }
    "="                            { return EQUAL; }
    "("                            { return LEFT_PAREN; }
    ")"                            { return RIGHT_PAREN; }
    "."                            { return DOT; }
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
    "void"                         { afterType = true; return VOID; }
    "float"                        { afterType = true; return FLOAT; }
    "double"                       { afterType = true; return DOUBLE; }
    "int"                          { afterType = true; return INT; }
    "uint"                         { afterType = true; return UINT; }
    "bool"                         { afterType = true; return BOOL; }
    "float16_t"                    { afterType = true; return FLOAT16_T; }
    "float32_t"                    { afterType = true; return FLOAT32_T; }
    "float64_t"                    { afterType = true; return FLOAT64_T; }
    "int64_t"                      { afterType = true; return INT64_T; }
    "uint64_t"                     { afterType = true; return UINT64_T; }
    "int32_t"                      { afterType = true; return INT32_T; }
    "uint32_t"                     { afterType = true; return UINT32_T; }
    "int16_t"                      { afterType = true; return INT16_T; }
    "uint16_t"                     { afterType = true; return UINT16_T; }
    "int8_t"                       { afterType = true; return INT8_T; }
    "uint8_t"                      { afterType = true; return UINT8_T; }
    "vec2"                         { afterType = true; return VEC2; }
    "vec3"                         { afterType = true; return VEC3; }
    "vec4"                         { afterType = true; return VEC4; }
    "dvec2"                        { afterType = true; return DVEC2; }
    "dvec3"                        { afterType = true; return DVEC3; }
    "dvec4"                        { afterType = true; return DVEC4; }
    "bvec2"                        { afterType = true; return BVEC2; }
    "bvec3"                        { afterType = true; return BVEC3; }
    "bvec4"                        { afterType = true; return BVEC4; }
    "ivec2"                        { afterType = true; return IVEC2; }
    "ivec3"                        { afterType = true; return IVEC3; }
    "ivec4"                        { afterType = true; return IVEC4; }
    "uvec2"                        { afterType = true; return UVEC2; }
    "uvec3"                        { afterType = true; return UVEC3; }
    "uvec4"                        { afterType = true; return UVEC4; }
    "mat2"                         { afterType = true; return MAT2; }
    "mat3"                         { afterType = true; return MAT3; }
    "mat4"                         { afterType = true; return MAT4; }
    "mat2x2"                       { afterType = true; return MAT2X2; }
    "mat2x3"                       { afterType = true; return MAT2X3; }
    "mat2x4"                       { afterType = true; return MAT2X4; }
    "mat3x2"                       { afterType = true; return MAT3X2; }
    "mat3x3"                       { afterType = true; return MAT3X3; }
    "mat3x4"                       { afterType = true; return MAT3X4; }
    "mat4x2"                       { afterType = true; return MAT4X2; }
    "mat4x3"                       { afterType = true; return MAT4X3; }
    "mat4x4"                       { afterType = true; return MAT4X4; }
    "dmat2"                        { afterType = true; return DMAT2; }
    "dmat3"                        { afterType = true; return DMAT3; }
    "dmat4"                        { afterType = true; return DMAT4; }
    "dmat2x2"                      { afterType = true; return DMAT2X2; }
    "dmat2x3"                      { afterType = true; return DMAT2X3; }
    "dmat2x4"                      { afterType = true; return DMAT2X4; }
    "dmat3x2"                      { afterType = true; return DMAT3X2; }
    "dmat3x3"                      { afterType = true; return DMAT3X3; }
    "dmat3x4"                      { afterType = true; return DMAT3X4; }
    "dmat4x2"                      { afterType = true; return DMAT4X2; }
    "dmat4x3"                      { afterType = true; return DMAT4X3; }
    "dmat4x4"                      { afterType = true; return DMAT4X4; }
    "atomic_uint"                  { afterType = true; return ATOMIC_UINT; }
    "sampler2D"                    { afterType = true; return SAMPLER2D; }
    "sampler3D"                    { afterType = true; return SAMPLER3D; }
    "samplerCube"                  { afterType = true; return SAMPLERCUBE; }
    "sampler2DShadow"              { afterType = true; return SAMPLER2DSHADOW; }
    "samplerCubeShadow"            { afterType = true; return SAMPLERCUBESHADOW; }
    "sampler2DArray"               { afterType = true; return SAMPLER2DARRAY; }
    "sampler2DArrayShadow"         { afterType = true; return SAMPLER2DARRAYSHADOW; }
    "samplerCubeArray"             { afterType = true; return SAMPLERCUBEARRAY; }
    "samplerCubeArrayShadow"       { afterType = true; return SAMPLERCUBEARRAYSHADOW; }
    "isampler2D"                   { afterType = true; return ISAMPLER2D; }
    "isampler3D"                   { afterType = true; return ISAMPLER3D; }
    "isamplerCube"                 { afterType = true; return ISAMPLERCUBE; }
    "isampler2DArray"              { afterType = true; return ISAMPLER2DARRAY; }
    "isamplerCubeArray"            { afterType = true; return ISAMPLERCUBEARRAY; }
    "usampler2D"                   { afterType = true; return USAMPLER2D; }
    "usampler3D"                   { afterType = true; return USAMPLER3D; }
    "usamplerCube"                 { afterType = true; return USAMPLERCUBE; }
    "usampler2DArray"              { afterType = true; return USAMPLER2DARRAY; }
    "usamplerCubeArray"            { afterType = true; return USAMPLERCUBEARRAY; }
    "sampler1D"                    { afterType = true; return SAMPLER1D; }
    "sampler1DShadow"              { afterType = true; return SAMPLER1DSHADOW; }
    "sampler1DArray"               { afterType = true; return SAMPLER1DARRAY; }
    "sampler1DArrayShadow"         { afterType = true; return SAMPLER1DARRAYSHADOW; }
    "isampler1D"                   { afterType = true; return ISAMPLER1D; }
    "isampler1DArray"              { afterType = true; return ISAMPLER1DARRAY; }
    "usampler1D"                   { afterType = true; return USAMPLER1D; }
    "usampler1DArray"              { afterType = true; return USAMPLER1DARRAY; }
    "sampler2DRect"                { afterType = true; return SAMPLER2DRECT; }
    "sampler2DRectShadow"          { afterType = true; return SAMPLER2DRECTSHADOW; }
    "isampler2DRect"               { afterType = true; return ISAMPLER2DRECT; }
    "usampler2DRect"               { afterType = true; return USAMPLER2DRECT; }
    "samplerBuffer"                { afterType = true; return SAMPLERBUFFER; }
    "isamplerBuffer"               { afterType = true; return ISAMPLERBUFFER; }
    "usamplerBuffer"               { afterType = true; return USAMPLERBUFFER; }
    "sampler2DMS"                  { afterType = true; return SAMPLER2DMS; }
    "isampler2DMS"                 { afterType = true; return ISAMPLER2DMS; }
    "usampler2DMS"                 { afterType = true; return USAMPLER2DMS; }
    "sampler2DMSArray"             { afterType = true; return SAMPLER2DMSARRAY; }
    "isampler2DMSArray"            { afterType = true; return ISAMPLER2DMSARRAY; }
    "usampler2DMSArray"            { afterType = true; return USAMPLER2DMSARRAY; }
    "image2D"                      { afterType = true; return IMAGE2D; }
    "iimage2D"                     { afterType = true; return IIMAGE2D; }
    "uimage2D"                     { afterType = true; return UIMAGE2D; }
    "image3D"                      { afterType = true; return IMAGE3D; }
    "iimage3D"                     { afterType = true; return IIMAGE3D; }
    "uimage3D"                     { afterType = true; return UIMAGE3D; }
    "imageCube"                    { afterType = true; return IMAGECUBE; }
    "iimageCube"                   { afterType = true; return IIMAGECUBE; }
    "uimageCube"                   { afterType = true; return UIMAGECUBE; }
    "imageBuffer"                  { afterType = true; return IMAGEBUFFER; }
    "iimageBuffer"                 { afterType = true; return IIMAGEBUFFER; }
    "uimageBuffer"                 { afterType = true; return UIMAGEBUFFER; }
    "image1D"                      { afterType = true; return IMAGE1D; }
    "iimage1D"                     { afterType = true; return IIMAGE1D; }
    "uimage1D"                     { afterType = true; return UIMAGE1D; }
    "image1DArray"                 { afterType = true; return IMAGE1DARRAY; }
    "iimage1DArray"                { afterType = true; return IIMAGE1DARRAY; }
    "uimage1DArray"                { afterType = true; return UIMAGE1DARRAY; }
    "image2DRect"                  { afterType = true; return IMAGE2DRECT; }
    "iimage2DRect"                 { afterType = true; return IIMAGE2DRECT; }
    "uimage2DRect"                 { afterType = true; return UIMAGE2DRECT; }
    "image2DArray"                 { afterType = true; return IMAGE2DARRAY; }
    "iimage2DArray"                { afterType = true; return IIMAGE2DARRAY; }
    "uimage2DArray"                { afterType = true; return UIMAGE2DARRAY; }
    "imageCubeArray"               { afterType = true; return IMAGECUBEARRAY; }
    "iimageCubeArray"              { afterType = true; return IIMAGECUBEARRAY; }
    "uimageCubeArray"              { afterType = true; return UIMAGECUBEARRAY; }
    "image2DMS"                    { afterType = true; return IMAGE2DMS; }
    "iimage2DMS"                   { afterType = true; return IIMAGE2DMS; }
    "uimage2DMS"                   { afterType = true; return UIMAGE2DMS; }
    "image2DMSArray"               { afterType = true; return IMAGE2DMSARRAY; }
    "iimage2DMSArray"              { afterType = true; return IIMAGE2DMSARRAY; }
    "uimage2DMSArray"              { afterType = true; return UIMAGE2DMSARRAY; }
    // storage_qualifier
    "const"                        { afterTypeQualifier = true; return CONST; }
    "inout"                        { afterTypeQualifier = true; return INOUT; }
    "in"                           { afterTypeQualifier = true; return IN; }
    "out"                          { afterTypeQualifier = true; return OUT; }
    "centroid"                     { afterTypeQualifier = true; return CENTROID; }
    "patch"                        { afterTypeQualifier = true; return PATCH; }
    "sample"                       { afterTypeQualifier = true; return SAMPLE; }
    "uniform"                      { afterTypeQualifier = true; return UNIFORM; }
    "shared"                       { afterTypeQualifier = true; return SHARED; }
    "buffer"                       { afterTypeQualifier = true; return BUFFER; }
    "varying"                      { afterTypeQualifier = true; return VARYING; }
    "coherent"                     { afterTypeQualifier = true; return COHERENT; }
    "volatile"                     { afterTypeQualifier = true; return VOLATILE; }
    "restrict"                     { afterTypeQualifier = true; return RESTRICT; }
    "readonly"                     { afterTypeQualifier = true; return READONLY; }
    "writeonly"                    { afterTypeQualifier = true; return WRITEONLY; }
    "subroutine"                   { afterTypeQualifier = true; return SUBROUTINE; }
    "nonprivate"                   { afterTypeQualifier = true; return NONPRIVATE; }
    "attr"                         { afterTypeQualifier = true; return ATTR; }
    "hitattrnv"                    { afterTypeQualifier = true; return HITATTRNV; }
    "hitattrext"                   { afterTypeQualifier = true; return HITATTREXT; }
    "payloadnv"                    { afterTypeQualifier = true; return PAYLOADNV; }
    "payloadext"                   { afterTypeQualifier = true; return PAYLOADEXT; }
    "payloadinnv"                  { afterTypeQualifier = true; return PAYLOADINNV; }
    "payloadinext"                 { afterTypeQualifier = true; return PAYLOADINEXT; }
    "calldatanv"                   { afterTypeQualifier = true; return CALLDATANV; }
    "calldataext"                  { afterTypeQualifier = true; return CALLDATAEXT; }
    "calldatainnv"                 { afterTypeQualifier = true; return CALLDATAINNV; }
    "calldatainext"                { afterTypeQualifier = true; return CALLDATAINEXT; }
    "devicecoherent"               { afterTypeQualifier = true; return DEVICECOHERENT; }
    "queuefamilycoherent"          { afterTypeQualifier = true; return QUEUEFAMILYCOHERENT; }
    "workgroupcoherent"            { afterTypeQualifier = true; return WORKGROUPCOHERENT; }
    "subgroupcoherent"             { afterTypeQualifier = true; return SUBGROUPCOHERENT; }
    "shadercallcoherent"           { afterTypeQualifier = true; return SHADERCALLCOHERENT; }
    // precision_qualifier
    "highp"                        { afterTypeQualifier = true; return HIGH_PRECISION; }
    "mediump"                      { afterTypeQualifier = true; return MEDIUM_PRECISION; }
    "lowp"                         { afterTypeQualifier = true; return LOW_PRECISION; }
    // interpolation_qualifier
    "smooth"                       { afterTypeQualifier = true; return SMOOTH; }
    "flat"                         { afterTypeQualifier = true; return FLAT; }
    "noperspective"                { afterTypeQualifier = true; return NOPERSPECTIVE; }
    "__explicitInterpAMD"          { afterTypeQualifier = true; return EXPLICITINTERPAMD; }
    "pervertexnv"                  { afterTypeQualifier = true; return PERVERTEXNV; }
    "perprimitivenv"               { afterTypeQualifier = true; return PERPRIMITIVENV; }
    "perviewnv"                    { afterTypeQualifier = true; return PERVIEWNV; }
    "pertasknv"                    { afterTypeQualifier = true; return PERTASKNV; }
    // Rest
    "struct"                       { afterTypeQualifier = true; return STRUCT; }
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
    {IDENTIFIER}                   {
          String text = yytext().toString();
          if (afterType) {
              afterType = false;
              return IDENTIFIER;
          } else if (afterTypeQualifier) {
              afterTypeQualifier = false;
              afterType = true;
              userTypesTable.add(text);
              return TYPE_NAME_IDENTIFIER;
          } else if (userTypesTable.contains(text)) {
              afterType = true;
              return TYPE_NAME_IDENTIFIER;
          }
          return IDENTIFIER;
      }
}
[^]                               { return BAD_CHARACTER; }
