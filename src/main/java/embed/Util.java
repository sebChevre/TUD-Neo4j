package embed;

import org.neo4j.cypher.internal.compiler.v2_1.commands.expressions.ProjectedPath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sce on 04.08.14.
 */
public class Util {

    final static String DB_POPULATE_FILE = "ress/ok/total_script.txt";


    public static void main(String[] args) {

        readAllFiles();

    }

    static List<String> readAllFiles(){
        Path dir = FileSystems.getDefault().getPath("ress/ok/");

        List<String> scriptsCypher = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                //System.out.println(file.getFileName());

                String fileContent = new String(Files.readAllBytes(file));

                //System.out.println(fileContent);

                scriptsCypher.add(fileContent);

            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }

        System.out.println("All files loaded: " + scriptsCypher.size() +" files in memoriy to execute scritps");
        return scriptsCypher;
    }


}
