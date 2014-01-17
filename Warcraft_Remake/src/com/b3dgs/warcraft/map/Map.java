package com.b3dgs.warcraft.map;

import com.b3dgs.lionengine.file.FileReader;
import com.b3dgs.lionengine.game.map.AbstractPathMap;
import com.b3dgs.lionengine.game.map.Border20;
import com.b3dgs.lionengine.game.map.Border20Map;
import com.b3dgs.warcraft.World;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import static com.b3dgs.warcraft.RessourcesHandler.TILES_DIR;

public class Map extends AbstractPathMap<Tile> {

    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;
    public static String THEME;
    private static final Color[] GROUND0 = {new Color(9, 43, 8), new Color(45, 29, 25)}; // Dark
    private static final Color[] GROUND1 = {new Color(11, 45, 8), new Color(56, 37, 29)}; // Dark-Normal
    private static final Color[] GROUND2 = {new Color(17, 51, 8), new Color(59, 39, 30)}; // Normal
    private static final Color[] GROUND3 = {new Color(19, 52, 8), new Color(63, 42, 31)}; // Normal-Light
    private static final Color[] GROUND4 = {new Color(23, 55, 8), new Color(68, 46, 34)}; // Light
    private static final Color[] GROUND5 = {new Color(23, 59, 6), new Color(74, 50, 35)}; // Light-Bright
    private static final Color[] GROUND6 = {new Color(32, 61, 5), new Color(80, 54, 38)}; // Bright
    private static final Color[] GROUND7 = {new Color(82, 40, 19), new Color(79, 56, 39)}; // Road
    private static final Color[] GROUND8 = {new Color(66, 30, 15), new Color(62, 42, 29)}; // Bridge
    private static final Color[] GROUND9 = {new Color(128, 72, 32), new Color(128, 72, 12)}; // Cut tree
    private static final Color[] TREE_BORDER = {new Color(10, 28, 9), new Color(42, 40, 21)};
    private static final Color[] TREE = {new Color(14, 25, 9), new Color(41, 44, 22)};
    private static final Color[] BORDER = {new Color(35, 52, 85), new Color(85, 69, 32)};
    private static final Color[] WATER = {new Color(27, 40, 101), new Color(59, 63, 0)};
    public final FogOfWar fogOfWar;
    public final Border20Map axis18map;
    private int id;
    private Border20[][] trees;
    private int tcut;

    public Map(World world, FogOfWar fogOfWar) {
        super(TILES_DIR, TILE_WIDTH, TILE_HEIGHT, world.height);
        this.fogOfWar = fogOfWar;
        this.fogOfWar.setMap(this);
        this.axis18map = new Border20Map(true);
        this.id = 0;
    }

    @Override
    public void create(int widthInTile, int heightInTile) {
        super.create(widthInTile, heightInTile);
        this.trees = new Border20[heightInTile][widthInTile];
        this.axis18map.create(this);
        this.tcut = 124;
        THEME = this.theme;
        if (this.theme.equals("swamp")) {
            this.id = 1;
            this.tcut = 143;
        }
    }

    public int getTCut() {
        return this.tcut;
    }

    public void updateTree(Tile tile, boolean check) {
        int tx = tile.getX() / this.getTileWidth();
        int ty = -tile.getY() / this.getTileHeight();
        if (check) {
            this.axis18map.checkAll(this.trees, tx, ty, 0, 0, 1);
        } else {
            this.axis18map.updateExclude(this.trees, tx, ty);
        }
        this.axis18map.finalCheck(this.trees, ty, tx);
        for (int v = ty - 1; v <= ty + 1; v++) {
            for (int h = tx - 1; h <= tx + 1; h++) {
                Tile t = this.getTile(v, h);
                if (t != null) {
                    if (t.getId() != Border20.NONE) {
                        Border20 a = this.axis18map.get(this.trees, v, h);
                        t.setNumber(a);
                    } else {
                        this.axis18map.set(this.trees, v, h, Border20.NONE);
                    }
                }
            }
        }
    }

    public void setAxis(Tile tile, Border20 axis) {
        this.axis18map.set(this.trees, -tile.getY() / this.getTileHeight(), tile.getX() / this.getTileWidth(), axis);
    }

    @Override
    public Tile createTile(int pattern, int number, int x, int y, String collision) {
        return new Tile(pattern, number, x, y, collision);
    }

    @Override
    protected void renderTile(Graphics2D g, Tile tile, int x, int y, int tx, int ty) {
        if (this.fogOfWar.isVisited(ty, tx)) {
            super.renderTile(g, tile, x, y, tx, ty);
        }
    }

    @Override
    public void load(FileReader fr) throws IOException {
        super.load(fr);
        for (int v = 0; v < this.heightInTile; v++) {
            for (int h = 0; h < this.widthInTile; h++) {
                Tile tile = this.getTile(v, h);
                this.trees[v][h] = tile.getId();
            }
        }
    }

    @Override
    protected Color getTilePixelColor(Tile tile) {
        switch (tile.getCollisionEnum()) {
            case GROUND0:
                return GROUND0[this.id];
            case GROUND1:
                return GROUND1[this.id];
            case GROUND2:
                return GROUND2[this.id];
            case GROUND3:
                return GROUND3[this.id];
            case GROUND4:
                return GROUND4[this.id];
            case GROUND5:
                return GROUND5[this.id];
            case GROUND6:
                return GROUND6[this.id];
            case GROUND7:
                return GROUND7[this.id];
            case GROUND8:
                return GROUND8[this.id];
            case GROUND9:
                return GROUND9[this.id];
            case TREE_BORDER:
                return TREE_BORDER[this.id];
            case TREE:
                return TREE[this.id];
            case BORDER:
                return BORDER[this.id];
            case WATER:
                return WATER[this.id];
            default:
                return Color.BLACK;
        }
    }
}
