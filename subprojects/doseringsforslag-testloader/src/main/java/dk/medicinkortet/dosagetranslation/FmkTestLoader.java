package dk.medicinkortet.dosagetranslation;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

/**
 * Test loading all suggestions to FMK
 */
public class FmkTestLoader {

    private static CloseableHttpClient httpclient = HttpClients.createDefault();

    private static final String SOSIGW_URL = "http://test1.ekstern-test.nspop.dk:8080/sosigw/proxy/soap-request";
    static Map<String, String> replaceMap;
    static {
        replaceMap = new HashMap<>();
        replaceMap.put("##FMKURL##",        "http://test1.fmk.netic.dk/fmk12/ws/MedicineCard");
        replaceMap.put("##FMKDUMPURL##",     "http://test1.fmk.netic.dk/fmk12/ws/DumpRestore");

        replaceMap.put("##USERNAME##",      "Farooq Olsson");
        replaceMap.put("##USERCPR##",       "1111141920");
        replaceMap.put("##USERGIVENNAME##", "Farooq");
        replaceMap.put("##USERSURNAME##",   "Olsson");
        replaceMap.put("##USERAUTH##",      "KSFDJ");
        replaceMap.put("##USERROLE##",      "5433");

        replaceMap.put("##SYSTEMNAME##",    "DosageToTestLoader");
        replaceMap.put("##CARECVR##",       "20921897");
        replaceMap.put("##CARENAME##",      "TRIFORK A/S");
    }
    private static final String DUMP_START_TAG = "<ssi2013:DumpData>";
    private static final String DUMP_END_TAG = "</ssi2013:DumpData>";
    private static final String SIGN_URL_END_TAG = "</ns2:BrowserUrl>";
    private static final String SIGN_URL_START_TAG = "<ns2:BrowserUrl>";

    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args.length < 1) {
            System.out.println("Usage: java dk.medicinkortet.dosagetranslation.FmkTestLoader <inputfile.txt>");
            System.exit(-1);
        }
        Scanner input = new Scanner(System.in);
        System.out.println("Tester om der er logget ind");

        System.out.println("**************************");
        System.out.println("* Dosis til tekst tester *");
        System.out.println("**************************");
        System.out.println("Skriv CPR på en testpatient: ");
        String cpr = input.next();
        replaceMap.put("##CPR##", cpr);
        if (dumpPatient()) {
            try {
                Path tempFilename = Files.createTempDirectory(UUID.randomUUID().toString()).toAbsolutePath();
                TextfileConverter.PATH_TO_INPUT_FILE = args[0];
                TextfileConverter.PATH_TO_OUTPUT_DIR = tempFilename.toString();
                TextfileConverter.main(new String[0]);
            } finally {
                restorePatient();
            }
        }
        // Indlæs til fmk

    }

    private static void restorePatient() throws IOException, URISyntaxException {
        String req = RequestReplacer.replaceAll(RequestLoader.load("restore_patient.xml"), replaceMap);
        String response = sendRequest(req);
        if (response.contains("ssi2013:RestorePatientsResponse")) {
            System.out.println("Patient restored");
        }
    }

    private static boolean dumpPatient() throws IOException, URISyntaxException {
        String req = RequestReplacer.replaceAll(RequestLoader.load("dump_patient.xml"), replaceMap);
        String response = sendRequest(req);
        if (response.contains("sosigw_no_valid_idcard_in_cache")) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("PLEASE SIGN IDCARD AND RESTART");
            System.out.println(findSigningUrl(response));
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } else {
            int dumpStart = response.indexOf(DUMP_START_TAG);
            int dumpEnd = response.indexOf(DUMP_END_TAG);
            if (dumpStart > 0 && dumpEnd > 0 && dumpEnd > dumpStart) {
                String dumpData = response.substring(dumpStart + DUMP_START_TAG.length(), dumpEnd);
                replaceMap.put("##DUMPDATA##", dumpData);
                return true;
            }
        }
        return false;
    }

    private static String sendRequest(String request) throws IOException {
        StringEntity entity = new StringEntity(request, ContentType.create("application/xml", Consts.UTF_8));
        HttpPost httpPost = new HttpPost(SOSIGW_URL);
        httpPost.setEntity(entity);
        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity.getContentLength() > 0) {
                return EntityUtils.toString(responseEntity);
            }
        }
        return null;
    }

    private static String findSigningUrl(String message) throws UnsupportedEncodingException {
        int start = message.indexOf(SIGN_URL_START_TAG);
        int end = message.indexOf(SIGN_URL_END_TAG);
        String url = message.substring(start + SIGN_URL_START_TAG.length(), end);
        return url.replace("&amp;", "&");
    }

}
