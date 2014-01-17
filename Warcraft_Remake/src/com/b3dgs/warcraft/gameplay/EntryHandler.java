package com.b3dgs.warcraft.gameplay;

import com.b3dgs.lionengine.drawable.AnimatedSprite;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractEntryHandler;
import com.b3dgs.lionengine.game.strategy.StrategyCamera;
import com.b3dgs.lionengine.game.strategy.StrategyCursor;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.building.GoldMine;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.map.CollisionType;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.unit.ModelAttacker;
import com.b3dgs.warcraft.unit.ModelUnit;
import com.b3dgs.warcraft.unit.ModelWorker;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Override default handler, by giving more specific functions such as default click.
 */
public class EntryHandler extends AbstractEntryHandler<Tile, ModelSkill, Attributes> {

    private class Effect {

        private int x1, y1, x2, y2;
        private AnimatedSprite eff1, eff2;
        private boolean active1, active2;

        private Effect(AnimatedSprite eff1, int x1, int y1, AnimatedSprite eff2, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.eff1 = eff1;
            this.eff2 = eff2;
            this.active1 = false;
            this.active2 = false;
        }

        private void render(Graphics2D g, Camera camera) {
            if (this.active1) {
                this.eff1.render(g, this.x1 - camera.getX(), this.y1 - camera.getY());
            }
            if (this.active2) {
                this.eff2.render(g, this.x2 - camera.getX(), this.y2 - camera.getY());
            }
        }

        private void activate1(boolean state) {
            this.active1 = state;
        }

        private void activate2(boolean state) {
            this.active2 = state;
        }
    }
    private final Map map;
    private int pathCount, selectionSize;
    private boolean cancelClick;
    private final TreeMap<Integer, Effect> effects = new TreeMap<Integer, Effect>();
    private final Set<Integer> remove = new HashSet<Integer>(1);

    public EntryHandler(ControlPanel panel, Map map) {
        super(panel);
        this.map = map;
        this.pathCount = 0;
        this.selectionSize = 0;
        this.cancelClick = false;
    }

    public void addEffect(int id, AnimatedSprite eff1, int x1, int y1, AnimatedSprite eff2, int x2, int y2) {
        this.effects.put(id, new Effect(eff1, x1, y1, eff2, x2, y2));
    }

    public void removeEffect(int id) {
        this.remove.add(id);
    }

    public void activateEffect(int id, int n, boolean state) {
        Effect e = this.effects.get(id);
        if (n == 0) {
            e.activate1(state);
        } else if (n == 1) {
            e.activate2(state);
        }
    }

    @Override
    public void update(Keyboard keyboard, StrategyCursor cursor, StrategyCamera camera, float extrp) {
        this.pathCount = 0;
        this.selectionSize = (int) Math.sqrt(this.getSelection().size());
        super.update(keyboard, cursor, camera, extrp);
    }

    @Override
    public void render(Graphics2D g, StrategyCamera camera, StrategyCursor cursor) {
        super.render(g, camera, cursor);
        Set<Integer> set = effects.keySet();
        boolean del = false;
        for (Integer k : set) {
            Effect e = this.effects.get(k);
            e.render(g, camera);
            if (e.eff2.getFrame() > 17) {
                del = true;
            }
        }
        if (del) {
            for (Integer e : this.remove) {
                this.effects.remove(e);
            }
        }
    }

