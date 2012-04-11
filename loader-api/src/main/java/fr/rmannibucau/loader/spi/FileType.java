package fr.rmannibucau.loader.spi;

/**
 * @author Romain Manni-Bucau
 */
public enum FileType {
    XML, JAVA;

    public String getExtension() {
        return name().toLowerCase();
    }
}
