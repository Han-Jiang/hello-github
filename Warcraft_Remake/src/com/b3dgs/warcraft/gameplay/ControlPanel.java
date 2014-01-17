package com.b3dgs.warcraft.gameplay;

import com.b3dgs.lionengine.audio.Sound;
import com.b3dgs.lionengine.core.Alignment;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.Text;
import com.b3dgs.lionengine.drawable.TiledSprite;
import com.b3dgs.lionengine.game.strategy.AbstractControlPanel;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractPlayer;
import com.b3dgs.lionengine.game.strategy.StrategyCursor;
import com.b3dgs.lionengine.utility.Maths;
import com.b3dgs.lionengine.utility.Media;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.building.BuildingType;
import com.b3dgs.warcraft.building.GoldMine;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;
import static com.b3dgs.lionengine.Drawable.DRAWABLE;
import static com.b3dgs.warcraft.RessourcesHandler.*;

public class ControlPanel extends AbstractControlPanel<Tile, ModelSkill, Attributes> {
    
    private static long sndTimer = 0;
    
    public static Sound getRandSfx(SFX name) {
        switch (name) {
            case hit:
                return getRandSfx(SND_HIT);
            case arrow_hit:
                return getRandSfx(SND_ARROW_HIT);
            case arrow_thrown:
                return getRandSfx(SND_ARROW_THROWN);
            case construction:
                return getRandSfx(SND_CONSTRUCTION);
            case deconstruction:
                return getRandSfx(SND_DECONSTRUCTION);
            case click:
                return getRandSfx(SND_CLICK);
            case valided:
                return getRandSfx(SND_VALIDED);
        }
        return null;
    }
    
    public static Sound getRandSfx(Race race, SFX name) {
        if (Maths.time() - sndTimer > 300) {
            sndTimer = Maths.time();
            int n = 0;
            if (race == Race.orcs) {
                n = 1;
            }
            switch (name) {
                case select:
                    return getRandSfx(SND_SELECT[n]);
                case confirm:
                    return getRandSfx(SND_CONFIRM[n]);
                case die:
                    return getRandSfx(SND_DIE[n]);
                default:
                    return null;
            }
        } else {
            return null;
        }
    }
    
    public static void playSfx(SFX name) {
        playSfx(0, null, name);
    }
    
    public static void playSfx(int pid, Race race, SFX name) {
        try {
            if (race == null) {
                if (pid == 0 || pid == playerID) {
                    getRandSfx(name).play();
                }
            } else if (pid == playerID || pid == 0) {
                getRandSfx(race, name).play();
            }
        } catch (NullPointerException e) {
        }
    }
    
    private static Sound getRandSfx(Sound[] sfx) {
        return sfx[Maths.random(0, sfx.length - 1)];
    }
    public static final Sprite S_GOLD = DRAWABLE.loadSprite(Media.get(SPRITES_DIR, "gold.png"));
    public static final Sprite S_WOOD = DRAWABLE.loadSprite(Media.get(SPRITES_DIR, "wood.png"));
    public static final Text TEXT = DRAWABLE.createText(Font.SANS_SERIF, 9, Text.NORMAL);
    public static final Color C_TEXT = new Color(255, 255, 235);
    private static int playerID;
    private final TiledSprite skillBackground, stats;
    
    public ControlPanel(Race race) {
        super(DRAWABLE.loadSprite(getSprite("hud_" + race.name().toLowerCase() + ".png")), DRAWABLE.loadTiledSprite(getSprite("icons.png"), 27, 19));
        this.setClickableArea(72, 12, 240, 176);
        this.icons.load(false);
        this.panel.load(false);
        S_GOLD.load(false);
        S_WOOD.load(false);
        this.skillBackground = DRAWABLE.loadTiledSprite(getSprite("skill_background.png"), 31, 23);
        this.skillBackground.load(false);
        this.stats = DRAWABLE.loadTiledSprite(getSprite("stats.png"), 66, 34);
        this.stats.load(false);
    }
    
    @Override
    public void setPlayer(AbstractPlayer player) {
        super.setPlayer(player);
        playerID = player.id;
    }
    
    @Override
    protected void renderSingleEntry(Graphics2D g, StrategyCursor cursor, AbstractEntry<Tile, ModelSkill, Attributes> entry) {
        int p = entry.life.getPercent();
        boolean building = false;
        if (entry instanceof ModelBuilding) {
            ModelBuilding b = (ModelBuilding) entry;
            // Construction progress bar
            if (b.isUnderConstruction()) {
                p = b.getBuildingProgress();
                building = true;
            }
            // Farm food usage
            if (!b.isUnderConstruction() && b.type == BuildingType.FARM && b.getOwnerID() == this.player.id) {
                TEXT.setColor(C_TEXT);
                TEXT.draw(g, "Food usage:", 5, 118, Alignment.LEFT);
                TEXT.draw(g, "Growth: " + b.player().getFarmGrowth(), 10, 130, Alignment.LEFT);
                TEXT.draw(g, "Used: " + b.player().getFarmUsed(), 10, 142, Alignment.LEFT);
            }
        }
        // Skills
        if (entry.getOwnerID() == this.player.id && !building) {
            this.renderEntrySkills(g, cursor, entry, 4, 118, 7, 4, 2);
        }

        // Icon
        this.stats.render(g, 0, 2, 72);
        this.icons.render(g, entry.icon, 6, 76);
        TEXT.setColor(C_TEXT);
        TEXT.draw(g, entry.getDataString("NAME"), 5, 97, Alignment.LEFT);

        // Goldmine informations
        if (entry instanceof GoldMine) {
            GoldMine gm = (GoldMine) entry;
            TEXT.setColor(C_TEXT);
            TEXT.draw(g, "Gold left:", 5, 118, Alignment.LEFT);
            TEXT.draw(g, "" + (int) gm.gold.getValue(), 10, 130, Alignment.LEFT);
        }

        // Life bar color
        if (p > 50) {
            g.setColor(Color.GREEN);
        } else if (p > 25) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.RED);
        }
        g.fillRect(37, 92, (int) Math.ceil(p * 0.265f), 3);
    }
    
    @Override
    protected void renderMultipleEntry(Graphics2D g, StrategyCursor cursor, Set<AbstractEntry<Tile, ModelSkill, Attributes>> entrys) {
        Set<String> ignore = new HashSet<String>(1);
        ignore.add(((Player) this.player).race + "_STDBUILD");
        this.renderEntrysSkills(g, cursor, entrys, 4, 118, 7, 4, 2, ignore);
    }
    
    @Override
    protected void updateSkill(Graphics2D g, StrategyCursor cursor, ModelSkill skill, int ico, int ix, int iy, int iw, int ih) {
        int cx = cursor.getScreenX(), cy = cursor.getScreenY();
        if (skill.isUnlocked()) {
            // Selection effect
            if (skill.isSelected() && cx >= ix && cx <= ix + iw && cy >= iy && cy <= iy + ih) {
                this.skillBackground.render(g, 1, ix - 2, iy - 2);
                super.updateSkill(g, cursor, skill, ico, ix, iy + 1, iw, ih);
            } else {
                this.skillBackground.render(g, 0, ix - 2, iy - 2);
                super.updateSkill(g, cursor, skill, ico, ix, iy, iw, ih);
            }
        } else {
            this.skillBackground.render(g, 0, ix - 2, iy - 2);
        }
    }
}
