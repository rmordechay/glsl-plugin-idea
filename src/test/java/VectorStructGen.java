import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

public class VectorStructGen {
    private static final String[][] types = new String[][] {
            {
                    "Vec", "vec", "float"
            },
            {
                    "IVec", "ivec", "int"
            },
            {
                    "BVec", "bvec", "bool"
            },
            {
                    "DVec", "dvec", "double"
            },
            {
                    "UVec", "uvec", "uint"
            }
    };
    private static final char[][] groups = new char[][] {
            "xyzw".toCharArray(),
            "rgba".toCharArray(),
            "stpq".toCharArray()
    };

    public static void main(String[] args) throws IOException {
        var out = new PrintWriter(Files.newBufferedWriter(Path.of("glsl-vector-structs.glsl")));
        out.println("int length();");
        for (var type: types) {
            generateStruct(type, out);
        }
        out.close();
    }

    private static void permute(char[] symbols, int symbolCount, int length, Consumer<String> callback) {
        int permutations = 1;
        for (int i = 0; i < length; i++) {
            permutations *= symbolCount;
        }
        var sb = new StringBuilder();
        for (int i = 0; i < permutations; i++) {
            sb.setLength(0);
            int current = i;
            for (int x = 0; x < length; x++) {
                sb.append(symbols[current % symbolCount]);
                current /= symbolCount;
            }
            sb.reverse();
            callback.accept(sb.toString());
        }
    }

    private static void generateStruct(String[] type, PrintWriter out) {
        var vec = type[1];
        var scalar = type[2];
        for (int source = 2; source <= 4; source++) {
            out.printf("struct %s%d {\n", type[0], source);
            for (int target = 1; target <= 4; target++) {
                String pfx = target == 1 ? scalar : vec + target;
                for (var group: groups) {
                    permute(group, source, target, (str) -> {
                        out.printf("    %s %s;\n", pfx, str);
                    });
                }
            }
            out.println("};\n");
        }
    }
}
