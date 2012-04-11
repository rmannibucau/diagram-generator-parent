package fr.rmannibucau;

import java.io.File;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * @author Romain Manni-Bucau
 */
public class DiagramGeneratorMojoTest {
    @Test public void execute() throws Exception {
        DiagramGeneratorMojo mojo = new DiagramGeneratorMojo();
        File input = new File(getClass().getResource("/spring/").getFile());

        mojo.setInput(input.getPath());
        mojo.setOutput(input.getParentFile().getParentFile());
        mojo.setType("camel");
        mojo.setFileType("xml");
        mojo.setFormat("png");
        mojo.setWidth(480);
        mojo.setHeight(640);
        mojo.setView(false);

        mojo.execute();

        assertTrue(new File(mojo.getOutput(), "camel.png").exists());
    }
}
