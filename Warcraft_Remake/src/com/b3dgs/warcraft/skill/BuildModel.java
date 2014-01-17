package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.drawable.TiledSprite;
import com.b3dgs.lionengine.game.strategy.AbstractControlPanel;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.StrategyCursor;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.gameplay.AI;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.gameplay.Cost;
import com.b3dgs.warcraft.gameplay.Costs;
import com.b3dgs.warcraft.gameplay.Player;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.unit.ModelUnit;
import com.b3dgs.warcraft.unit.ModelWorker;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import static com.b3dgs.warcraft.gameplay.ControlPanel.C_TEXT;
import static com.b3dgs.warcraft.gameplay.ControlPanel.S_GOLD;
import static com.b3dgs.warcraft.gameplay.ControlPanel.S_WOOD;
import static com.b3dgs.warcraft.gameplay.ControlPanel.TEXT;

public class BuildModel extends ModelSkill {

    private static int w, h;
    private static boolean blocked, plan;
    private final String buildName;
    private final Cost cost;
    private final String desc, gold, infos;
    private boolean ai;

    public BuildModel(String name, int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super(name, priority, owner);
        this.buildName = name;
        this.setIgnore(true);
        this.cost = Costs.get(this.buildName);
        this.desc = this.getDisplayName() + "  ";
        this.gold = this.desc + "     " + this.cost.gold.get() + " ";
        this.infos = this.gold + "    " + this.cost.wood.get();
    }

    @Override
    public void onClick() {
        super.onClick();
        w = this.cost.tw;
        h = this.cost.th;
        plan = true;
        this.setActive(true);
    }

    @Override
    public void renderOnPanel(Graphics2D g, AbstractControlPanel<Tile, ModelSkill, Attributes> panel, StrategyCursor cursor,
            TiledSprite icons, int x, int y, int w, int h) {

        super.renderOnPanel(g, panel, cursor, icons, x, y, w, h);
        int cx = cursor.getScreenX();
        int cy = cursor.getScreenY();
        if (cx >= x && cx <= x + w && cy >= y && cy <= y + h) {
            TEXT.setColor(C_TEXT);
            TEXT.draw(g, this.infos, 72, 192, Alignment.LEFT);
            S_GOLD.render(g, 72 + TEXT.getStringWidth(g, this.desc), 192);
            S_WOOD.render(g, 72 + TEXT.getStringWidth(g, this.gold), 192);
        }
    }

    @Override
    public void renderOnMap(Graphics2D g, AbstractControlPanel<Tile, ModelSkill, Attributes> panel, StrategyCursor cursor) {
        if (cursor.canClick(panel) && plan && !this.ai) {
            if (this.owner.map.checkFreePlace(cursor.getHorizontalMapTile(), cursor.getVerticalMapTile(), w, h, this.owner.id)) {
                g.setColor(Color.GREEN);
                blocked = false;
            } else {
                g.setColor(Color.RED);
                blocked = true;
            }
            List<ModelBuilding> lst = ModelBuilding.getByOwner(0);
            for (ModelBuilding e : lst) {
                if (e.isActive() && e.getDistance(cursor.getHorizontalMapTile(), cursor.getVerticalMapTile(), w, h) < 6) {
                    g.setColor(Color.RED);
                    blocked = true;
                    break;
                }
            }
            int x = (cursor.getScreenX() - 8) / Map.TILE_WIDTH * Map.TILE_WIDTH + 8;
            int y = (cursor.getScreenY() + 4) / Map.TILE_HEIGHT * Map.TILE_HEIGHT - 4;
            g.drawRect(x, y, w * Map.TILE_WIDTH - 1, h * Map.TILE_HEIGHT - 1);
        }
    }

    @Override
    public void action() {
        if (this.owner instanceof ModelWorker) {
            this.ai = (((ModelUnit) owner).player() instanceof AI);
            if (!blocked || this.ai) {
                ModelWorker worker = (ModelWorker) this.owner;
                worker.stop();
                Player player = worker.player();
                if (!worker.isConstructing() && player.gold.canSpend(this.cost.gold.get()) && player.wood.canSpend(this.cost.wood.get())) {
                    worker.buildAt(this.destX, this.destY, this.buildName, this.cost.tw, cost.th, this.cost.time);
                    player.incWorkersOnConstructing(1);
                    ControlPanel.playSfx(worker.getOwnerID(), null, SFX.valided);
                    this.setActive(false);
                    if (!this.ai) {
                        plan = false;
                    }
                }
            } else {
                this.setRetry(true);
            }
        }
    }
}
