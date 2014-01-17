package com.b3dgs.warcraft;

import com.b3dgs.lionengine.Audio;
import com.b3dgs.lionengine.audio.Midi;
import com.b3dgs.lionengine.audio.Sound;
import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.drawable.Cursor;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.Text;
import com.b3dgs.lionengine.drawable.TiledSprite;
import com.b3dgs.lionengine.engine.Loader;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.Maths;
import com.b3dgs.lionengine.utility.Media;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.gameplay.Race;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import static com.b3dgs.lionengine.Drawable.DRAWABLE;
import static com.b3dgs.lionengine.utility.Maths.fixBetween;
import static com.b3dgs.warcraft.RessourcesHandler.MUSICS_DIR;
import static com.b3dgs.warcraft.RessourcesHandler.SPRITES_DIR;

public class Menu extends AbstractSequence {

    private static final Text FONT = DRAWABLE.createText(Font.DIALOG, 10, Text.NORMAL);
    private static final Color COLOR_HEAD = new Color(255, 244, 69);
    private static final Color COLOR = new Color(255, 255, 235);
    private static final Color COLOR_OVER = new Color(255, 245, 70);
    private static final Color COLOR_BOX_BORDER = new Color(190, 190, 190);
    private static final Color COLOR_BOX_IN = new Color(20, 48, 77);
    private static final Race[] RACES = {Race.humans, Race.orcs};
    private static final String[] MAPS = {"2_bridges.map", "lake.map"};
    private static final String[] FOGS = {"Revealed", "Hidden"};
    private static MENUS MENU = MENUS.INTRO_UP;
    private static boolean clicked;

    private static enum MENUS {

        INTRO_UP, INTRO_DOWN, MAIN_UP, MAIN, NEW, NEW_OUT, LOAD, ABOUT, PLAY, EXIT
    }

    private static class Button {

        private final TiledSprite surface;
        private final String text;
        private final int x, y, w, h;
        private final MENUS menu;
        private boolean over;

        private Button(TiledSprite surface, String text, int x, int y, MENUS menu) {
            this.surface = surface;
            this.text = text;
            this.x = x;
            this.y = y;
            this.w = surface.getTileWidth();
            this.h = surface.getTileHeight();
            this.menu = menu;
            this.over = false;
        }

        private void update(Cursor cursor) {
            int cx = cursor.getX();
            int cy = cursor.getY();
            this.over = (cx >= this.x && cy >= this.y && cx <= this.x + this.w && cy <= this.y + this.h);
            if (!clicked && this.over && cursor.getClick() == Mouse.LEFT) {
                ControlPanel.playSfx(SFX.click);
                clicked = true;
                MENU = this.menu;
                this.over = false;
            }
        }

        private void render(Graphics2D g) {
            if (this.over) {
                this.surface.render(g, 1, this.x, this.y);
                FONT.setColor(COLOR_OVER);
            } else {
                this.surface.render(g, 0, this.x, this.y);
                FONT.setColor(COLOR);
            }
            FONT.setColor(COLOR);
            FONT.draw(g, this.text, this.x + this.w / 2, this.y + 4, Alignment.CENTERED);
            FONT.setColor(COLOR_HEAD);
            FONT.draw(g, this.text.substring(0, 1), this.x + this.w / 2 - FONT.getStringWidth(g, this.text) / 2, this.y + 4, Alignment.LEFT);
        }
    }

    private static class Choice {

        private final TiledSprite surface;
        private final int x, y, w, h;
        private boolean right, over;

        private Choice(TiledSprite surface, int x, int y, boolean right) {
            this.surface = surface;
            this.x = x;
            this.y = y;
            this.w = surface.getTileWidth();
            this.h = surface.getTileHeight();
            this.right = right;
            this.over = false;
        }

        private boolean update(Cursor cursor) {
            int cx = cursor.getX();
            int cy = cursor.getY();
            boolean pressed = false;
            this.over = (cx >= this.x && cy >= this.y && cx <= this.x + this.w && cy <= this.y + this.h);
            if (!clicked && this.over && cursor.getClick() == Mouse.LEFT) {
                ControlPanel.playSfx(SFX.click);
                pressed = true;
                clicked = true;
            }
            return pressed;
        }

