import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class MainMIDlet extends MIDlet {
    private Display display;
    private GameCanvas gameCanvas;

    public MainMIDlet() {
        display = Display.getDisplay(this);
        gameCanvas = new GameCanvas(this);
    }

    protected void startApp() {
        display.setCurrent(gameCanvas);
        gameCanvas.startThread();
    }

    protected void pauseApp() {
        gameCanvas.stopThread();
    }

    protected void destroyApp(boolean unconditional) {
        gameCanvas.stopThread();
    }

    public void salir() {
        destroyApp(true);
        notifyDestroyed();
    }
}

