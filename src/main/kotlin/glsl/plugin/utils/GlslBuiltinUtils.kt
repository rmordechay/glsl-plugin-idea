package glsl.plugin.utils

import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil.findChildOfType
import com.intellij.psi.util.PsiTreeUtil.findChildrenOfType
import glsl.data.GlslDefinitions
import glsl.data.GlslDefinitions.ShaderType.*
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslFileType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.utils.GlslUtils.getResourceFileAsString
import glsl.psi.interfaces.*
import java.util.*

object GlslBuiltinUtils {

    private lateinit var vecStructs: Map<String, Map<String, GlslNamedElement>>
    private lateinit var builtinConstants: Map<String, GlslNamedElement>
    private lateinit var defaultShaderVariables: Map<String, GlslNamedElement>
    private lateinit var shaderVariables: EnumMap<GlslDefinitions.ShaderType, Map<String, GlslNamedElement>>
    private lateinit var builtinFuncs: Map<String, List<GlslFunctionPrototype>>

    /**
     *
     */
    private fun getBuiltinFile(fileName: String): GlslFile? {
        val project = ProjectManager.getInstance().defaultProject
        val funcsString = getResourceFileAsString("builtin-objects/$fileName.glsl") ?: return null
        val glslFile = PsiFileFactory.getInstance(project)
            .createFileFromText(fileName, GlslFileType(), funcsString) as? GlslFile
        glslFile?.viewProvider?.virtualFile?.isWritable = false
        glslFile?.viewProvider
        return glslFile
    }

    /**
     * Creates a map of the GLSL builtin functions with their name as a key and a list of their AST
     * as a value. Due to overloading, most functions have different signatures with the same name.
     * Therefore, we want to create a list of them and show all possible signatures to the user.
     */
    fun getBuiltinFuncs(): Map<String, List<GlslFunctionPrototype>> {
        if (::builtinFuncs.isInitialized) {
            return builtinFuncs
        }
        val funcs = mutableMapOf<String, MutableList<GlslFunctionPrototype>>()
        val builtinFile = getBuiltinFile("glsl-builtin-functions")
        val declarations = findChildrenOfType(builtinFile, GlslDeclaration::class.java)
        for (declaration in declarations) {
            val funcName = findChildOfType(declaration, GlslFunctionHeader::class.java)?.name ?: continue
            val functionPrototype = declaration.functionPrototype ?: continue
            if (funcs.containsKey(funcName)) {
                funcs[funcName]?.add(functionPrototype)
            } else {
                funcs[funcName] = mutableListOf(functionPrototype)
            }
        }
        builtinFuncs = funcs
        return funcs
    }

    /**
     *
     */
    fun getVecStructs(): Map<String, Map<String, GlslNamedElement>> {
        if (::vecStructs.isInitialized) {
            return vecStructs
        }
        val builtinFile = getBuiltinFile("glsl-vector-structs")
        val structSpecifiers = findChildrenOfType(builtinFile, GlslStructSpecifier::class.java).toList()
        val lengthFunc = findChildOfType(builtinFile, GlslFunctionHeader::class.java)
        val vecStructsTemp = hashMapOf<String, MutableMap<String, GlslNamedElement>>()
        for (structSpecifier in structSpecifiers) {
            val vecName = structSpecifier.name?.lowercase() ?: continue
            for (structDeclaration in structSpecifier.structDeclarationList) {
                val structDeclarator = structDeclaration.structDeclaratorList.first()
                val structDeclaratorName = structDeclarator.name ?: continue
                vecStructsTemp.putIfAbsent(vecName, hashMapOf())
                vecStructsTemp[vecName]!![structDeclaratorName] = structDeclarator
            }
            if (lengthFunc != null && lengthFunc.name == "length") {
                vecStructsTemp[vecName]?.set("length", lengthFunc)
            }
        }
        this.vecStructs = vecStructsTemp
        return vecStructsTemp
    }