        private void render(Graphics2D g) {
            int a = 0;
            if (this.right) {
                a += 2;
            }
            if (this.over) {
                this.surface.render(g, 1 + a, this.x, this.y);
            } else {
                this.surface.render(g, 0 + a, this.x, this.y);
            }
        }

        private int getSide() {
            return this.right ? 1 : -1;
        }
    }
    private Midi music;
    private Sprite logo, background;
    private Cursor cursor;
    private Button[] buttons;
    private Choice[] choices;
    private Color[] alphas;
    private int alpha, playerRace, opponentRace, map, fog;
    private long introTimer;
    private boolean pressed, end;

    public Menu(Loader loader) {
        super(loader);
    }

    @Override
    protected void load() {
        this.alphas = new Color[256];
        for (int i = 0; i < 256; i++) {
            this.alphas[i] = new Color(0, 0, 0, i);
        }
        this.music = Audio.AUDIO.loadMidi(Media.get(MUSICS_DIR, "menu.mid"));
        this.logo = DRAWABLE.loadSprite(Media.get(SPRITES_DIR, "blizzard.png"));
        this.logo.load(false);
        this.background = DRAWABLE.loadSprite(Media.get(SPRITES_DIR, "menu.png"));
        this.background.load(false);
        this.cursor = DRAWABLE.createCursor(this.screen, Media.get(SPRITES_DIR, "cursor.png"));
        this.cursor.place(this.screen.init.widthRef / 2, this.screen.init.heightRef / 2);
        this.buttons = new Button[7];

        TiledSprite bigButton = DRAWABLE.loadTiledSprite(Media.get(SPRITES_DIR, "case1.png"), 112, 16);
        bigButton.load(false);
        TiledSprite smallButton = DRAWABLE.loadTiledSprite(Media.get(SPRITES_DIR, "case2.png"), 54, 16);
        smallButton.load(false);
        TiledSprite arrowButton = DRAWABLE.loadTiledSprite(Media.get(SPRITES_DIR, "case3.png"), 15, 15);
        arrowButton.load(false);

        this.buttons[0] = new Button(bigButton, "Start a new game", 104, 93, MENUS.NEW);
        this.buttons[1] = new Button(bigButton, "Load existing game", 104, 111, MENUS.MAIN);
        this.buttons[2] = new Button(bigButton, "About", 104, 129, MENUS.ABOUT);
        this.buttons[3] = new Button(bigButton, "Quit game", 104, 163, MENUS.EXIT);

        this.buttons[4] = new Button(smallButton, "Ok", 84, 170, MENUS.NEW_OUT);
        this.buttons[5] = new Button(smallButton, "Cancel", 183, 170, MENUS.MAIN);

        this.buttons[6] = new Button(smallButton, "Back", 133, 170, MENUS.MAIN);

        this.choices = new Choice[8];
        this.choices[0] = new Choice(arrowButton, 142, 100, false);
        this.choices[1] = new Choice(arrowButton, 218, 100, true);
        this.choices[2] = new Choice(arrowButton, 142, 116, false);
        this.choices[3] = new Choice(arrowButton, 218, 116, true);
        this.choices[4] = new Choice(arrowButton, 142, 132, false);
        this.choices[5] = new Choice(arrowButton, 218, 132, true);
        this.choices[6] = new Choice(arrowButton, 142, 148, false);
        this.choices[7] = new Choice(arrowButton, 218, 148, true);

        this.playerRace = 0;
        this.opponentRace = 1;
        this.map = 0;
        this.alpha = 0;
        this.end = false;
        this.fog = 0;
        this.fpsOffsetY = 80;
        clicked = false;
        if (MENU == MENUS.INTRO_UP) {
            Sound sound = Audio.AUDIO.loadSound(Media.get(RessourcesHandler.SFX_DIR, "blizzard.wav"));
            sound.play();
        }
        if (this.keyboard.used()) {
            this.pressed = true;
        }
        this.introTimer = Maths.time();
    }

