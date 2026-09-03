package glsl.plugin.utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil.findChildOfType
import com.intellij.psi.util.PsiTreeUtil.findChildrenOfType
import com.intellij.psi.util.findParentOfType
import glsl.data.ShaderType
import glsl.data.ShaderType.*
import glsl.plugin.language.GlslFile
import glsl.plugin.language.GlslFileType
import glsl.plugin.psi.named.GlslNamedElement
import glsl.plugin.psi.named.GlslNamedVariable
import glsl.plugin.psi.named.types.user.GlslNamedStructSpecifier
import glsl.plugin.utils.GlslUtils.getResourceFileAsString
import glsl.psi.interfaces.GlslDeclaration
import glsl.psi.interfaces.GlslFunctionDeclarator
import glsl.psi.interfaces.GlslSingleDeclaration
import glsl.psi.interfaces.GlslStructSpecifier
import java.util.*

object GlslBuiltinUtils {

    /**
     * Cached per-project (via [CachedValuesManager]) rather than as a single shared cache, since
     * the builtin PSI elements are built against a specific [Project] and become invalid once
     * that project is disposed - which can happen independently of any other open project.
     */
    private val BUILTIN_FUNCS_KEY = Key.create<CachedValue<Map<String, List<GlslFunctionDeclarator>>>>("glsl.builtinFuncs")
    private val VEC_STRUCTS_KEY = Key.create<CachedValue<Map<String, Map<String, GlslNamedVariable>>>>("glsl.vecStructs")
    private val BUILTIN_CONSTANTS_KEY = Key.create<CachedValue<Map<String, GlslNamedVariable>>>("glsl.builtinConstants")
    private val SHADER_VARIABLES_KEY = Key.create<CachedValue<ShaderVariables>>("glsl.shaderVariables")

    private class ShaderVariables(
        val default: Map<String, GlslNamedVariable>,
        val perType: EnumMap<ShaderType, Map<String, GlslNamedVariable>>
    )

    /**
     * Creates a map of the GLSL builtin functions with their name as a key and a list of their AST
     * as a value. Due to overloading, most functions have different signatures with the same name.
     * Therefore, we want to create a list of them and show all possible signatures to the user.
     */
    fun getBuiltinFuncs(project: Project): Map<String, List<GlslFunctionDeclarator>> {
        return CachedValuesManager.getManager(project).getCachedValue(project, BUILTIN_FUNCS_KEY, {
            val funcs = mutableMapOf<String, MutableList<GlslFunctionDeclarator>>()
            val builtinFile = getBuiltinFile(project, "glsl-builtin-functions")
            val declarations = findChildrenOfType(builtinFile, GlslDeclaration::class.java)
            for (declaration in declarations) {
                val funcName = findChildOfType(declaration, GlslFunctionDeclarator::class.java)?.name ?: continue
                val functionDeclarator = declaration.functionDeclarator ?: continue
                if (funcs.containsKey(funcName)) {
                    funcs[funcName]?.add(functionDeclarator)
                } else {
                    funcs[funcName] = mutableListOf(functionDeclarator)
                }
            }
            CachedValueProvider.Result.create(funcs as Map<String, List<GlslFunctionDeclarator>>, ModificationTracker.NEVER_CHANGED)
        }, false)
    }

    /**
     *
     */
    fun getVecStructs(project: Project): Map<String, Map<String, GlslNamedVariable>> {
        return CachedValuesManager.getManager(project).getCachedValue(project, VEC_STRUCTS_KEY, {
            val builtinFile = getBuiltinFile(project, "glsl-vector-structs")
            val structSpecifiers = findChildrenOfType(builtinFile, GlslStructSpecifier::class.java).toList()
            val lengthFunc = findChildOfType(builtinFile, GlslFunctionDeclarator::class.java)
            val vecStructsTemp = hashMapOf<String, MutableMap<String, GlslNamedVariable>>()
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
            CachedValueProvider.Result.create(vecStructsTemp as Map<String, Map<String, GlslNamedVariable>>, ModificationTracker.NEVER_CHANGED)
        }, false)
    }

    /**
     *
     */
    fun getBuiltinConstants(project: Project): Map<String, GlslNamedVariable> {
        return CachedValuesManager.getManager(project).getCachedValue(project, BUILTIN_CONSTANTS_KEY, {
            val builtinFile = getBuiltinFile(project, "glsl-builtin-constants")
            val singleDeclarations = findChildrenOfType(builtinFile, GlslSingleDeclaration::class.java).toList()
            val constants = hashMapOf<String, GlslNamedVariable>()
            for (child in singleDeclarations) {
                val childName = child.name
                if (childName != null) {
                    constants[childName] = child
                }
            }
            CachedValueProvider.Result.create(constants as Map<String, GlslNamedVariable>, ModificationTracker.NEVER_CHANGED)
        }, false)
    }

