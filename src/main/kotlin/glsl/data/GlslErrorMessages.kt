package glsl.data

class GlslErrorMessages {
    companion object {
        const val UNDECLARED_IDENTIFIER = "Use of undeclared identifier."
        const val VERSION_REQUIRED = "#version required and missing."
        const val NO_DEFINITION_MAIN = "No definition of main in shader."
        const val REDECLARED_IDENTIFIER = "Regular non-array variable '%s' may not be redeclared."
        const val MISSING_RETURN_FUNCTION = "Missing return for function '%s'."
        const val MAIN_MUST_RETURN_VOID = "main() must return void."
        const val INCOMPATIBLE_TYPES_IN_INIT = "Incompatible types in initialization (and no available implicit conversion)."
        const val INCOMPATIBLE_TYPES_IN_ASSIGNMENT = "Incompatible types (%s and %s) in assignment (and no available implicit conversion)."
        const val CANT_ACCESS_ARRAY_OF_TYPE = "Can't access array element of type '%s'."
        const val NO_MATCHING_FUNCTION_CALL = "No matching function for call to %s(%s)."
        const val TOO_FEW_ARGUMENTS_CONSTRUCTOR = "Too few arguments to constructor of '%s'."
        const val TOO_MANY_ARGUMENTS_CONSTRUCTOR = "Too many arguments to constructor of '%s'."
        const val TYPES_CONDITIONAL_EXPR_NO_MATCH = "Types '%s' and '%s' in conditional operator do not match (and no applicable implicit type conversion)."
        const val CONDITION_MUST_BE_BOOL = "Condition must be of type bool."
        const val INVALID_TYPES_ARGUMENT_CONSTRUCTOR = "Invalid type '%s' as argument %s of constructor of '%s'."
        const val CANT_ACCESS_ARRAY_ELEMENT = "Can't access array element of type '%s'."
        const val CONSTRUCTOR_PRIMITIVE_ONE_ARGUMENT = "Constructor of primitive type must have at least one argument."
        const val INVALID_CALL_OF = "Invalid call of '%s' (not a function or subroutine uniform)."
    }
}