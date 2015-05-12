package dk.medicinkortet.dosagetranslation;

import org.apache.http.Consts;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class RequestLoader {

    public static String load(String filename) throws URISyntaxException, IOException {
        List<String> allLines = Files.readAllLines(
                Paths.get(RequestLoader.class.getClassLoader().getResource(filename).toURI()), Consts.UTF_8);
        return allLines.stream().collect(Collectors.joining("\n"));
    }

}
