package com.b3dgs.warcraft;

import com.b3dgs.lionengine.Drawable;
import com.b3dgs.lionengine.File;
import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.core.Screen;
import com.b3dgs.lionengine.drawable.Text;
import com.b3dgs.lionengine.file.FileReader;
import com.b3dgs.lionengine.file.FileWriter;
import com.b3dgs.lionengine.file.XMLNode;
import com.b3dgs.lionengine.game.AbstractWorld;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.StrategyCamera;
import com.b3dgs.lionengine.game.strategy.StrategyCursor;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.Displays;
import com.b3dgs.lionengine.utility.LevelRipConverter;
import com.b3dgs.lionengine.utility.Media;
import com.b3dgs.warcraft.building.BuildingType;
import com.b3dgs.warcraft.building.GoldMine;
import com.b3dgs.warcraft.gameplay.AI;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.gameplay.Minimap;
import com.b3dgs.warcraft.gameplay.Player;
import com.b3dgs.warcraft.gameplay.Race;
import com.b3dgs.warcraft.map.FogOfWar;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static com.b3dgs.warcraft.RessourcesHandler.SPRITES_DIR;

/**
 * So much imports, but don't forget that this is the class which handle everything !
 * You will find here all calls to update and render functions... including initialisation.
 */
public final class World extends AbstractWorld {

    private static final int MAX_PLAYERS = 2;
    private static final Color advert = new Color(255, 255, 255, 80);
    private final Map map;
    private final FogOfWar fogOfWar;
    public final StrategyCamera camera;
    private final Scene scene;
    private final StrategyCursor cursor;
    private final EntryHandler entrys;
    private final RessourcesHandler rsc;
    private final EntryFactory factory;
    private final ControlPanel panel;
    private final Player[] player;
    private final Minimap minimap;
    private final Text text;

    public World(Scene sequence, Screen screen) {
        super(screen);
        this.scene = sequence;
        this.fogOfWar = new FogOfWar();
        this.map = new Map(this, this.fogOfWar);
        this.cursor = new StrategyCursor(screen, Media.get(SPRITES_DIR, "cursor.png"), this.map);
        this.rsc = new RessourcesHandler();
        this.panel = new ControlPanel(GameConfig.getPlayerRace());
        this.camera = new StrategyCamera(screen, this.panel, this.map.getTileWidth(), this.map.getTileHeight());
        this.entrys = new EntryHandler(this.panel, this.map);
        this.factory = new EntryFactory(this.map, this.entrys, this.rsc);
        this.camera.setOffsets(this.panel);
        this.panel.setEntryHandler(this.entrys);
        this.player = new Player[MAX_PLAYERS];
        this.player[0] = new Player("Player1", GameConfig.getPlayerRace(), this.map, this.factory, this.entrys);
        this.player[1] = new AI("Player2", GameConfig.getOpponentRace(), this.map, this.factory, this.entrys);
        this.panel.setPlayer(this.player[0]);
        this.entrys.setPlayer(this.player[0]);
        this.minimap = new Minimap(this.map, this.cursor, this.camera, this.entrys);
        this.minimap.setPlayer(this.player[0]);
        this.text = Drawable.DRAWABLE.createText(Font.DIALOG, 9, Text.NORMAL);
        if (sequence != null) {
            sequence.playMusic(GameConfig.getPlayerRace() + ".mid");
        }
        //this.importLevel("level.png", "lake.map");
    }

    @Override
    public void update(Keyboard keyboard, Mouse mouse, float extrp) {
        this.camera.update(keyboard, mouse, extrp);
        this.cursor.update(mouse, false, this.camera, extrp);
        this.entrys.update(keyboard, this.cursor, this.camera, extrp);
        for (int i = 0; i < 2; i++) {
            this.player[i].update(extrp);
        }
        this.minimap.update(3, 6);
    }

    @Override
    protected void render(Graphics2D g) {
        this.map.render(g, this.camera, this.mapViewH - 1, this.mapViewV - 1);
        this.entrys.render(g, this.camera, this.cursor);
        this.fogOfWar.render(g, this.camera, this.mapViewH - 1, this.mapViewV - 1);
        this.entrys.renderCursorSelection(g, this.camera);
        this.panel.render(g, this.cursor);
        this.player[0].render(g);
        this.minimap.render(g, 3, 6);
        this.cursor.render(g);
        this.text.setColor(advert);
        this.text.draw(g, "Powered by LionEngine", 320, 192, Alignment.RIGHT);
    }

    @Override
    public void save(FileWriter file) throws IOException {
    }

    @Override
    public void load(FileReader file) throws IOException {
        this.map.load(file);
        this.fogOfWar.create(this.map);
        this.map.createMiniMap();
        this.entrys.createLayers(this.map);
        this.calculateMapView(this.map, this.panel);
        this.minimap.setView(this.mapViewH, this.mapViewV);
        this.camera.setBorders(this.map, this.panel);

        String data = file.getFileName().substring(0, file.getFileName().length() - 4) + ".txt";
        BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(Media.getTempFile(data, true))));
        String s = fr.readLine();
        if (s.charAt(0) == '{') {
            fr.readLine();
            this.player[0].setStartingPoint(Integer.parseInt(fr.readLine()), Integer.parseInt(fr.readLine()));
            fr.readLine();
            this.player[1].setStartingPoint(Integer.parseInt(fr.readLine()), Integer.parseInt(fr.readLine()));
            s = fr.readLine();
            while (s.charAt(0) == '+') {
                this.addGoldMine(Integer.parseInt(fr.readLine()), Integer.parseInt(fr.readLine()), Integer.parseInt(fr.readLine()));
                s = fr.readLine();
                if (s.charAt(0) == '}') {
                    break;
                }
            }
        }

        this.player[0].setAttack(this.player[1].getStartX(), this.player[1].getStartY());
        this.player[1].setAttack(this.player[0].getStartX(), this.player[0].getStartY());
        for (int i = 0; i < 2; i++) {
            this.player[i].init();
        }
        this.camera.place((this.player[0].getStartX() - this.mapViewH / 2) * Map.TILE_WIDTH,
                (this.player[0].getStartY() - this.mapViewV / 2) * Map.TILE_HEIGHT);

        this.entrys.update(null, this.cursor, this.camera, 1.0f);
        this.fogOfWar.setOwner(this.entrys.list(), this.player[0].id);
        this.fogOfWar.setFogOfWar(GameConfig.getHide(), GameConfig.getFog());
    }

    public void addNeutralEntry(BuildingType type, int x, int y) {
        AbstractEntry<Tile, ModelSkill, Attributes> entry = this.factory.createBuilding(BuildingType.GOLDMINE, Race.neutral);
        entry.setOwnerID(0);
        this.entrys.add(entry);
        entry.place(x, y);
    }

    public void addGoldMine(int x, int y, int gold) {
        GoldMine mine = this.factory.createGoldMine();
        mine.setOwnerID(0);
        mine.gold.add(gold);
        this.entrys.add(mine);
        mine.place(x, y);
    }

    @Override
    protected void save(XMLNode world) {
    }

    @Override
    protected void load(XMLNode world) {
    }

    private void importLevel(String levelrip, String out) {
        LevelRipConverter<Tile> lrc = new LevelRipConverter<Tile>();
        lrc.start(Media.get("levels", levelrip), this.map, "tiles", "forest", false);
        try {
            FileWriter fw = File.FILE.createFileWriter(Media.get("levels", out));
            this.map.save(fw);
            fw.close();
        } catch (IOException ex) {
            Displays.error("importLevel", "Error during map import !");
        }
    }
}
