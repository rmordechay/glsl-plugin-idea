package glsl.data

data class GlslError(
    val errorCode: GlslErrorCode,
    val formattedMessage: String
)

enum class GlslErrorCode(val message: String) {
    INCOMPATIBLE_TYPES_IN_INIT("Incompatible types in initialization (and no available implicit conversion)."),
    MISSING_RETURN_FUNCTION("Missing return for function '%s'."),
    NO_MATCHING_FUNCTION_CALL("No matching function for call to %s(%s)."),
    DOES_NOT_OPERATE_ON("'%s' does not operate on '%s' and '%s'."),
    TOO_FEW_ARGUMENTS_CONSTRUCTOR("Too few arguments to constructor of '%s'."),
    TOO_MANY_ARGUMENTS_CONSTRUCTOR("Too many arguments to constructor of '%s'."),
    REDECLARED_IDENTIFIER("Regular non-array variable '%s' may not be redeclared."),
    MAIN_MUST_RETURN_VOID("main() must return void."),
    INCOMPATIBLE_TYPES_IN_ASSIGNMENT("Incompatible types (%s and %s) in assignment (and no available implicit conversion)."),
    CANT_ACCESS_ARRAY_OF_TYPE("Can't access array element of type '%s'."),
    TYPES_CONDITIONAL_EXPR_NO_MATCH("Types '%s' and '%s' in conditional operator do not match (and no applicable implicit type conversion)."),
    CONDITION_MUST_BE_BOOL("Condition must be of type bool."),
    INVALID_TYPES_ARGUMENT_CONSTRUCTOR("Invalid type '%s' as argument %s of constructor of '%s'."),
    CANT_ACCESS_ARRAY_ELEMENT("Can't access array element of type '%s'."),
    CONSTRUCTOR_PRIMITIVE_ONE_ARGUMENT("Constructor of primitive type must have at least one argument."),
    INVALID_CALL_OF("Invalid call of '%s' (not a function or subroutine uniform)."),
}