    @Override
    protected void update(float extrp) {
        super.update(1.0f);
        this.cursor.update(this.mouse, false, 1.0f);
        if (this.cursor.getClick() == 0) {
            clicked = false;
        }
        if (!this.keyboard.used()) {
            this.pressed = false;
        }
        switch (MENU) {
            case INTRO_UP:
                this.alpha += 3;
                this.alpha = fixBetween(this.alpha, 0, 255);
                if (Maths.time() - this.introTimer > 3000) {
                    MENU = MENUS.INTRO_DOWN;
                }
                break;
            case INTRO_DOWN:
                this.alpha -= 10;
                this.alpha = fixBetween(this.alpha, 0, 255);
                if (this.alpha == 0) {
                    MENU = MENUS.MAIN_UP;
                }
                break;
            case MAIN_UP:
                this.alpha += 10;
                this.alpha = fixBetween(this.alpha, 0, 255);
                if (this.alpha == 255) {
                    this.music.setLoop(6300, this.music.getTicks() - 3680);
                    this.music.play(true);
                    MENU = MENUS.MAIN;
                }
                break;
            case MAIN:
                for (int i = 0; i < 4; i++) {
                    this.buttons[i].update(this.cursor);
                }
                break;
            case NEW:
                for (int i = 0; i < 8; i++) {
                    if (this.choices[i].update(this.cursor)) {
                        if (Math.floor(i / 2) == 0) {
                            this.playerRace = fixBetween(this.playerRace + this.choices[i].getSide(), 0, RACES.length - 1);
                            if (this.playerRace == 0) {
                                this.opponentRace = 1;
                            } else if (this.playerRace == 1) {
                                this.opponentRace = 0;
                            }
                        } else if (Math.floor(i / 2) == 1) {
                            this.opponentRace = fixBetween(this.opponentRace + this.choices[i].getSide(), 0, RACES.length - 1);
                            if (this.opponentRace == 0) {
                                this.playerRace = 1;
                            } else if (this.opponentRace == 1) {
                                this.playerRace = 0;
                            }
                        } else if (Math.floor(i / 2) == 2) {
                            this.map = fixBetween(this.map + this.choices[i].getSide(), 0, MAPS.length - 1);
                        } else if (Math.floor(i / 2) == 3) {
                            this.fog = fixBetween(this.fog + this.choices[i].getSide(), 0, FOGS.length - 1);
                        }
                    }
                }
                for (int i = 4; i < 6; i++) {
                    this.buttons[i].update(this.cursor);
                }
                break;
            case ABOUT:
                this.buttons[6].update(this.cursor);
                break;
            case NEW_OUT:
                this.alpha -= 10;
                this.alpha = fixBetween(this.alpha, 0, 255);
                if (this.alpha == 0) {
                    MENU = MENUS.PLAY;
                }
                break;
            case PLAY:
                boolean hide = false;
                boolean ffog = false;
                if (this.fog >= 1) {
                    hide = true;
                }
                if (this.fog == 2) {
                    ffog = true;
                }
                GameConfig.setConfig(RACES[this.playerRace], RACES[this.opponentRace], MAPS[this.map], hide, ffog);
                this.music.stop();
                MENU = MENUS.MAIN_UP;
                this.alpha = 0;
                this.end(new Scene(this.loader));
                break;
            case EXIT:
                this.music.stop();
                this.end();
                break;
        }
        if (!this.pressed && this.keyboard.isPressed(Keyboard.ESCAPE)) {
            this.music.stop();
            this.end();
        }
    }

