package dk.medicinkortet.dosagetranslation;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URISyntaxException;

public class RequestLoader {

    public static String load(String filename) throws URISyntaxException, IOException {
        return IOUtils.toString(RequestLoader.class.getClassLoader().getResourceAsStream(filename));
    }

}
