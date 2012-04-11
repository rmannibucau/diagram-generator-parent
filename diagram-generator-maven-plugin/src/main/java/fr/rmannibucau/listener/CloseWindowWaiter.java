package fr.rmannibucau.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

/**
 * @author Romain Manni-Bucau
 */
public class CloseWindowWaiter extends WindowAdapter {
    private CountDownLatch latch;

    public CloseWindowWaiter(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override public void windowClosed(WindowEvent e) {
        latch.countDown();
    }
}
