package fr.rmannibucau.camel.loader;

import fr.rmannibucau.loader.spi.FileType;
import fr.rmannibucau.loader.spi.Loader;
import fr.rmannibucau.loader.spi.LoaderHelper;
import fr.rmannibucau.loader.spi.graph.Diagram;
import java.io.File;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Romain Manni-Bucau
 */
public class CamelLoaderTest {
    @Test public void getKey() {
        Loader loader = LoaderHelper.getLoader("camel");
        assertTrue(loader instanceof CamelLoader);
        assertEquals("camel", loader.key());
    }

    @Test public void loadXml() {
        Loader loader = LoaderHelper.getLoader("camel");
        Diagram diagram = loader.load(new File(getClass().getResource("/route.xml").getFile()).getParent(), FileType.XML);
        assertEquals(10, diagram.getVertices().size());
        assertEquals(9, diagram.getEdges().size());
    }

    @Test public void loadJava() {
        Loader loader = LoaderHelper.getLoader("camel");
        Diagram diagram = loader.load("fr.rmannibucau.camel.route.ExampleRouteBuilder", FileType.JAVA);
        assertEquals(6, diagram.getVertices().size());
        assertEquals(5, diagram.getEdges().size());
    }
}
