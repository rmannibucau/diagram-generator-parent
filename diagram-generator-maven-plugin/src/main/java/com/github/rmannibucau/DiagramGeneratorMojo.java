package com.github.rmannibucau;

import com.github.rmannibucau.graph.GraphViewer;
import com.github.rmannibucau.graph.layout.LevelLayout;
import com.github.rmannibucau.listener.CloseWindowWaiter;
import com.github.rmannibucau.loader.spi.FileType;
import com.github.rmannibucau.loader.spi.Loader;
import com.github.rmannibucau.loader.spi.LoaderHelper;
import com.github.rmannibucau.loader.spi.graph.Diagram;
import com.github.rmannibucau.loader.spi.graph.Edge;
import com.github.rmannibucau.loader.spi.graph.Node;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Romain Manni-Bucau
 *
 * This mojo aims to generate a diagram route diagram from camel code/context.
 */
@Mojo(threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM, name = "generate")
public class DiagramGeneratorMojo extends AbstractMojo {
    @Parameter(property = "diagram.input", required = true)
    private String input;

    @Parameter(property = "diagram.viewer", defaultValue = "false")
    private boolean view;

    @Parameter(property = "diagram.width", defaultValue = "640")
    private int width;

    @Parameter(property = "diagram.height", defaultValue = "480")
    private int height;

    @Parameter(property = "diagram.adjust", defaultValue = "true")
    private boolean adjust;

    /**
     * Base output directory for reports.
     */
    @Parameter(property = "diagram.output", defaultValue = "${project.build.directory}/diagram/")
    private File output;

    @Parameter(property = "diagram.type", defaultValue = "camel")
    private String type;

    @Parameter(property = "diagram.fileType", defaultValue = "xml")
    private String fileType;

    @Parameter(property = "diagram.format", defaultValue = "png")
    private String format;

    @Parameter
    private List<String> additionalClasspathElements;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final Loader loader = LoaderHelper.getLoader(type);

        final ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        final ClassLoader cl = classloader();

        Thread.currentThread().setContextClassLoader(cl);

        try {
            final List<Diagram> diagrams = loader.load(input, FileType.valueOf(fileType.toUpperCase()));
            for (Diagram diagram : diagrams) {
                final Dimension outputSize = new Dimension(width, height);
                final LevelLayout layout = new LevelLayout(diagram);
                final VisualizationViewer<Node, Edge> viewer = new GraphViewer(layout);

                layout.setVertexShapeTransformer(viewer.getRenderContext().getVertexShapeTransformer());
                layout.setSize(outputSize);
                layout.setIgnoreSize(adjust);
                layout.reset();
                viewer.setPreferredSize(layout.getSize());
                viewer.setSize(layout.getSize());

                // creating a realized window to be sure the viewer will be able to draw correctly the graph
                final JFrame window = createWindow(viewer, diagram.getName());

                // saving it too
                if (!output.exists()) {
                    output.mkdirs();
                }
                saveView(layout.getSize(), outputSize, diagram.getName(), viewer);

                // viewing the window if necessary
                if (view) {
                    CountDownLatch latch = new CountDownLatch(1);
                    CloseWindowWaiter waiter = new CloseWindowWaiter(latch);
                    window.setVisible(true);
                    window.addWindowListener(waiter);
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        getLog().error("can't await window close event", e);
                    }
                } else {
                    window.dispose();
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    private JFrame createWindow(VisualizationViewer<Node, Edge> viewer, String name) {
        viewer.setBackground(Color.WHITE);

        DefaultModalGraphMouse<Node, Edge> gm = new DefaultModalGraphMouse<Node, Edge>();
        gm.setMode(DefaultModalGraphMouse.Mode.PICKING);
        viewer.setGraphMouse(gm);

        JFrame frame = new JFrame(name + " viewer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout());
        frame.getContentPane().add(viewer);
        frame.pack();

        return frame;
    }

    private void saveView(Dimension currentSize, Dimension desiredSize, String name, VisualizationViewer<Node, Edge> viewer) throws MojoExecutionException {
        BufferedImage bi = new BufferedImage(currentSize.width, currentSize.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        boolean db = viewer.isDoubleBuffered();
        viewer.setDoubleBuffered(false);
        viewer.paint(g);
        viewer.setDoubleBuffered(db);
        if (!currentSize.equals(desiredSize)) {
            double xFactor = desiredSize.width * 1. / currentSize.width;
            double yFactor = desiredSize.height * 1. / currentSize.height;
            double factor = Math.min(xFactor, yFactor);
            getLog().info("optimal size is (" + currentSize.width + ", " + currentSize.height + ")");
            getLog().info("scaling with a factor of " + factor);

            AffineTransform tx = new AffineTransform();
            tx.scale(factor, factor);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage biNew = new BufferedImage((int) (bi.getWidth() * factor), (int) (bi.getHeight() * factor), bi.getType());
            bi = op.filter(bi, biNew);
        }
        g.dispose();

        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(output, name + "." + format));
            if (!ImageIO.write(bi, format, os)) {
                throw new MojoExecutionException("can't save picture " + name + "." + format);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("can't save the diagram", e);
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    throw new MojoExecutionException("can't close diagram", e);
                }
            }
        }
    }

    private ClassLoader classloader() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (additionalClasspathElements == null || additionalClasspathElements.isEmpty()) {
            additionalClasspathElements = new ArrayList<String>();
            additionalClasspathElements.add("target/classes");
        }

        final List<URL> urls = new ArrayList<URL>(additionalClasspathElements.size());
        for (String add : additionalClasspathElements) {
            final File file = new File(add);
            if (file.exists()) {
                try {
                    urls.add(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    getLog().warn("Ignoring '" + add + "'", e);
                }
            } else {
                getLog().warn("Ignoring '" + add + "' since it doesn't exist.");
            }
        }

        return new URLClassLoader(urls.toArray(new URL[urls.size()]), cl);
    }

    public List<String> getAdditionalClasspathElements() {
        return additionalClasspathElements;
    }

    public void setAdditionalClasspathElements(List<String> additionalClasspathElements) {
        this.additionalClasspathElements = additionalClasspathElements;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean getAdjust() {
        return adjust;
    }

    public void setAdjust(boolean adjust) {
        this.adjust = adjust;
    }
}
