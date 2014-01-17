package com.b3dgs.warcraft.building;

import com.b3dgs.lionengine.Drawable;
import com.b3dgs.lionengine.drawable.AnimState;
import com.b3dgs.lionengine.drawable.AnimatedSprite;
import com.b3dgs.lionengine.drawable.TiledSprite;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.CollisionArea;
import com.b3dgs.lionengine.game.MediaRessource;
import com.b3dgs.lionengine.game.strategy.AbstractBuilding;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractPlayer;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.gameplay.Player;
import com.b3dgs.warcraft.gameplay.Race;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.unit.ModelWorker;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

public abstract class ModelBuilding extends AbstractBuilding<Tile, ModelSkill, Attributes> {

    private static final TreeMap<Integer, ModelBuilding> ENTRYS = new TreeMap<Integer, ModelBuilding>();

    public static ModelBuilding get(int id) {
        return ENTRYS.get(id);
    }

    public static void clear() {
        ENTRYS.clear();
    }

    public static List<ModelBuilding> getByOwner(int ownerID) {
        List<ModelBuilding> list = new ArrayList<ModelBuilding>(1);
        Collection<ModelBuilding> c = ENTRYS.values();
        for (ModelBuilding u : c) {
            if (u.getOwnerID() == ownerID) {
                list.add(u);
            }
        }
        return list;
    }

    private void manage() {
        ENTRYS.put(this.id, this);
    }
    private static final int EXPLODE_SIZE = 64;
    private static final int BURNING_OFFSET = 16;
    public final Map map;
    public final BuildingType type;
    public final Race faction;
    protected Player player;
    private final EntryHandler handler;
    private final TiledSprite construction;
    private final AnimatedSprite burning, explode;
    private boolean destroyed, isOnScreen;

    public ModelBuilding(Map map, RessourcesHandler rsch, MediaRessource<BufferedImage> rsc, BufferedImage construction, EntryHandler handler) {
        super(rsc.file, map, rsc.ressource, new Attributes());
        this.handler = handler;
        this.map = map;
        this.type = BuildingType.valueOf(this.getDataString("TYPE").toUpperCase());
        this.setFieldOfView(this.getDataInt("FOV"));
        this.setFrame(this.getDataInt("DEFAULT_FRAME"));
        this.faction = Race.valueOf(this.getDataString("FACTION").toLowerCase());
        this.life.setMax(this.getDataInt("MAX_LIFE"));
        this.life.set(this.life.getMax());
        this.construction = Drawable.DRAWABLE.loadTiledSprite(construction, 48, 48);
        this.construction.load(false);
        this.burning = Drawable.DRAWABLE.loadAnimatedSprite(rsch.get("BURNING").ressource, 4, 2);
        this.burning.load(false);
        this.explode = Drawable.DRAWABLE.loadAnimatedSprite(rsch.get("EXPLODE").ressource, 6, 3);
        this.explode.load(false);
        this.destroyed = false;
        this.setLayer(1);
        this.map.fogOfWar.updateEntryFOV(this);
        this.manage();
    }

    @Override
    public void place(int tx, int ty) {
        super.place(tx, ty);
        this.map.fogOfWar.updateEntryFOV(this);
        this.handler.addEffect(this.id,
                this.burning, this.getX() + this.getWidth() / 2 - BURNING_OFFSET / 2, this.getY() - BURNING_OFFSET,
                this.explode, this.getX(), this.getY() - (EXPLODE_SIZE - this.getWidth()));
    }

