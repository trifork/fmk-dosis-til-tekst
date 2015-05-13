package dk.medicinkortet.dosagetranslation;

import com.opencsv.CSVReader;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.FileReader;
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

    private static final String SOSIGW_PROXY_URL = "http://test1.ekstern-test.nspop.dk:8080/sosigw/proxy/soap-request";
    private static final String SOSIGW_WS_URL =    "http://test1.ekstern-test.nspop.dk:8080/sosigw/service/sosigw";

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
        replaceMap.put("##USERROLENAME##",  "Tandlæge");

        replaceMap.put("##SYSTEMNAME##",    "DosageToTestLoader");
        replaceMap.put("##CARECVR##",       "20921897");
        replaceMap.put("##CARENAME##",      "TRIFORK A/S");
    }
    private static final String DUMP_START_TAG = "<ssi2013:DumpData>";
    private static final String DUMP_END_TAG = "</ssi2013:DumpData>";
    private static final String SIGN_URL_END_TAG = "</ns2:BrowserUrl>";
    private static final String SIGN_URL_START_TAG = "<ns2:BrowserUrl>";

    public static void main(String[] args) throws Exception {
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

                // Indlæs til fmk
                loadDosageSuggestions(TextfileConverter.PATH_TO_OUTPUT_DIR);
            } finally {
                restorePatient();
            }
        }
    }

    private static void loadDosageSuggestions(String tempFileDir) throws IOException, SAXException, TransformerException, URISyntaxException {
        String filename = tempFileDir + "/DosageStructures.csv";
        try (CSVReader reader = new CSVReader(new FileReader(filename), '|')) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 5) {
                    String xml = nextLine[5];
                    if (!xml.equals("XML")) {
                        loadDosageSuggestion(xml);
                    }
                }
            }
        }
    }

    private static int successCount = 0;
    private static int failCount = 0;
    private static void loadDosageSuggestion(String xml) throws IOException, SAXException, TransformerException, URISyntaxException {
        Document dosageDocument = XmlHelper.loadXmlFromString(xml);
        NodeList dosageNodes = dosageDocument.getDocumentElement().getChildNodes();

        for (int i=0; i<dosageNodes.getLength(); ++i) {
            Node dosageNode = dosageNodes.item(i);
            if (dosageNode.getNodeType() == Node.ELEMENT_NODE) {
                Element dosageElement = (Element)dosageNode;
                NodeList dosageChildNodes = dosageElement.getChildNodes();
                if (dosageChildNodes.getLength() == 2) {
                    Element versionElement = (Element) dosageChildNodes.item(0);
                    String version = versionElement.getTextContent();
                    Element dosageStructureElement = (Element) dosageChildNodes.item(1);
                    String dosageStructureText = XmlHelper.elementToString(dosageStructureElement);

                    replaceMap.put("##DOSAGESTRUCTURE##", dosageStructureText);
                    String req = RequestReplacer.replaceAll(RequestLoader.load("create_drugmedication"+version+".xml"), replaceMap);
                    String response = sendRequest(req, SOSIGW_PROXY_URL, "");
                    if (!response.contains("CreateDrugMedicationResponse")) {
                        System.out.println("Failed to create drugMedication with dosage suggestion");
                        System.out.println("Version:" + version);
                        System.out.println("Request:" + req);
                        System.out.println("Response:" + response);
                        failCount++;
                    } else {
                        successCount++;
                    }
                    if ((failCount+successCount) % 10 == 0) {
                        System.out.println("Successcount: " + successCount + " failcount: " + failCount);
                    }
                } else {
                    System.out.println("Bad XML format");
                }
            }
        }
    }

    private static void restorePatient() throws IOException, URISyntaxException {
        String req = RequestReplacer.replaceAll(RequestLoader.load("restore_patient.xml"), replaceMap);
        String response = sendRequest(req, SOSIGW_PROXY_URL, null);
        if (response.contains("ssi2013:RestorePatientsResponse")) {
            System.out.println("Patient restored");
        }
    }

    private static boolean dumpPatient() throws IOException, URISyntaxException {
        String req = RequestReplacer.replaceAll(RequestLoader.load("dump_patient.xml"), replaceMap);
        String response = sendRequest(req, SOSIGW_PROXY_URL, null);
        if (response.contains("sosigw_no_valid_idcard_in_cache")) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("PLEASE SIGN IDCARD AND RESTART");
            System.out.println(findSigningUrl(response));
        } else if (response.contains("ID card is not valid in time")) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("IDCARD EXPIRED PLEASE RERUN");
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            logout();
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

    private static void logout() throws URISyntaxException, IOException {
        String req = RequestReplacer.replaceAll(RequestLoader.load("logout.xml"), replaceMap);
        sendRequest(req, SOSIGW_WS_URL, "\"http://sosi.dk/gw/2014.06.01#logoutWithResponse\"");
    }

    private static String sendRequest(String request, String url, String action) throws IOException {
        StringEntity entity = new StringEntity(request, ContentType.create("text/xml", Consts.UTF_8));
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(entity);
        if (action != null) {
            httpPost.addHeader("SOAPAction", action);
        }
        httpPost.addHeader("Content-Type", "text/xml; charset=UTF-8");

        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        }
    }

    private static String findSigningUrl(String message) throws UnsupportedEncodingException {
        int start = message.indexOf(SIGN_URL_START_TAG);
        int end = message.indexOf(SIGN_URL_END_TAG);
        String url = message.substring(start + SIGN_URL_START_TAG.length(), end);
        return url.replace("&amp;", "&");
    }

}
