package fr.rmannibucau.camel.loader;

import fr.rmannibucau.loader.spi.DiagramGeneratorRuntimeException;
import fr.rmannibucau.loader.spi.FileType;
import fr.rmannibucau.loader.spi.Loader;
import fr.rmannibucau.loader.spi.graph.Diagram;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Romain Manni-Bucau
 */
public class CamelLoader implements Loader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelLoader.class);

    @Override public Diagram load(String input, FileType fileType) {
        return camelContextToDiagram(input, fileType);
    }

    @Override public String key() {
        return "camel";
    }

    private Diagram camelContextToDiagram(String input, FileType fileType) {
        final Diagram diagram = new Diagram();
        final GraphGenerator graphGenerator = new GraphGenerator(diagram);

        final List<CamelContext> contexts = new ArrayList<CamelContext>();

        final ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        final ClassLoader cl = classloader();

        Thread.currentThread().setContextClassLoader(cl);

        try {
            if (fileType.equals(FileType.XML)) { // spring dsl
                File file = new File(input.replace("%20", " "));
                if (!file.exists() || file.isDirectory()) {
                    file = new File(file, "*." + fileType.getExtension());
                }
                ApplicationContext appCtx;
                try {
                    appCtx = new FileSystemXmlApplicationContext(file.toURI().toURL().toString());
                } catch (MalformedURLException e) {
                    String msg = "can't load context file";
                    LOGGER.error(msg, e);
                    throw new DiagramGeneratorRuntimeException(msg, e);
                }

                diagram.setName(file.getName().substring(0, file.getName().length() - 4));
                if (diagram.getName().contains("*")) {
                    diagram.setName("camel");
                }
                contexts.addAll(appCtx.getBeansOfType(SpringCamelContext.class).values());
            } else { // java dsl
                final CamelContext context = new DefaultCamelContext();

                final Class<?> clazz;
                try {
                    clazz = cl.loadClass(input);
                    context.addRoutes((RoutesBuilder) clazz.newInstance());
                } catch (Exception e) {
                    throw new DiagramGeneratorRuntimeException("can't load route class", e);
                }
                diagram.setName(clazz.getSimpleName());
                contexts.add(context);
            }

            if (contexts.size() == 0) {
                String msg = "can't find route inside " + input;
                if (fileType.equals(FileType.XML)) {
                    msg += " directory.";
                } else if (fileType.equals(FileType.JAVA)) {
                    msg += " package.";
                }
                throw new DiagramGeneratorRuntimeException(msg, new Exception(msg));
            }

            for (CamelContext ctx : contexts) {
                try {
                    graphGenerator.drawRoutes(ctx);
                } catch (IOException e) {
                    String msg = "can't generate the graph";
                    LOGGER.error(msg, e);
                    throw new DiagramGeneratorRuntimeException(msg, e);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }

        return diagram;
    }

    private ClassLoader classloader() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final File target = new File("target/classes");
        final File targetTest = new File("target/test-classes");
        if (target.exists() && targetTest.exists()) {
            try {
                return new URLClassLoader(new URL[] { target.toURI().toURL(), targetTest.toURI().toURL() }, cl);
            } catch (MalformedURLException e) {
                return cl;
            }
        } else if (target.exists()) {
            try {
                return new URLClassLoader(new URL[] { target.toURI().toURL() }, cl);
            } catch (MalformedURLException e) {
                return cl;
            }
        }
        return cl;
    }
}
