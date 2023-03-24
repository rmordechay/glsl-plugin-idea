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
  boolean inPp = false;
  boolean afterBackslash = false;
  boolean afterType = false;
  boolean afterTypeQualifier = false;
  Set<CharSequence> userTypesTable = new HashSet<>();
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
%state IN_MULITLINE_COMMENT
%state PREPROCESSOR_IGNORE

WHITE_SPACE=[ \t\f]+
NEW_LINE=[\n\r]+
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

IDENTIFIER=[a-zA-Z_]+\w*
BOOLCONSTANT=false|true
STRING_LITERAL=(\"([^\"\\]|\\.)*\")
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
PP_TEXT=[^\\\n]*
MACRO_LINE="__LINE__"
MACRO_FILE="__FILE__"
MACRO_VERSION="__VERSION__"

%%

<IN_MULITLINE_COMMENT> {
    "*/"                           { yybegin(YYINITIAL); return MULTILINE_COMMENT; }
    [^*\n]+                        { return MULTILINE_COMMENT; }
    "*"                            { return MULTILINE_COMMENT; }
    {NEW_LINE}                     { return MULTILINE_COMMENT; }
}

<PREPROCESSOR_IGNORE> {
  "\\"                             { return WHITE_SPACE;}
  {NEW_LINE}                       { yybegin(YYINITIAL); return WHITE_SPACE; }
  {WHITE_SPACE}                    { return WHITE_SPACE; }
  {FLOATCONSTANT}                  { return FLOATCONSTANT; }
  {DOUBLECONSTANT}                 { return DOUBLECONSTANT; }
  {INTCONSTANT}                    { return INTCONSTANT; }
  {BOOLCONSTANT}                   { return BOOLCONSTANT; }
  {STRING_LITERAL}                 { return STRING_LITERAL; }
  {PP_TEXT}                        { return PP_TEXT;}
}

<YYINITIAL> {
  {WHITE_SPACE}                    { return WHITE_SPACE; }
  {NEW_LINE}                       {
                                      if (inPp && !afterBackslash) {
                                          afterBackslash = false;
                                          inPp = false;
                                          return PP_END;
                                      }
                                      afterBackslash = false;
                                      return WHITE_SPACE;
                                   }
  "\\"                             {
                                      if (inPp) {
                                          afterBackslash = true;
                                      }
                                      return WHITE_SPACE;
                                   }
  "/*"                             { yybegin(IN_MULITLINE_COMMENT);  return MULTILINE_COMMENT; }
  {LINE_COMMENT}                   { return LINE_COMMENT; }
  // Preprocessors
  {PP_VERSION}                     { inPp = true; return PP_VERSION;}
  {PP_UNDEF}                       { inPp = true; return PP_UNDEF;}
  {PP_IFDEF}                       { inPp = true; return PP_IFDEF;}
  {PP_IFNDEF}                      { inPp = true; return PP_IFNDEF;}
  {PP_ELSE}                        { inPp = true; return PP_ELSE;}
  {PP_ENDIF}                       { inPp = true; return PP_ENDIF;}
  {PP_INCLUDE}                     { inPp = true; return PP_INCLUDE;}
  {PP_EXTENSION}                   { inPp = true; return PP_EXTENSION;}
  {PP_LINE}                        { inPp = true; return PP_LINE;}
  {MACRO_LINE}                     { inPp = true; return MACRO_LINE;}
  {MACRO_FILE}                     { inPp = true; return MACRO_FILE;}
  {MACRO_VERSION}                  { inPp = true; return MACRO_VERSION;}
  {PP_DEFINE}                      { inPp = true; return PP_DEFINE;}
  {PP_IF}                          { inPp = true; return PP_IF;}
  {PP_ELIF}                        { inPp = true; return PP_ELIF;}
  {PP_ERROR}                       { yybegin(PREPROCESSOR_IGNORE); return PP_ERROR;}
  {PP_PRAGMA}                      { yybegin(PREPROCESSOR_IGNORE); return PP_PRAGMA;}
  // Punctuation
  "#"                              { inPp = true; return HASH; }
  ";"                              { afterType = false; afterTypeQualifier = false; return SEMICOLON; }
  ","                              { afterType = false; afterTypeQualifier = false; return COMMA; }
  ":"                              { afterType = false; afterTypeQualifier = false; return COLON; }
  "="                              { afterType = false; afterTypeQualifier = false; return EQUAL; }
  "("                              { afterType = false; afterTypeQualifier = false; return LEFT_PAREN; }
  ")"                              { afterType = false; afterTypeQualifier = false; return RIGHT_PAREN; }
  "."                              { afterType = false; afterTypeQualifier = false; return DOT; }
  "!"                              { afterType = false; afterTypeQualifier = false; return BANG; }
  "-"                              { afterType = false; afterTypeQualifier = false; return DASH; }
  "~"                              { afterType = false; afterTypeQualifier = false; return TILDE; }
  "+"                              { afterType = false; afterTypeQualifier = false; return PLUS; }
  "*"                              { afterType = false; afterTypeQualifier = false; return STAR; }
  "/"                              { afterType = false; afterTypeQualifier = false; return SLASH; }
  "%"                              { afterType = false; afterTypeQualifier = false; return PERCENT; }
  "<"                              { afterType = false; afterTypeQualifier = false; return LEFT_ANGLE; }
  ">"                              { afterType = false; afterTypeQualifier = false; return RIGHT_ANGLE; }
  "|"                              { afterType = false; afterTypeQualifier = false; return VERTICAL_BAR; }
  "^"                              { afterType = false; afterTypeQualifier = false; return CARET; }
  "&"                              { afterType = false; afterTypeQualifier = false; return AMPERSAND; }
  "?"                              { afterType = false; afterTypeQualifier = false; return QUESTION; }
  "["                              { afterType = false; afterTypeQualifier = false; return LEFT_BRACKET; }
  "]"                              { afterType = false; afterTypeQualifier = false; return RIGHT_BRACKET; }
  "{"                              { afterType = false; afterTypeQualifier = false; return LEFT_BRACE; }
  "}"                              { afterType = false; afterTypeQualifier = false; return RIGHT_BRACE; }
  "+="                             { afterType = false; afterTypeQualifier = false; return ADD_ASSIGN; }
  "-="                             { afterType = false; afterTypeQualifier = false; return SUB_ASSIGN; }
  "*="                             { afterType = false; afterTypeQualifier = false; return MUL_ASSIGN; }
  "/="                             { afterType = false; afterTypeQualifier = false; return DIV_ASSIGN; }
  "%="                             { afterType = false; afterTypeQualifier = false; return MOD_ASSIGN; }
  ">>"                             { afterType = false; afterTypeQualifier = false; return RIGHT_OP; }
  "<<"                             { afterType = false; afterTypeQualifier = false; return LEFT_OP; }
  "&&"                             { afterType = false; afterTypeQualifier = false; return AND_OP; }
  "||"                             { afterType = false; afterTypeQualifier = false; return OR_OP; }
  "^^"                             { afterType = false; afterTypeQualifier = false; return XOR_OP; }
  ">>="                            { afterType = false; afterTypeQualifier = false; return RIGHT_ASSIGN; }
  "<<="                            { afterType = false; afterTypeQualifier = false; return LEFT_ASSIGN; }
  "&="                             { afterType = false; afterTypeQualifier = false; return AND_ASSIGN; }
  "|="                             { afterType = false; afterTypeQualifier = false; return OR_ASSIGN; }
  "^="                             { afterType = false; afterTypeQualifier = false; return XOR_ASSIGN; }
  "=="                             { afterType = false; afterTypeQualifier = false; return EQ_OP; }
  "!="                             { afterType = false; afterTypeQualifier = false; return GE_OP; }
  ">="                             { afterType = false; afterTypeQualifier = false; return NE_OP; }
  "<="                             { afterType = false; afterTypeQualifier = false; return LE_OP; }
  "--"                             { afterType = false; afterTypeQualifier = false; return DEC_OP; }
  "++"                             { afterType = false; afterTypeQualifier = false; return INC_OP; }
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
    "sampler2d"                    { afterType = true; return SAMPLER2D; }
    "sampler3d"                    { afterType = true; return SAMPLER3D; }
    "samplercube"                  { afterType = true; return SAMPLERCUBE; }
    "sampler2dshadow"              { afterType = true; return SAMPLER2DSHADOW; }
    "samplercubeshadow"            { afterType = true; return SAMPLERCUBESHADOW; }
    "sampler2darray"               { afterType = true; return SAMPLER2DARRAY; }
    "sampler2darrayshadow"         { afterType = true; return SAMPLER2DARRAYSHADOW; }
    "samplercubearray"             { afterType = true; return SAMPLERCUBEARRAY; }
    "samplercubearrayshadow"       { afterType = true; return SAMPLERCUBEARRAYSHADOW; }
    "isampler2d"                   { afterType = true; return ISAMPLER2D; }
    "isampler3d"                   { afterType = true; return ISAMPLER3D; }
    "isamplercube"                 { afterType = true; return ISAMPLERCUBE; }
    "isampler2darray"              { afterType = true; return ISAMPLER2DARRAY; }
    "isamplercubearray"            { afterType = true; return ISAMPLERCUBEARRAY; }
    "usampler2d"                   { afterType = true; return USAMPLER2D; }
    "usampler3d"                   { afterType = true; return USAMPLER3D; }
    "usamplercube"                 { afterType = true; return USAMPLERCUBE; }
    "usampler2darray"              { afterType = true; return USAMPLER2DARRAY; }
    "usamplercubearray"            { afterType = true; return USAMPLERCUBEARRAY; }
    "sampler1d"                    { afterType = true; return SAMPLER1D; }
    "sampler1dshadow"              { afterType = true; return SAMPLER1DSHADOW; }
    "sampler1darray"               { afterType = true; return SAMPLER1DARRAY; }
    "sampler1darrayshadow"         { afterType = true; return SAMPLER1DARRAYSHADOW; }
    "isampler1d"                   { afterType = true; return ISAMPLER1D; }
    "isampler1darray"              { afterType = true; return ISAMPLER1DARRAY; }
    "usampler1d"                   { afterType = true; return USAMPLER1D; }
    "usampler1darray"              { afterType = true; return USAMPLER1DARRAY; }
    "sampler2drect"                { afterType = true; return SAMPLER2DRECT; }
    "sampler2drectshadow"          { afterType = true; return SAMPLER2DRECTSHADOW; }
    "isampler2drect"               { afterType = true; return ISAMPLER2DRECT; }
    "usampler2drect"               { afterType = true; return USAMPLER2DRECT; }
    "samplerbuffer"                { afterType = true; return SAMPLERBUFFER; }
    "isamplerbuffer"               { afterType = true; return ISAMPLERBUFFER; }
    "usamplerbuffer"               { afterType = true; return USAMPLERBUFFER; }
    "sampler2dms"                  { afterType = true; return SAMPLER2DMS; }
    "isampler2dms"                 { afterType = true; return ISAMPLER2DMS; }
    "usampler2dms"                 { afterType = true; return USAMPLER2DMS; }
    "sampler2dmsarray"             { afterType = true; return SAMPLER2DMSARRAY; }
    "isampler2dmsarray"            { afterType = true; return ISAMPLER2DMSARRAY; }
    "usampler2dmsarray"            { afterType = true; return USAMPLER2DMSARRAY; }
    "image2d"                      { afterType = true; return IMAGE2D; }
    "iimage2d"                     { afterType = true; return IIMAGE2D; }
    "uimage2d"                     { afterType = true; return UIMAGE2D; }
    "image3d"                      { afterType = true; return IMAGE3D; }
    "iimage3d"                     { afterType = true; return IIMAGE3D; }
    "uimage3d"                     { afterType = true; return UIMAGE3D; }
    "imagecube"                    { afterType = true; return IMAGECUBE; }
    "iimagecube"                   { afterType = true; return IIMAGECUBE; }
    "uimagecube"                   { afterType = true; return UIMAGECUBE; }
    "imagebuffer"                  { afterType = true; return IMAGEBUFFER; }
    "iimagebuffer"                 { afterType = true; return IIMAGEBUFFER; }
    "uimagebuffer"                 { afterType = true; return UIMAGEBUFFER; }
    "image1d"                      { afterType = true; return IMAGE1D; }
    "iimage1d"                     { afterType = true; return IIMAGE1D; }
    "uimage1d"                     { afterType = true; return UIMAGE1D; }
    "image1darray"                 { afterType = true; return IMAGE1DARRAY; }
    "iimage1darray"                { afterType = true; return IIMAGE1DARRAY; }
    "uimage1darray"                { afterType = true; return UIMAGE1DARRAY; }
    "image2drect"                  { afterType = true; return IMAGE2DRECT; }
    "iimage2drect"                 { afterType = true; return IIMAGE2DRECT; }
    "uimage2drect"                 { afterType = true; return UIMAGE2DRECT; }
    "image2darray"                 { afterType = true; return IMAGE2DARRAY; }
    "iimage2darray"                { afterType = true; return IIMAGE2DARRAY; }
    "uimage2darray"                { afterType = true; return UIMAGE2DARRAY; }
    "imagecubearray"               { afterType = true; return IMAGECUBEARRAY; }
    "iimagecubearray"              { afterType = true; return IIMAGECUBEARRAY; }
    "uimagecubearray"              { afterType = true; return UIMAGECUBEARRAY; }
    "image2dms"                    { afterType = true; return IMAGE2DMS; }
    "iimage2dms"                   { afterType = true; return IIMAGE2DMS; }
    "uimage2dms"                   { afterType = true; return UIMAGE2DMS; }
    "image2dmsarray"               { afterType = true; return IMAGE2DMSARRAY; }
    "iimage2dmsarray"              { afterType = true; return IIMAGE2DMSARRAY; }
    "uimage2dmsarray"              { afterType = true; return UIMAGE2DMSARRAY; }
    // Control
    "if"                           { afterType = false; afterTypeQualifier = false; return IF; }
    "else"                         { afterType = false; afterTypeQualifier = false; return ELSE; }
    "switch"                       { afterType = false; afterTypeQualifier = false; return SWITCH; }
    "case"                         { afterType = false; afterTypeQualifier = false; return CASE; }
    "default"                      { afterType = false; afterTypeQualifier = false; return DEFAULT; }
    "while"                        { afterType = false; afterTypeQualifier = false; return WHILE; }
    "do"                           { afterType = false; afterTypeQualifier = false; return DO; }
    "for"                          { afterType = false; afterTypeQualifier = false; return FOR; }
    "continue"                     { afterType = false; afterTypeQualifier = false; return CONTINUE; }
    "break"                        { afterType = false; afterTypeQualifier = false; return BREAK; }
    "return"                       { afterType = false; afterTypeQualifier = false; return RETURN; }
    "discard"                      { afterType = false; afterTypeQualifier = false; return DISCARD; }
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
    // Rest (to be sorted)
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
    {FLOATCONSTANT}                  { return FLOATCONSTANT; }
    {INTCONSTANT}                    { return INTCONSTANT; }
    {UINTCONSTANT}                   { return UINTCONSTANT; }
    {BOOLCONSTANT}                   { return BOOLCONSTANT; }
    {STRING_LITERAL}                 { return STRING_LITERAL; }
    {IDENTIFIER}                     {
          String text = yytext().toString();
          if (afterType) {
              afterType = false;
              return IDENTIFIER;
          } else if (afterTypeQualifier) {
              afterTypeQualifier = false;
              userTypesTable.add(text);
              return TYPE_NAME_IDENTIFIER;
          } else if (userTypesTable.contains(text)) {
              return TYPE_NAME_IDENTIFIER;
          }
          return IDENTIFIER;
      }
}

[^] { return BAD_CHARACTER; }
