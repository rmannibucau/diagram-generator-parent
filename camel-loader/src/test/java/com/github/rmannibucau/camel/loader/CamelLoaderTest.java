package com.github.rmannibucau.camel.loader;

import com.github.rmannibucau.loader.spi.FileType;
import com.github.rmannibucau.loader.spi.Loader;
import com.github.rmannibucau.loader.spi.LoaderHelper;
import com.github.rmannibucau.loader.spi.graph.Diagram;
import org.junit.Test;

import java.io.File;

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
        Diagram diagram = loader.load(new File(getClass().getResource("/route.xml").getFile()).getParent(), FileType.XML).iterator().next();
        assertEquals(10, diagram.getVertices().size());
        assertEquals(9, diagram.getEdges().size());
    }

    @Test public void loadJavaFromClass() {
        Loader loader = LoaderHelper.getLoader("camel");
        Diagram diagram = loader.load("com.github.rmannibucau.camel.route.ExampleRouteBuilder", FileType.JAVA).iterator().next();
        assertEquals(6, diagram.getVertices().size());
        assertEquals(5, diagram.getEdges().size());
    }

    @Test public void loadJavaFromPackage() {
        Loader loader = LoaderHelper.getLoader("camel");
        Diagram diagram = loader.load("com.github.rmannibucau.camel.route", FileType.JAVA).iterator().next();
        assertEquals(6, diagram.getVertices().size());
        assertEquals(5, diagram.getEdges().size());
    }
}
