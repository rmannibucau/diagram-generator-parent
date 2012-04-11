package fr.rmannibucau.loader.spi.graph;

import javax.swing.*;

/**
 * @author Romain Manni-Bucau
 */
public class Node extends Info {
    private Icon icon;

    public Node(String txt) {
        super(txt);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
