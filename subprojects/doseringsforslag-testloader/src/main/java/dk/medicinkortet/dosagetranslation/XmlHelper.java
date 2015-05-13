package dk.medicinkortet.dosagetranslation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class XmlHelper {
    private static TransformerFactory transFactory = TransformerFactory.newInstance();
    private static Transformer transformer;
    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder dBuilder;
    static {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            transformer = transFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        } catch (ParserConfigurationException | TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document loadXmlFromString(String xml) throws IOException, SAXException {
        return dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    public static String elementToString(Element element) throws TransformerException {
        StringWriter buffer = new StringWriter();
        element.removeAttribute("xsi:schemaLocation");
        element.removeAttribute("xmlns:xsi");
        transformer.transform(new DOMSource(element), new StreamResult(buffer));
        return buffer.toString();
    }
}
