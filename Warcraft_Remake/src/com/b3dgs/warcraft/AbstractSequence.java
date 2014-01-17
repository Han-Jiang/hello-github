package com.b3dgs.warcraft;

import com.b3dgs.lionengine.Drawable;
import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.drawable.Text;
import com.b3dgs.lionengine.engine.Loader;
import com.b3dgs.lionengine.engine.Sequence;
import com.b3dgs.lionengine.utility.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public abstract class AbstractSequence extends Sequence {

    public static final Color ADVERT = new Color(208, 208, 240, 120);
    private static final Runtime runtime = Runtime.getRuntime();
    private static final long MEGA_BYTE = 1048576L;
    private final Timer memTime;
    private int max, used;
    protected final Text text = Drawable.DRAWABLE.createText(Font.SERIF, 10, Text.NORMAL);
    protected int fpsOffsetX, fpsOffsetY;
    protected boolean lockMouse, fps;
    public final float wide;

    public AbstractSequence(Loader loader) {
        super(loader);
        this.text.setColor(ADVERT);
        this.memTime = new Timer();
        this.lockMouse = false;
        this.fps = false;
        this.fpsOffsetX = 0;
        this.fpsOffsetY = 0;
        float owidth = this.screen.init.widthRef * (this.screen.display.getHeight() / (float) this.screen.init.heightRef);
        this.wide = this.screen.getWide() ? (this.screen.display.getWidth() / owidth) : 1.0f;
        this.used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1048576L);
        this.max = (int) (runtime.maxMemory() / MEGA_BYTE);
        this.memTime.start();
    }

    @Override
    protected void update(float extrp) {
        if (this.keyboard.isPressed(KeyEvent.VK_TAB)) {
            this.fps = true;
        } else {
            this.fps = false;
        }
    }

    @Override
    protected void render(Graphics2D g) {
        this.text.setColor(ADVERT);
        if (this.fps) {
            this.text.draw(g, "FPS = " + this.getFPS(), this.fpsOffsetX, this.fpsOffsetY, Alignment.LEFT);
            if (this.memTime.elapsed(500L)) {
                this.used = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1048576L);
                this.max = (int) (runtime.totalMemory() / MEGA_BYTE);
            }
            this.text.draw(g, "Memory: " + this.used + "MB" + " / " + this.max + "MB", this.fpsOffsetX, this.fpsOffsetY + 12, Alignment.LEFT);
        }
    }

    @Override
    protected void terminate() {
    }

    public void lockMouse(boolean state) {
        this.lockMouse = state;
    }
}
