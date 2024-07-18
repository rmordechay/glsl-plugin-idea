/**
 * This is only a dummy script that doesn't run as part of the plugin. It's just a separate
 * to write builtin funcs to file, documentation etc.
 */
package glsl.data

import com.intellij.lang.documentation.DocumentationMarkup
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path


/**
 *
 */
@Suppress("unused")
private fun sortFunctionsByTypePriority() {
    val priorities = mapOf(
        "bool" to 0,
        "int" to 1,
        "float" to 2,
        "uint" to 3,
        "double" to 4,
        "vec2" to 5,
        "vec3" to 6,
        "vec4" to 7,
        "ivec2" to 8,
        "ivec3" to 9,
        "ivec4" to 10,
        "bvec2" to 11,
        "bvec3" to 12,
        "bvec4" to 13,
        "uvec2" to 14,
        "uvec3" to 15,
        "uvec4" to 16,
        "dvec2" to 17,
        "dvec3" to 18,
        "dvec4" to 19,
        "mat2" to 20,
        "mat3" to 21,
        "mat4" to 22,
        "umat2" to 23,
        "umat3" to 24,
        "umat4" to 25,
        "imat2" to 26,
        "imat3" to 27,
        "imat4" to 28,
        "bmat2" to 29,
        "bmat3" to 30,
        "bmat4" to 31,
        "dmat2" to 32,
        "dmat3" to 33,
        "dmat4" to 34,
        "mat3x2" to 35,
        "mat2x4" to 36,
        "mat3x4" to 37,
    )
    val lines =
        Files.readAllLines(Path("/Users/roi.mordechay/Desktop/Programming/glsl-plugin-idea/src/main/resources/builtin-objects/glsl-builtin-functions.glsl"))
    val re = "^\\w+ (\\w+)".toRegex()
    lines.sortBy { re.matches(it) }
    val funcs = mutableMapOf<String, MutableList<String>>()
    for (line in lines) {
        val value = re.matchAt(line, 0)?.groups?.get(1)?.value ?: return
        funcs.putIfAbsent(value, mutableListOf())
        funcs[value]?.add(line)
    }
    val sortedFuncs = mutableListOf<String>()
    val file = File("builtin-functions.glsl")
    for (func in funcs.values) {
        val sortedStrings = func.sortedWith(compareBy { string ->
            val text = priorities.entries.firstOrNull { (key, _) ->
                val regex = "\\b$key\\b".toRegex()
                regex.containsMatchIn(string)
            }
            if (text == null) {
                println(string)
            }
            text!!.value
        })
        sortedFuncs.addAll(sortedStrings)
    }
    file.writeText("")
    for (sortedFunc in sortedFuncs) {
        file.appendText(sortedFunc + "\n")
    }
}
@Suppress("unused")
class DocsParts(val header: String, val parameters: Map<String, String>, val description: String?)


/**
 *
 */
@Suppress("unused")
private fun convertFuncTextToHtml(funcName: String, docsParts: DocsParts): String {
    val sb = StringBuilder()
    sb.append("<div id=\"$funcName\">")
    sb.append("<h1 style=\"margin-block-end: 20px;\">$funcName</h1>")
    sb.append(DocumentationMarkup.CONTENT_START)
    sb.append("${docsParts.header}.")
    sb.append(DocumentationMarkup.CONTENT_END)
    sb.append("<br>")
    sb.append("<h3>Parameters</h3>")
    sb.append(DocumentationMarkup.SECTIONS_START)
    for (parameter in docsParts.parameters.entries) {
        sb.append("<tr>")
        sb.append("<td><p><b>${parameter.key}&nbsp;&nbsp;&nbsp;&nbsp;</b><p></td>")
        sb.append("<td><p>${parameter.value}<p></td>")
        sb.append("</tr>")
    }
    sb.append(DocumentationMarkup.SECTIONS_END)
    sb.append("<br>")
    if (docsParts.description != null) {
        sb.append("<h3>Return</h3>")
        sb.append(DocumentationMarkup.CONTENT_START)
        sb.append(docsParts.description)
        sb.append(DocumentationMarkup.CONTENT_END)
    }
    sb.append("</div>")
    return sb.toString()
}

