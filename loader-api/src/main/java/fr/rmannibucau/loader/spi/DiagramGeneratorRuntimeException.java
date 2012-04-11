package fr.rmannibucau.loader.spi;

/**
 * @author Romain Manni-Bucau
 */
public class DiagramGeneratorRuntimeException extends RuntimeException {
    public DiagramGeneratorRuntimeException(String str, Throwable thr) {
        super(str, thr);
    }
}
