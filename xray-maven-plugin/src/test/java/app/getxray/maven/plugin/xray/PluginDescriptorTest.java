package app.getxray.maven.plugin.xray;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

// checks the default timeout without waiting for it in the (slow) TimeoutHandlingIT tests
class PluginDescriptorTest {

    @ParameterizedTest
    @ValueSource(strings = { "import-results", "import-features", "export-features" })
    void defaultTimeoutTest(String goal) throws Exception {
        Element configuration = (Element) findMojo(goal).getElementsByTagName("configuration").item(0);
        Element timeout = (Element) configuration.getElementsByTagName("timeout").item(0);
        Assertions.assertNotNull(timeout, "no timeout configuration for goal " + goal);
        Assertions.assertEquals("50", timeout.getAttribute("default-value"));
        Assertions.assertEquals("${xray.timeout}", timeout.getTextContent());
    }

    private static Element findMojo(String goal) throws Exception {
        try (InputStream is = PluginDescriptorTest.class.getResourceAsStream("/META-INF/maven/plugin.xml")) {
            Assertions.assertNotNull(is, "plugin descriptor not found");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            NodeList mojos = doc.getElementsByTagName("mojo");
            for (int i = 0; i < mojos.getLength(); i++) {
                Element mojo = (Element) mojos.item(i);
                if (goal.equals(mojo.getElementsByTagName("goal").item(0).getTextContent())) {
                    return mojo;
                }
            }
        }
        return Assertions.fail("goal not found in plugin descriptor: " + goal);
    }
}
