package dk.medicinkortet.dosagetranslation;

import java.util.Map;
import java.util.UUID;

public class RequestReplacer {

    public static String replaceAll(String text, Map<String, String> replaceMap) {
        String result = text;
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            result = result.replaceAll(entry.getKey(), entry.getValue());
        }
        result = result.replaceAll("##MESSAGEID##", UUID.randomUUID().toString());
        return result;
    }
}
