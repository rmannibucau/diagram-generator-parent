package fr.rmannibucau.camel.loader;

import fr.rmannibucau.loader.spi.DiagramGeneratorRuntimeException;
import fr.rmannibucau.loader.spi.FileType;
import fr.rmannibucau.loader.spi.Loader;
import fr.rmannibucau.loader.spi.graph.Diagram;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.ClassFinder;
import org.apache.xbean.finder.UrlSet;
import org.apache.xbean.finder.archive.ClasspathArchive;
import org.apache.xbean.finder.archive.FilteredArchive;
import org.apache.xbean.finder.filter.PackageFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Romain Manni-Bucau
 */
public class CamelLoader implements Loader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelLoader.class);

    @Override public List<Diagram> load(String input, FileType fileType) {
        return camelContextToDiagram(input, fileType);
    }

    @Override public String key() {
        return "camel";
    }

    private List<Diagram> camelContextToDiagram(String input, FileType fileType) {
        final List<Diagram> diagrams = new ArrayList<Diagram>();

        final ClassLoader cl = Thread.currentThread().getContextClassLoader();

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

            final Diagram diagram = new Diagram();
            final GraphGenerator graphGenerator = new GraphGenerator(diagram);

            diagram.setName(file.getName().substring(0, file.getName().length() - 4));
            if (diagram.getName().contains("*")) {
                diagram.setName("camel");
            }
            final Collection<SpringCamelContext> contexts = appCtx.getBeansOfType(SpringCamelContext.class).values();
            for (CamelContext ctx : contexts) {
                try {
                    graphGenerator.drawRoutes(ctx.getRouteDefinitions());
                } catch (IOException e) {
                    LOGGER.error("can't draw routes for context '" + ctx.getName() + "'");
                }
            }
            diagrams.add(diagram);
        } else { // java dsl
            final CamelContext context = new DefaultCamelContext();

            final Class<?> clazz;
            try {
                clazz = cl.loadClass(input);
                context.addRoutes((RoutesBuilder) clazz.newInstance());

                final Diagram diagram = new Diagram();
                final GraphGenerator graphGenerator = new GraphGenerator(diagram);
                diagram.setName(clazz.getSimpleName());
                try {
                    graphGenerator.drawRoutes(context.getRouteDefinitions());
                } catch (IOException e) {
                    LOGGER.error("can't draw routes for context '" + context.getName() + "'", e);
                }
                diagrams.add(diagram);
            } catch (Exception e) { // try input as a package
                try {
                    UrlSet set = new UrlSet(cl);
                    set = set.excludeJavaHome();
                    set = set.excludeJavaEndorsedDirs();
                    set = set.excludeJavaExtDirs();

                    final AnnotationFinder finder = new AnnotationFinder(new FilteredArchive(new ClasspathArchive(cl, set.getUrls().toArray(new URL[set.getUrls().size()])), new PackageFilter(input)));
                    finder.link();

                    final List<Class<? extends RouteBuilder>> builders = finder.findSubclasses(RouteBuilder.class);
                    for (Class<? extends RouteBuilder> builderClazz : builders) {
                        int modifiers = builderClazz.getModifiers();
                        if (Modifier.isAbstract(modifiers) || builderClazz.getEnclosingClass() != null) {
                            continue;
                        }

                        final RouteBuilder builder = builderClazz.newInstance();
                        final Diagram diagram = new Diagram();
                        final GraphGenerator graphGenerator = new GraphGenerator(diagram);
                        diagram.setName(builderClazz.getSimpleName());

                        final DefaultCamelContext ctx = new DefaultCamelContext();
                        ctx.addRoutes(builder);
                        graphGenerator.drawRoutes(ctx.getRouteDefinitions());
                        diagrams.add(diagram);
                    }
                } catch (Exception e1) {
                    throw new DiagramGeneratorRuntimeException("can't load routes from package or class", e1);
                }
            }
        }

        if (diagrams.size() == 0) {
            String msg = "can't find route inside " + input;
            if (fileType.equals(FileType.XML)) {
                msg += " directory.";
            } else if (fileType.equals(FileType.JAVA)) {
                msg += " package.";
            }
            throw new DiagramGeneratorRuntimeException(msg, new Exception(msg));
        }

        return diagrams;
    }
}