    fun getShaderVariables(project: Project, fileExtension: String? = null): Map<String, GlslNamedVariable> {
        val shaderVariables = getShaderVariablesData(project)
        val shaderType = getShaderType(fileExtension)
        if (shaderType == GLSL) {
            return shaderVariables.default
        }
        return shaderVariables.perType[shaderType] ?: emptyMap()
    }

    /**
     *
     */
    private fun getShaderVariablesData(project: Project): ShaderVariables {
        return CachedValuesManager.getManager(project).getCachedValue(project, SHADER_VARIABLES_KEY, {
            val shaderVariablesFile = getBuiltinFile(project, "glsl-shader-variables")
            val structSpecifiers = findChildrenOfType(shaderVariablesFile, GlslStructSpecifier::class.java).filter { it.findParentOfType<GlslStructSpecifier>() == null }.toList()
            // Initializes map with ShaderType enum
            val perTypeVariables = EnumMap<ShaderType, Map<String, GlslNamedVariable>>(ShaderType::class.java)
            val allShaderVariables = hashMapOf<String, GlslNamedVariable>()
            for (structSpecifier in structSpecifiers) {
                val namedStruct = structSpecifier as GlslNamedStructSpecifier
                val structDeclarators = hashMapOf<String, GlslNamedVariable>()
                for (structMember in structSpecifier.getStructMembers()) {
                    val memberName = structMember.name ?: continue
                    structDeclarators[memberName] = structMember
                    allShaderVariables[memberName] = structMember
                }
                val shaderType = getShaderType(namedStruct.name)
                perTypeVariables[shaderType] = structDeclarators
            }
            CachedValueProvider.Result.create(ShaderVariables(allShaderVariables, perTypeVariables), ModificationTracker.NEVER_CHANGED)
        }, false)
    }

    /**
     *
     */
    fun isBuiltin(project: Project, name: String?, fileExtension: String? = null): Boolean {
        if (name == null) return false
        return isBuiltinFunction(project, name) || isBuiltinShaderVariable(project, name, fileExtension) || isBuiltinConstant(project, name)
    }

    /**
     *
     */
    fun isBuiltinFunction(project: Project, name: String?): Boolean {
        if (name == null) return false
        return name in getBuiltinFuncs(project).keys
    }

    /**
     *
     */
    fun isBuiltinConstant(project: Project, name: String): Boolean {
        return name in getBuiltinConstants(project).keys
    }

    /**
     *
     */
    fun isBuiltinShaderVariable(project: Project, variable: String, fileExtension: String?): Boolean {
        if (fileExtension == null) return false
        val shaderVariables = getShaderVariablesData(project)
        fun isAinB(a: String, b: Map<String, GlslNamedElement>?): Boolean = if (b != null) a in b.keys else false
        return when (val shaderType = getShaderType(fileExtension)) {
            VERT -> isAinB(variable, shaderVariables.perType[shaderType])
            GEOM -> isAinB(variable, shaderVariables.perType[shaderType])
            FRAG -> isAinB(variable, shaderVariables.perType[shaderType])
            TESC -> isAinB(variable, shaderVariables.perType[shaderType])
            TESE -> isAinB(variable, shaderVariables.perType[shaderType])
            COMP -> isAinB(variable, shaderVariables.perType[shaderType])
            GLSL -> isAinB(variable, shaderVariables.default)
        }
    }

    /**
     *
     */
    private fun getBuiltinFile(project: Project, fileName: String): GlslFile? {
        val funcsString = getResourceFileAsString("builtin-objects/$fileName.glsl") ?: return null
        val fileFactory = PsiFileFactory.getInstance(project)
        val glslFile = fileFactory.createFileFromText(fileName, GlslFileType(), funcsString) as? GlslFile
        glslFile?.viewProvider?.virtualFile?.isWritable = false
        return glslFile
    }

    /**
     *
     */
    private fun getShaderType(fileExtension: String?): ShaderType {
        if (fileExtension == null) return GLSL
        return try {
            valueOf(fileExtension.lowercase())
        } catch (_: IllegalArgumentException) {
            GLSL
        }
    }
}
