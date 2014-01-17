package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.drawable.TiledSprite;
import com.b3dgs.lionengine.game.strategy.AbstractControlPanel;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.StrategyCursor;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.building.ModelProductor;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.gameplay.Cost;
import com.b3dgs.warcraft.gameplay.Costs;
import com.b3dgs.warcraft.gameplay.Player;
import com.b3dgs.warcraft.map.Tile;
import java.awt.Color;
import java.awt.Graphics2D;
import static com.b3dgs.warcraft.gameplay.ControlPanel.C_TEXT;
import static com.b3dgs.warcraft.gameplay.ControlPanel.S_GOLD;
import static com.b3dgs.warcraft.gameplay.ControlPanel.S_WOOD;
import static com.b3dgs.warcraft.gameplay.ControlPanel.TEXT;

public abstract class ProdModel extends ModelSkill {

    private static final Color prod_back = new Color(128, 128, 128, 144);
    private static final Color prod = new Color(96, 192, 96, 160);
    private String prodName;
    private final Cost cost;
    private final String desc, gold, infos;

    public ProdModel(String prodName, int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super(prodName, priority, owner);
        this.prodName = prodName;
        this.setOrder(false);
        this.cost = Costs.get(this.prodName);
        this.desc = this.getDisplayName() + "  ";
        this.gold = this.desc + "     " + this.cost.gold.get() + " ";
        this.infos = this.gold + "    " + this.cost.wood.get();
    }

    @Override
    public void renderOnPanel(Graphics2D g, AbstractControlPanel<Tile, ModelSkill, Attributes> panel, StrategyCursor cursor,
            TiledSprite icons, int x, int y, int w, int h) {

        super.renderOnPanel(g, panel, cursor, icons, x, y, w, h);
        int cx = cursor.getScreenX();
        int cy = cursor.getScreenY();
        if (this.alert == null) {
            if (cx >= x && cx <= x + w && cy >= y && cy <= y + h) {
                TEXT.setColor(C_TEXT);
                TEXT.draw(g, this.infos, 72, 192, Alignment.LEFT);
                S_GOLD.render(g, 72 + TEXT.getStringWidth(g, this.desc), 192);
                S_WOOD.render(g, 72 + TEXT.getStringWidth(g, this.gold), 192);
            }
        }
        if (this.owner instanceof ModelProductor) {
            ModelProductor productor = (ModelProductor) this.owner;
            if (this.prodName.equals(productor.getProducingElement())) {
                int p = productor.getProductionProgress();
                if (p >= 0) {
                    int q = productor.getQueueLength() + 1;
                    g.setColor(prod_back);
                    g.fillRect(x, y, w, h);
                    g.setColor(prod);
                    g.fillRect(x, y, (p * w / 100), h);
                    TEXT.setColor(Color.GREEN);
                    if (q > 1) {
                        TEXT.draw(g, Integer.toString(q), x + w - 1, y + h / 2 + 2, Alignment.RIGHT);
                    }
                }
            }
        }
    }

    @Override
    public void action() {
        if (this.owner instanceof ModelProductor) {
            ModelProductor productor = (ModelProductor) this.owner;
            Player player = (Player) Player.get(productor.getOwnerID());
            if (player.getFarmUsed() < player.getFarmGrowth()) {
                if (player.gold.canSpend(this.cost.gold.get()) && player.wood.canSpend(this.cost.wood.get())) {
                    player.gold.spend(this.cost.gold.get());
                    player.wood.spend(this.cost.wood.get());
                    ControlPanel.playSfx(productor.getOwnerID(), null, SFX.click);
                    productor.addToProductionQueue(this.prodName, this.cost.time);
                    this.setAlert(null);
                } else {
                    this.setAlert(ALERT_RSC);
                }
            } else {
                this.setAlert(ALERT_POP);
            }
        }
    }
}
