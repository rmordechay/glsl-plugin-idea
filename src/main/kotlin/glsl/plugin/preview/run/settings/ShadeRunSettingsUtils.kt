package glsl.plugin.preview.run.settings

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.util.elementType
import glsl.plugin.psi.named.variables.GlslNamedSingleDeclaration
import glsl.psi.interfaces.GlslSingleDeclaration

/**
 * Modify the run configuration based on the shader file. Like extracting uniform names.
 * @param configuration The run configuration to modify
 * @param psiFile The shader file
 */
fun modifyOptionsFromShaderSourceFile(configuration: ShaderRunConfiguration, psiFile: PsiFile) {
    psiFile.let {
        configuration.setFragmentFile(it.virtualFile.url)
        configuration.name = it.containingFile.name

        val uniformMapping = mutableMapOf<UniformType, String>()
        getUniforms(psiFile).forEach { uniformVarName ->
            for (uniformType in UniformType.entries) {
                if (uniformVarName.contains(uniformType.name, ignoreCase = true)) {
                    if (uniformMapping.containsKey(uniformType)) {//name for this uniform type already exists
                        if (!uniformMapping[uniformType]?.lowercase().equals(uniformType.defaultName.lowercase())) { //found name that equals the default name has more priority
                            uniformMapping[uniformType] = uniformVarName; //if it is not the default name, replace it with the new uniform name
                        }
                    } else {
                        uniformMapping[uniformType] = uniformVarName
                    }
                }
            }
        }
        configuration.setUniformNames(uniformMapping)
    }
}

/**
 * Find all uniform names in the shader file
 */
fun getUniforms(psiElement: PsiElement): List<String> {
    val uniforms = mutableListOf<String>()
    psiElement.accept(object : PsiRecursiveElementVisitor() {
        override fun visitElement(element: PsiElement) {
            if (element is GlslSingleDeclaration && element.typeQualifier?.text.equals("uniform")) {
                uniforms.add(element.name!!)
            }
            element.acceptChildren(this)
        }
    })
    return uniforms
}