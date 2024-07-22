package glsl.data


enum class GlslErrorCode(val shortName: String, val message: String) {
    MISSING_RETURN_FUNCTION("Missing return function", "Missing return for function '%s'."),
    INCOMPATIBLE_TYPES_IN_INIT("Incompatible types in init", "Incompatible types in initialization (and no available implicit conversion)."),
    NO_MATCHING_FUNCTION_CALL("No matching function call", "No matching function for call to %s(%s)."),
    TOO_FEW_ARGUMENTS_CONSTRUCTOR("Too few arguments constructor", "Too few arguments to constructor of '%s'."),
    TOO_MANY_ARGUMENTS_CONSTRUCTOR("Too many arguments constructor", "Too many arguments to constructor of '%s'."),
    DOES_NOT_OPERATE_ON("Does not operate on", "s' does not operate on '%s' and '%s'."),
    REDECLARED_IDENTIFIER("Redeclared identifier", "Regular non-array variable '%s' may not be redeclared."),
    MAIN_MUST_RETURN_VOID("Main must return void", "main() must return void."),
    INCOMPATIBLE_TYPES_IN_ASSIGNMENT("Incompatible types in assignment", "Incompatible types (%s and %s) in assignment (and no available implicit conversion)."),
    CANT_ACCESS_ARRAY_OF_TYPE("Cant access array of type", "Can't access array element of type '%s'."),
    TYPES_CONDITIONAL_EXPR_NO_MATCH("Types conditional expr no match", "Types '%s' and '%s' in conditional operator do not match (and no applicable implicit type conversion)."),
    CONDITION_MUST_BE_BOOL("Condition must be bool", "Condition must be of type bool."),
    INVALID_TYPES_ARGUMENT_CONSTRUCTOR("Invalid types argument constructor", "Invalid type '%s' as argument %s of constructor of '%s'."),
    CANT_ACCESS_ARRAY_ELEMENT("Cant access array element", "Can't access array element of type '%s'."),
    CONSTRUCTOR_PRIMITIVE_ONE_ARGUMENT("Constructor primitive one argument", "Constructor of primitive type must have at least one argument."),
    INVALID_CALL_OF("Invalid call of", "Invalid call of '%s' (not a function or subroutine uniform)."),
}
