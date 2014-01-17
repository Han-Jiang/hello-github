package com.b3dgs.warcraft;

import com.b3dgs.lionengine.Audio;
import com.b3dgs.lionengine.audio.Midi;
import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.engine.Loader;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.utility.Media;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.gameplay.Costs;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.unit.ModelUnit;
import java.awt.Graphics2D;
import static com.b3dgs.warcraft.RessourcesHandler.LEVELS_DIR;
import static com.b3dgs.warcraft.RessourcesHandler.MUSICS_DIR;

public final class Scene extends AbstractSequence {

    private World world;
    private Midi music;

    public Scene(Loader loader) {
        super(loader);
        this.fpsOffsetX = 72;
        this.fpsOffsetY = 12;
    }

    @Override
    protected void load() {
        ModelUnit.clear();
        ModelBuilding.clear();
        Costs.initializeCosts(Media.get("costs.xml"));
        this.world = new World(this, this.screen);
        this.world.loadFromFile(Media.get(LEVELS_DIR, GameConfig.getMap()));
    }

    @Override
    protected void update(float extrp) {
        super.update(1.0f);
        this.world.update(this.keyboard, this.mouse, 1.0f);
        if (this.keyboard.isPressed(Keyboard.ESCAPE)) {
            this.music.stop();
            this.end(new Menu(this.loader));
        }
    }

    @Override
    protected void render(Graphics2D g) {
        this.world.render(g);
        if (this.fps) {
            this.text.draw(g, "Coord = [" + (this.world.camera.getX() / Map.TILE_WIDTH + 4) + " | " + this.world.camera.getY() / Map.TILE_HEIGHT + "]", this.fpsOffsetX, this.fpsOffsetY + 24, Alignment.LEFT);
        }
        this.text.setColor(ADVERT);
        this.text.draw(g, Main.PROGRAM + " " + Main.VERSION, (int) (315 * this.wide) - 2, 181, Alignment.RIGHT);
        this.text.draw(g, "www.b3dgs.com", 74, 181, Alignment.LEFT);
        super.render(g);
    }

    public void playMusic(String file) {
        this.music = Audio.AUDIO.loadMidi(Media.get(MUSICS_DIR, file));
        if (file.equals("humans.mid")) {
            this.music.setLoop(600, this.music.getTicks() - 760);
        } else {
            this.music.setLoop(13000, this.music.getTicks() - 575);
        }
        this.music.play(true);
    }
}