    /**
     *
     */
    fun getVecComponent(name: String, vecType: String? = null): GlslNamedElement? {
        if (vecType == null) {
            return getVecStructs()["vec3"]?.get(name)
        }
        return getVecStructs()[vecType]?.get(name)
    }

    /**
     *
     */
    fun getBuiltinConstants(): Map<String, GlslNamedElement> {
        if (::builtinConstants.isInitialized) {
            return builtinConstants
        }
        val builtinFile = getBuiltinFile("glsl-builtin-constants")
        val singleDeclarations = findChildrenOfType(builtinFile, GlslSingleDeclaration::class.java).toList()
        val constants = hashMapOf<String, GlslNamedElement>()
        for (child in singleDeclarations) {
            val childName = child.name
            if (childName != null) {
                constants[childName] = child
            }
        }
        builtinConstants = constants
        return constants
    }

    /**
     *
     */
    fun getShaderVariables(fileExtension: String? = null): Map<String, GlslNamedElement> {
        if (!::defaultShaderVariables.isInitialized || !::shaderVariables.isInitialized) {
            setShaderVariables()
        }
        val shaderType = getShaderType(fileExtension)
        if (shaderType == GLSL) {
            return defaultShaderVariables
        }
        return shaderVariables[shaderType] ?: emptyMap()
    }

    /**
     *
     */
    private fun isShaderVariable(variable: String, fileExtension: String?): Boolean {
        if (fileExtension == null) return false
        if (!::defaultShaderVariables.isInitialized || !::shaderVariables.isInitialized) {
            setShaderVariables()
        }
        fun isAinB(a: String, b: Map<String, GlslNamedElement>?): Boolean = if (b != null) a in b.keys else false
        return when (val shaderType = getShaderType(fileExtension)) {
            VERT -> isAinB(variable, shaderVariables[shaderType])
            GEOM -> isAinB(variable, shaderVariables[shaderType])
            FRAG -> isAinB(variable, shaderVariables[shaderType])
            TESC -> isAinB(variable, shaderVariables[shaderType])
            TESE -> isAinB(variable, shaderVariables[shaderType])
            COMP -> isAinB(variable, shaderVariables[shaderType])
            GLSL -> isAinB(variable, defaultShaderVariables)
        }
    }

    /**
     *
     */
    private fun setShaderVariables() {
        val shaderVariablesFile = getBuiltinFile("glsl-shader-variables")
        val structSpecifiers = findChildrenOfType(shaderVariablesFile, GlslStructSpecifier::class.java).toList()
        // Initializes map with ShaderType enum
        shaderVariables = EnumMap(GlslDefinitions.ShaderType::class.java)
        val allShaderVariables = hashMapOf<String, GlslNamedElement>()
        for (structSpecifier in structSpecifiers) {
            val structDeclarators = hashMapOf<String, GlslNamedElement>()
            for (structMember in structSpecifier.getStructMembers()) {
                val memberName = structMember.name ?: continue
                structDeclarators[memberName] = structMember
                allShaderVariables[memberName] = structMember
            }
            val shaderType = getShaderType(structSpecifier.name)
            shaderVariables[shaderType] = structDeclarators
        }
        defaultShaderVariables = allShaderVariables
    }

    /**
     *
     */
    fun isBuiltin(name: String?, fileExtension: String? = null): Boolean {
        if (name == null) return false
        return isBuiltinName(name, fileExtension) || isBuiltinConstant(name)
    }

    /**
     *
     */
    fun isBuiltinName(name: String, fileExtension: String? = null): Boolean {
        return name in getBuiltinFuncs().keys || isShaderVariable(name, fileExtension)
    }

    /**
     *
     */
    fun isBuiltinConstant(name: String): Boolean {
        return name in getBuiltinConstants().keys
    }

    /**
     *
     */
    private fun getShaderType(fileExtension: String?): GlslDefinitions.ShaderType {
        if (fileExtension == null) return GLSL
        return try {
            valueOf(fileExtension.lowercase())
        } catch (_: IllegalArgumentException) {
            GLSL
        }
    }
}