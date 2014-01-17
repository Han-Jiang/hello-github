package com.b3dgs.warcraft.map;

import com.b3dgs.lionengine.drawable.TiledSprite;
import com.b3dgs.lionengine.game.map.AbstractTiledFogOfWar;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.utility.Media;
import java.awt.Color;
import java.awt.Graphics2D;
import static com.b3dgs.lionengine.Drawable.DRAWABLE;
import static com.b3dgs.warcraft.RessourcesHandler.TILES_DIR;
import static com.b3dgs.warcraft.map.Map.TILE_HEIGHT;
import static com.b3dgs.warcraft.map.Map.TILE_WIDTH;

/**
 * Handle fog of war and undiscovered areas.
 */
public class FogOfWar extends AbstractTiledFogOfWar<Tile> {

    private Map map;

    public FogOfWar() {
        super();
        TiledSprite hide = DRAWABLE.loadTiledSprite(Media.get(TILES_DIR, "hide.png"), TILE_WIDTH, TILE_HEIGHT);
        hide.load(false);
        TiledSprite fog = DRAWABLE.loadTiledSprite(Media.get(TILES_DIR, "fog.png"), TILE_WIDTH, TILE_HEIGHT);
        hide.load(false);
        this.setFogTiles(hide, fog);
    }

    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    protected void onFogChanges(AbstractEntry<?, ?, ?> entry) {
        if (this.hasFogOfWar()) {
            Graphics2D g = this.map.createMiniMapGraphics();
            g.drawImage(this.map.getMiniMap(), 0, 0, null);
            for (int v = 0; v < this.map.getHeightInTiles(); v++) {
                for (int h = 0; h < this.map.getWidthInTiles(); h++) {
                    if (!this.isFogged(v, h)) {
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect(h, v, 1, 1);
                    }
                    if (!this.isVisited(v, h)) {
                        g.setColor(Color.BLACK);
                        g.fillRect(h, v, 1, 1);
                    }
                }
            }
            g.dispose();
        }
    }
}