    @Override
    protected boolean canBeSelected(AbstractEntry<Tile, ModelSkill, Attributes> entry) {
        for (int v = entry.getYInTile(); v < entry.getYInTile() + entry.getHeightInTile(); v++) {
            for (int h = entry.getXInTile(); h < entry.getXInTile() + entry.getWidthInTile(); h++) {
                if (this.map.fogOfWar.isVisited(v, h)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void updateEntry(Keyboard keyboard, StrategyCursor cursor, StrategyCamera camera,
            AbstractEntry<Tile, ModelSkill, Attributes> entry, float extrp) {

        super.updateEntry(keyboard, cursor, camera, entry, extrp);

        int tx = cursor.getHorizontalMapTile();
        int ty = cursor.getVerticalMapTile();

        // Automatic cancel call in case of worker entry,
        // in order to close the construction panel after an unselection.
        if (cursor.getClick() == Mouse.LEFT) {
            if (!this.cancelClick && !entry.isSelected() && entry instanceof ModelWorker) {
                if (((ModelWorker) entry).player() == this.player) {
                    entry.getSkill(entry.getDataString("FACTION").toUpperCase() + "_CANCEL").action();
                    this.cancelClick = true;
                }
            }
        } else {
            this.cancelClick = false;
        }

        if (entry.isSelected() && !this.clicked && entry.getOwnerID() == this.player.id) {

            // Default order
            if (!keyboard.used() && cursor.getClick() == Mouse.RIGHT && cursor.canClick(this.panel)) {

                // Ensure it is not a building
                if (entry instanceof ModelUnit) {
                    ModelUnit u = (ModelUnit) entry;

                    // Right click with an attacker over an ennemy means attack
                    if (u instanceof ModelAttacker) {
                        int id = u.map.getRef(ty, tx);
                        if (id > 0) {
                            AbstractEntry<Tile, ModelSkill, Attributes> e = ModelUnit.get(id);
                            if (e == null) {
                                e = ModelBuilding.get(id);
                            }
                            if (e != null && e.getOwnerID() != u.getOwnerID()) {
                                ((ModelAttacker) u).attack(e);
                                return;
                            }
                        }
                    }

                    // Else it means a simple move
                    this.move(u, tx, ty);
                }
            }
        }
    }

    private void move(ModelUnit unit, int tx, int ty) {
        int ox = 0, oy = 0;
        // Destination for each unit in case of multiple selection
        if (this.pathCount > 0) {
            ox = this.pathCount % this.selectionSize;
            oy = (int) Math.floor(this.pathCount / this.selectionSize);
        }
        this.clickedFlag = true;
        this.pathCount++;

        if (unit instanceof ModelAttacker) {
            ((ModelAttacker) unit).stopAttack();
            ((ModelAttacker) unit).setTarget(null);
        }
        if (unit instanceof ModelWorker) {
            int id = this.map.getRef(ty, tx);
            ModelBuilding e = ModelBuilding.get(id);
            if (this.map.getTile(ty, tx).getCollType() == CollisionType.TREE || (id > 0 && e instanceof GoldMine)) {
                ModelWorker worker = (ModelWorker) unit;
                ModelSkill s = worker.getSkill("EXTRACT");
                s.setDestination(tx, ty);
                s.action();
                return;
            }
        }
        unit.assignDestination(tx + ox, ty + oy);
        ControlPanel.playSfx(unit.getOwnerID(), unit.faction, SFX.confirm);
    }

    @Override
    public void renderCursorSelection(Graphics2D g, Color color, int x, int y, int w, int h) {
        super.renderCursorSelection(g, Color.GREEN, x, y, w, h);
    }

    @Override
    public void renderEntryOver(Graphics2D g, AbstractEntry<Tile, ModelSkill, Attributes> entry, StrategyCamera camera, Color color) {
        super.renderEntryOver(g, entry, camera, Color.GRAY);
    }

    @Override
    public void renderEntrySelection(Graphics2D g, AbstractEntry<Tile, ModelSkill, Attributes> entry, StrategyCamera camera, Color color) {
        if (entry.getOwnerID() == 0) {
            super.renderEntrySelection(g, entry, camera, Color.LIGHT_GRAY);
        } else if (entry.getOwnerID() != this.player.id) {
            super.renderEntrySelection(g, entry, camera, Color.RED);
        } else {
            super.renderEntrySelection(g, entry, camera, Color.GREEN);
        }
    }
}