@Suppress("unused")
val replacements = mapOf(
    "genFType" to listOf("float", "vec2", "vec3", "vec4"),
    "genIType" to listOf("int", "ivec2", "ivec3", "ivec4"),
    "genUType" to listOf("uint", "uvec2", "uvec3", "uvec4"),
    "genDType" to listOf("double", "dvec2", "dvec3", "dvec4"),
    "genBType" to listOf("bool", "bvec2", "bvec3", "bvec4"),
    "gvec2" to listOf("vec2", "ivec2", "uvec2"),
    "gvec3" to listOf("vec3", "ivec3", "uvec3"),
    "gvec4" to listOf("vec4", "ivec4", "uvec4"),
    "vec" to listOf("vec2", "vec3", "vec4"),
    "bvec" to listOf("bvec2", "bvec3", "bvec4"),
    "ivec" to listOf("ivec2", "ivec3", "ivec4"),
    "uvec" to listOf("uvec2", "uvec3", "uvec4"),
    "dvec" to listOf("dvec2", "dvec3", "dvec4"),
    "gsubpassInput" to listOf("subpassInput", "isubpassInput", "usubpassInput"),
    "gsubpassInputMS" to listOf("subpassInputMS", "isubpassInputMS", "usubpassInputMS"),
    "gsampler2DRect" to listOf("sampler2DRect", "isampler2DRect", "usampler2DRect"),
    "gsamplerBuffer" to listOf("samplerBuffer", "isamplerBuffer", "usamplerBuffer"),
    "gsampler1D" to listOf("sampler1D", "isampler1D", "usampler1D"),
    "gsampler2D" to listOf("sampler2D", "isampler2D", "usampler2D"),
    "gsampler3D" to listOf("sampler3D", "isampler3D", "usampler3D"),
    "gsampler1DArray" to listOf("sampler1DArray", "isampler1DArray", "usampler1DArray"),
    "gsampler2DArray" to listOf("sampler2DArray", "isampler2DArray", "usampler2DArray"),
    "gsampler3D" to listOf("sampler3D", "isampler3D", "usampler3D"),
    "gsamplerCube" to listOf("samplerCube", "isamplerCube", "usamplerCube"),
    "gsamplerCubeArray" to listOf("samplerCubeArray", "isamplerCubeArray", "usamplerCubeArray"),
    "gsampler2DMS" to listOf("sampler2DMS", "isampler2DMS", "usampler2DMS"),
    "gsampler2DMSArray" to listOf("sampler2DMSArray", "isampler2DMSArray", "usampler2DMSArray"),
    "gimage1D" to listOf("image1D", "iimage1D", "uimage1D"),
    "gimage2D" to listOf("image2D", "iimage2D", "uimage2D"),
    "gimage3D" to listOf("image3D", "iimage3D", "uimage3D"),
    "gimageCube" to listOf("imageCube", "iimageCube", "uimageCube"),
    "gimage2DMS" to listOf("image2DMS", "iimage2DMS", "uimage2DMS"),
    "gimage2DRect" to listOf("image2DRect", "iimage2DRect", "uimage2DRect"),
    "gimage1DArray" to listOf("image1DArray", "iimage1DArray", "uimage1DArray"),
    "gimage2DArray" to listOf("image2DArray", "iimage2DArray", "uimage2DArray"),
    "gimage2DMSArray" to listOf("image2DMSArray", "iimage2DMSArray", "uimage2DMSArray"),
    "gimageCubeArray" to listOf("imageCubeArray", "iimageCubeArray", "uimageCubeArray"),
    "gimageBuffer" to listOf("imageBuffer", "iimageBuffer", "uimageBuffer"),
)

@Suppress("unused")
val funcs = listOf(
    "int imageSize(readonly writeonly gimage1D image);",
    "ivec2 imageSize(readonly writeonly gimage2D image);",
    "ivec3 imageSize(readonly writeonly gimage3D image);",

)


@Suppress("unused")
val imageParams = listOf("gimage3D image, ivec3 P",
    "gimageCube image, ivec3 P",
    "gimageBuffer image, int P",
    "gimage2DArray image, ivec3 P",
    "gimageCubeArray image, ivec3 P",
    "gimage1D image, int P",
    "gimage1DArray image, ivec2 P",
    "gimage2DRect image, ivec2 P",
    "gimage2DMS image, ivec2 P, int sample",
    "gimage2DMSArray image, ivec3 P, int sample",
)

@Suppress("unused")
fun f2(funcs1: List<String>): List<String> {
    val lst = arrayListOf<String>()
    for (func in funcs1) {
        val funcSplit = func.split(" ")
        val genTypes = funcSplit.filter { replacements.keys.contains(it) }.toSet()
        if (genTypes.size == 2) {
            val first = replacements[genTypes.first()]!!
            val last = replacements[genTypes.last()]!!
            first.zip(last).forEach { (x, y) ->
                val find = "\\b${genTypes.first()}\\b".toRegex().replace(func, x)
                val find2 = "\\b${genTypes.last()}\\b".toRegex().replace(find, y)
                lst.add(find2)
            }
        } else if (genTypes.size == 1){
            val rep = replacements[genTypes.first()] ?: return emptyList()
            for (s in rep) {
                lst.add(func.replace(genTypes.first(), s))
            }
        } else if (genTypes.isEmpty()) {
            println(func)
        } else {
            print("***!!! $func !!!***")
        }
    }
    return lst
}

fun main() {
//    for (s in f2(funcs)) {
//        println(s)
//    }
//    for (func in funcs) {
//        if (func.contains("IMAGE_PARAMS")) {
//            for (imageParam in imageParams) {
//                println(func.replace("IMAGE_PARAMS", imageParam))
//            }
//        }
//    }
}