    @Override
    protected void render(Graphics2D g) {
        if (this.end) {
            return;
        }
        switch (MENU) {
            case INTRO_UP:
                this.logo.render(g, 0, 0);
                this.applyAlpha(g, this.alphas[255 - this.alpha]);

                break;
            case INTRO_DOWN:
                this.logo.render(g, 0, 0);
                this.applyAlpha(g, this.alphas[255 - this.alpha]);
                break;
            case MAIN_UP:
                this.background.render(g, 0, 0);
                this.applyAlpha(g, this.alphas[255 - this.alpha]);
                break;
            case MAIN:
                this.background.render(g, 0, 0);
                for (int i = 0; i < 4; i++) {
                    this.buttons[i].render(g);
                }
                this.cursor.render(g);
                break;
            case NEW:
                this.drawNew(g);
                this.cursor.render(g);
                break;
            case ABOUT:
                this.background.render(g, 0, 0);
                g.setColor(COLOR_BOX_BORDER);
                g.drawRect(80, 84, 160, 71);
                g.setColor(COLOR_BOX_IN);
                g.fillRect(81, 85, 159, 70);
                FONT.setColor(COLOR_HEAD);
                FONT.draw(g, Main.PROGRAM + " v" + Main.VERSION, 160, 89, Alignment.CENTERED);
                FONT.draw(g, "Author:", 115, 110, Alignment.CENTERED);
                FONT.draw(g, "Graphics:", 115, 120, Alignment.CENTERED);
                FONT.draw(g, "Musics:", 115, 130, Alignment.CENTERED);
                FONT.draw(g, "Website:", 115, 140, Alignment.CENTERED);
                FONT.setColor(COLOR);
                FONT.draw(g, "Pierre-Alexandre", 185, 110, Alignment.CENTERED);
                FONT.draw(g, "Blizzard (c)", 185, 120, Alignment.CENTERED);
                FONT.draw(g, "Blizzard (c)", 185, 130, Alignment.CENTERED);
                FONT.draw(g, "www.b3dgs.com", 185, 140, Alignment.CENTERED);
                this.buttons[6].render(g);
                this.cursor.render(g);
                break;
            case NEW_OUT:
                this.drawNew(g);
                this.applyAlpha(g, this.alphas[255 - this.alpha]);
                break;
            case PLAY:
                this.applyAlpha(g, Color.BLACK);
                break;
        }
        if (MENU.ordinal() > 1) {
            this.text.setColor(ADVERT);
            this.text.draw(g, Main.PROGRAM + " " + Main.VERSION, (int) (320 * this.wide) - 2, 0, Alignment.RIGHT);
            this.text.draw(g, "www.b3dgs.com", 2, 0, Alignment.LEFT);
            super.render(g);
        }
    }

    private void applyAlpha(Graphics2D g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, this.screen.init.widthRef, this.screen.init.heightRef);
    }

    private void drawNew(Graphics2D g) {
        this.background.render(g, 0, 0);
        g.setColor(COLOR_BOX_BORDER);
        g.drawRect(80, 84, 160, 82);
        g.setColor(COLOR_BOX_IN);
        g.fillRect(81, 85, 159, 81);
        FONT.setColor(COLOR_HEAD);
        FONT.draw(g, "Select game type", 160, 89, Alignment.CENTERED);
        FONT.setColor(COLOR);
        FONT.draw(g, "Race:", 136, 104, Alignment.RIGHT);
        FONT.draw(g, this.format(RACES[this.playerRace]), 188, 104, Alignment.CENTERED);

        FONT.draw(g, "Opponent:", 136, 120, Alignment.RIGHT);
        FONT.draw(g, this.format(RACES[this.opponentRace]), 188, 120, Alignment.CENTERED);

        FONT.draw(g, "Map:", 136, 136, Alignment.RIGHT);
        FONT.draw(g, this.format(MAPS[this.map], true), 188, 136, Alignment.CENTERED);

        FONT.draw(g, "View:", 136, 152, Alignment.RIGHT);
        FONT.draw(g, FOGS[this.fog], 188, 152, Alignment.CENTERED);

        for (int i = 4; i < 6; i++) {
            this.buttons[i].render(g);
        }
        for (int i = 0; i < 8; i++) {
            this.choices[i].render(g);
        }
    }

    private String format(Race name) {
        return this.format(name.name(), false);
    }

    private String format(String name, boolean ext) {
        String str = name.substring(0, 1).toUpperCase().concat(name.substring(1).toLowerCase());
        if (ext) {
            str = str.substring(0, str.length() - 4);
            str = str.replace('_', ' ');
        }
        return str;
    }

    @Override
    protected void terminate() {
        super.terminate();
        this.end = true;
        this.music = null;
        this.logo = null;
        this.background = null;
        this.cursor = null;
        for (int i = 0; i < this.buttons.length; i++) {
            this.buttons[i] = null;
        }
        for (int i = 0; i < this.choices.length; i++) {
            this.choices[i] = null;
        }
        this.buttons = null;
        this.choices = null;
    }
}
