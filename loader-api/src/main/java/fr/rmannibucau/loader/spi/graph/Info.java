package fr.rmannibucau.loader.spi.graph;

/**
 * @author Romain Manni-Bucau
 */
public abstract class Info {
    private String text;

    public Info(String txt) {
        text = txt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override public String toString() {
        return new StringBuilder(getClass().getSimpleName())
            .append("{").append(getText()).append("}").toString();
    }
}
