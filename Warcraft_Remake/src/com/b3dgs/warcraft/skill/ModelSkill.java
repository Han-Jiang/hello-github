package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.drawable.TiledSprite;
import com.b3dgs.lionengine.game.strategy.AbstractControlPanel;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractSkill;
import com.b3dgs.lionengine.game.strategy.StrategyCursor;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.Maths;
import com.b3dgs.lionengine.utility.Media;
import com.b3dgs.warcraft.gameplay.Race;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.unit.ModelUnit;
import java.awt.Color;
import java.awt.Graphics2D;
import static com.b3dgs.warcraft.gameplay.ControlPanel.C_TEXT;
import static com.b3dgs.warcraft.gameplay.ControlPanel.TEXT;

public abstract class ModelSkill extends AbstractSkill<Tile, ModelSkill, Attributes> {

    protected static final int ICON_FACTION_INC = 45;
    protected static final String ALERT_RSC = "Not enough ressources, extract more !";
    protected static final String ALERT_POP = "Not enough food, build more farms !";
    private static final Color LOCKED = new Color(128, 64, 64, 192);
    private static final long ALTER_TIME = 3000L;
    protected String alert;
    protected long alertTimer;
    protected int icon;

    public ModelSkill(String name, int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super(Media.get("skills.txt"), name, priority, owner);
        this.icon = this.getIcon();
        this.alert = null;
    }

    @Override
    public void updateOnMap(float extrp) {
    }

    @Override
    public void updateOnPanel(AbstractControlPanel<Tile, ModelSkill, Attributes> panel) {
        if (this.alert != null && Maths.time() - this.alertTimer > ALTER_TIME) {
            this.alert = null;
        }
    }

    @Override
    public void renderOnMap(Graphics2D g, AbstractControlPanel<Tile, ModelSkill, Attributes> panel, StrategyCursor cursor) {
    }

    @Override
    public void renderOnPanel(Graphics2D g, AbstractControlPanel<Tile, ModelSkill, Attributes> panel, StrategyCursor cursor,
            TiledSprite icons, int x, int y, int w, int h) {

        int cx = cursor.getScreenX();
        int cy = cursor.getScreenY();
        boolean over = (cx >= x && cx <= x + w && cy >= y && cy <= y + h);
        if (this.isUnlocked()) {
            if (cursor.getClick() == Mouse.LEFT && over) {
                icons.render(g, this.getIcon(), x, y + 1);
            } else {
                icons.render(g, this.getIcon(), x, y);
                if (over && this.alert == null) {
                    TEXT.setColor(C_TEXT);
                    TEXT.draw(g, this.getDisplayName(), 72, 192, Alignment.LEFT);
                }
            }
        } else {
            icons.render(g, this.getIcon(), x, y);
            g.setColor(LOCKED);
            g.fill3DRect(x, y, w, h, false);
        }

        if (this.alert != null) {
            TEXT.setColor(C_TEXT);
            TEXT.draw(g, this.alert, 72, 192, Alignment.LEFT);
        }
    }

    @Override
    public void setLvl(int lvl) {
        super.setLvl(lvl);
        this.setIcon(this.icon + ((lvl - 1) * 3));
    }

    public void setLvl(int lvl, boolean faction) {
        super.setLvl(lvl);
        Race fac;
        if (this.owner instanceof ModelUnit) {
            fac = ((ModelUnit) this.owner).faction;
        } else {
            fac = ((ModelBuilding) this.owner).faction;
        }
        if (fac == Race.orcs) {
            this.setIcon(this.icon + ((lvl - 1) * 3) + ICON_FACTION_INC);
        } else {
            this.setIcon(this.icon + ((lvl - 1) * 3));
        }
    }

    @Override
    public void onClick() {
        ControlPanel.playSfx(SFX.click);
    }

    public void setAlert(String alert) {
        this.alert = alert;
        this.alertTimer = Maths.time();
    }
}
