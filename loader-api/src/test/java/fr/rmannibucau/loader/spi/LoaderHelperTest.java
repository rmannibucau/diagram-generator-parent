package fr.rmannibucau.loader.spi;

import fr.rmannibucau.loader.test.LoaderImpl;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Romain Manni-Bucau
 */
public class LoaderHelperTest {
    @Test public void loadOK() {
        Loader loader = LoaderHelper.getLoader("test");
        assertTrue(loader instanceof LoaderImpl);
        assertEquals("test", loader.key());
    }

    @Test(expected = DiagramGeneratorRuntimeException.class) public void loadKO() {
        Loader loader = LoaderHelper.getLoader("does not exist");
    }
}
