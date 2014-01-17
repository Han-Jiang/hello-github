package com.b3dgs.warcraft.unit;

import com.b3dgs.lionengine.Drawable;
import com.b3dgs.lionengine.drawable.TiledSprite;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.CollisionArea;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractPlayer;
import com.b3dgs.lionengine.game.strategy.AbstractUnit;
import com.b3dgs.lionengine.game.strategy.Orientation;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.Maths;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.gameplay.Player;
import com.b3dgs.warcraft.gameplay.Race;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.skill.Move;
import com.b3dgs.warcraft.skill.Stop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

public abstract class ModelUnit extends AbstractUnit<Tile, ModelSkill, Attributes> {
    
    private static final TreeMap<Integer, ModelUnit> ENTRYS = new TreeMap<Integer, ModelUnit>();
    
    public static ModelUnit get(int id) {
        return ENTRYS.get(id);
    }
    
    public static void clear() {
        ENTRYS.clear();
    }
    
    public static List<ModelUnit> getByOwner(int ownerID) {
        List<ModelUnit> list = new ArrayList<ModelUnit>(1);
        Collection<ModelUnit> c = ENTRYS.values();
        for (ModelUnit u : c) {
            if (u.getOwnerID() == ownerID) {
                list.add(u);
            }
        }
        return list;
    }
    
    private void manage() {
        ENTRYS.put(this.id, this);
    }
    private static final int CORPSE_TIME = 5000;
    private static final int CORPSE_NUMBER = 3;
    private static final int CORPSE_OFFSET = 8;
    private static final Orientation[] orientations = Orientation.values();
    public final Map map;
    public final UnitType type;
    public final Race faction;
    protected Player player;
    private boolean isOnScreen;
    private TiledSprite corpse;
    private long deadTimer, angleTimer, nextAngleTimer;
    private boolean dead;
    private int deadIndex, deadOffset;
    
    public ModelUnit(Map map, RessourcesHandler rsch, String data, BufferedImage surface) {
        super(data, map, surface, new Attributes());
        this.map = map;
        this.type = UnitType.valueOf(this.getDataString("TYPE").toUpperCase());
        this.setFieldOfView(this.getDataInt("FOV"));
        this.setFrame(this.getDataInt("DEFAULT_FRAME"));
        this.setSkipLastFrameOnReverse(true);
        this.faction = Race.valueOf(this.getDataString("FACTION").toLowerCase());
        this.life.setMax(this.getDataInt("MAX_LIFE"));
        this.life.set(this.life.getMax());
        this.addSkill(new Move(0, this));
        this.addSkill(new Stop(1, this));
        this.setSpeed(1.5f, 1.5f);
        this.setLayer(2);
        this.corpse = Drawable.DRAWABLE.loadTiledSprite(rsch.get("CORPSE").ressource, 32, 32);
        this.corpse.load(false);
        this.deadTimer = -1L;
        this.dead = false;
        this.deadIndex = 0;
        if (this.faction == Race.orcs) {
            this.deadOffset = 8;
        } else {
            this.deadOffset = 0;
        }
        this.map.fogOfWar.updateEntryFOV(this);
        this.angleTimer = Maths.time();
        this.nextAngleTimer = Maths.random(0, 2000) + 5000L;
        this.manage();
    }
    
    @Override
    public void place(int tx, int ty) {
        super.place(tx, ty);
        this.map.fogOfWar.updateEntryFOV(this);
    }
    
    @Override
    public void update(Keyboard keyboard, Mouse mouse, float extrp) {
        int otx = this.getXInTile();
        int oty = this.getYInTile();
        
        super.update(keyboard, mouse, extrp);

        // Apply mirror depending of the orientation
        Orientation o = this.getOrientation();
        if (o.ordinal() > 4) {
            if (!this.getMirror()) {
                this.mirror(true);
            }
        } else {
            if (this.getMirror()) {
                this.mirror(false);
            }
        }
        if (!this.isAlive()) {
            // Handle dead corps effect
            if (!this.dead) {
                if (this.deadTimer == -1L) {
                    this.deadTimer = Maths.time();
                }
                if (Maths.time() - this.deadTimer > CORPSE_TIME) {
                    this.setVisibility(false);
                    this.dead = true;
                    this.deadIndex = 0;
                    this.deadTimer = Maths.time();
                }
            } else {
                if (this.deadIndex <= CORPSE_NUMBER && Maths.time() - this.deadTimer > CORPSE_TIME) {
                    this.deadIndex++;
                    this.deadTimer = Maths.time();
                }
            }
            if (this.deadIndex > CORPSE_NUMBER) {
                this.remove();
            }
        } else {
            // Update fog when unit moved
            if (otx != this.getXInTile() || oty != this.getYInTile()) {
                this.map.fogOfWar.updateEntryFOV(this);
            }
            // Apply a random angle unit entry is still idle too much time
            if (this.isPassive() && Maths.time() - this.angleTimer > this.nextAngleTimer) {
                this.setAnimation("IDLE");
                this.setOrientation(orientations[Maths.random(0, orientations.length - 1)]);
                this.angleTimer = Maths.time();
                this.nextAngleTimer = Maths.random(0, 2000) + 5000L;
            }
        }
        if (this.animName != null) {
            CollisionArea area = this.getCollArea(this.animName);
            this.updateCollision(area.getX(), area.getY(), area.getWidth(), area.getHeight());
        } else {
            this.updateCollision(16, 16, 0, 0);
        }
    }
    
    @Override
    public void render(Graphics2D g, Camera camera) {
        super.render(g, camera);
        if (this.dead && this.deadIndex <= CORPSE_NUMBER) {
            int o = 0;
            if (this.getOrientation().ordinal() > 0) {
                o = 4;
            }
            this.corpse.render(g, this.deadIndex + this.deadOffset + o, this.getX() - camera.getX() - CORPSE_OFFSET,
                    this.getY() - camera.getY() - CORPSE_OFFSET);
        }
        if (this.getX() >= camera.getX() && this.getX() <= camera.getX() + 320 && this.getY() >= camera.getY() && this.getY() <= camera.getY() + 200) {
            this.isOnScreen = true;
        } else {
            this.isOnScreen = false;
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
    public void stop() {
        super.stop();
        this.clearIgnoredID();
        this.angleTimer = Maths.time();
    }
    
    @Override
    public void onStartMove() {
        this.setAnimation("MOVE");
    }
    
    @Override
    public void onMove() {
        if (!this.animName.equals("MOVE")) {
            this.setAnimation("MOVE");
        }
    }
    
    @Override
    public void onArrived() {
        this.setAnimation("IDLE");
        this.angleTimer = Maths.time();
    }
    
    @Override
    public void onDied() {
        if (this.getOrientation().ordinal() < 4) {
            this.setOrientation(Orientation.NORTH);
        } else {
            this.setOrientation(Orientation.NORTH_EAST);
        }
        this.setAnimation("DIE");
        this.player.removeUnit(this);
        if (this.isOnScreen()) {
            ControlPanel.playSfx(0, this.faction, SFX.die);
        }
    }
    
    @Override
    public void onSelection() {
        ControlPanel.playSfx(this.getOwnerID(), this.faction, SFX.select);
    }
    
    @Override
    public void onOrderedFail(ModelSkill skill) {
    }
    
    @Override
    public void onKilled(AbstractEntry<Tile, ModelSkill, Attributes> attacker) {
    }
    
    public boolean isPassive() {
        return !this.isMoving();
    }
    
    public boolean isOnScreen() {
        return this.isOnScreen;
    }
}