    @Override
    public void update(Keyboard keyboard, Mouse mouse, float extrp) {
        super.update(keyboard, mouse, extrp);
        // Explode animation
        if (!this.isAlive() && !this.destroyed) {
            this.explode.updateAnimation(extrp);
            this.explode.play(1, 18, 0.12f, false, false);
            if (this.explode.getFrame() > 5) {
                this.setVisibility(false);
            }
            if (this.explode.getFrame() > 17) {
                this.remove();
            }
            if (this.explode.getAnimState() == AnimState.FINISHED) {
                this.destroyed = true;
            }
        }
        // Damaged building effect (fire)
        if (this.isAlive() && this.life.getPercent() <= 50) {
            this.burning.updateAnimation(extrp);
            if (this.life.getPercent() > 25) {
                this.burning.play(1, 4, 0.2f, false, true);
            } else {
                this.burning.play(5, 8, 0.2f, false, true);
            }
        }
        if (this.animName != null) {
            CollisionArea area = this.getCollArea(this.animName);
            this.updateCollision(area.getX(), area.getY(), area.getWidth(), area.getHeight());
        }
    }

    @Override
    public void render(Graphics2D g, Camera camera) {
        // Construction animation
        if (this.isUnderConstruction()) {
            int progress = this.getBuildingProgress();
            int tile = 0, ox, oy;
            if (progress > 25) {
                tile = 1;
                ox = 18 - this.getWidth() / 2;
                oy = 18 - this.getHeight() / 2;
            } else {
                ox = 16 - this.getWidth() / 2;
                oy = 16 - this.getHeight() / 2;
            }
            if (progress <= 50) {
                this.construction.render(g, tile, this.getX() - camera.getX() - ox, this.getY() - camera.getY() - oy);
            } else {
                super.render(g, camera);
            }
        } else {
            super.render(g, camera);
        }
        if (this.explode.getFrame() < 18) {
            if (!this.isAlive() && this.explode.getFrame() < 18) {
                this.handler.activateEffect(this.id, 1, true);
            } else {
                this.handler.activateEffect(this.id, 1, false);
            }
            if (this.isAlive() && this.life.getPercent() <= 50) {
                this.handler.activateEffect(this.id, 0, true);
            } else {
                this.handler.activateEffect(this.id, 0, false);
            }
            if (this.getX() >= camera.getX() && this.getX() <= camera.getX() + 320 && this.getY() >= camera.getY() && this.getY() <= camera.getY() + 200) {
                this.isOnScreen = true;
            } else {
                this.isOnScreen = false;
            }
        }
    }

    @Override
    public void setOwnerID(int id) {
        super.setOwnerID(id);
        if (id > 0) {
            this.player = (Player) AbstractPlayer.get(id);
        }
    }

    public Player player() {
        return this.player;
    }

    @Override
    public void onStartConstruction() {
    }

    @Override
    public void onConstructing() {
        if (this.getBuildingProgress() >= 50) {
            this.setVisibility(true);
        }
    }

    @Override
    public void onConstructed() {
        this.setFrame(2);
        this.setActive(true);
        this.map.fogOfWar.updateEntryFOV(this);
    }

    @Override
    public void onDestroyed() {
        if (this.isOnScreen()) {
            ControlPanel.playSfx(SFX.deconstruction);
        }
        this.setActive(false);
        this.destroyed = false;
        this.explode.setFrame(1);
        if (this.player != null) {
            this.player.removeBuilding(this);
        }
        this.handler.removeEffect(this.id);
    }

    @Override
    public void onSelection() {
        if (this.isUnderConstruction()) {
            ControlPanel.playSfx(this.getOwnerID(), null, SFX.construction);
        } else {
            ControlPanel.playSfx(this.getOwnerID(), null, SFX.click);
        }
    }

    @Override
    public void onOrderedFail(ModelSkill skill) {
    }

    @Override
    public void onHit(AbstractEntry<Tile, ModelSkill, Attributes> attacker) {
        super.onHit(attacker);
        this.player().defendMe(this, attacker);
    }

    @Override
    public void onKilled(AbstractEntry<Tile, ModelSkill, Attributes> attacker) {
    }

    public ModelWorker getBuilder() {
        if (this.builder != null) {
            return (ModelWorker) this.builder;
        } else {
            return null;
        }
    }

    public boolean isOnScreen() {
        return this.isOnScreen;
    }
}
