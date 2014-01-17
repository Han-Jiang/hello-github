package com.b3dgs.warcraft.unit;

import com.b3dgs.lionengine.game.MediaRessource;
import com.b3dgs.lionengine.game.strategy.AbstractBuilding;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractUnit;
import com.b3dgs.lionengine.game.strategy.ability.AttackerAbility;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.Maths;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.gameplay.AI;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Main abstraction representing any unit with the ability of attacking.
 */
public abstract class ModelAttacker extends ModelUnit implements AttackerAbility<Tile, ModelSkill, Attributes> {

    private final List<AbstractEntry<Tile, ModelSkill, Attributes>> guards;
    private int orderX, orderY;
    private boolean assault, riposte;
    private boolean defend;
    private long guardTimer;

    public ModelAttacker(Map map, RessourcesHandler rsch, MediaRessource<BufferedImage> rsc) {
        super(map, rsch, rsc.file, rsc.ressource);
        this.guards = new ArrayList<AbstractEntry<Tile, ModelSkill, Attributes>>(1);
        this.damages.setMin(this.getDataInt("DMG_MIN"));
        this.damages.setMax(this.getDataInt("DMG_MAX"));
        this.riposte = true;
        this.assault = false;
        this.defend = false;
        this.orderX = -1;
        this.orderY = -1;
    }

    public void setDamages(int min, int max) {
        this.damages.setMin(min);
        this.damages.setMax(max);
    }

    @Override
    public void update(Keyboard keyboard, Mouse mouse, float extrp) {
        super.update(keyboard, mouse, extrp);
        this.updateAttack(extrp);
        if (this.isAlive() && (!this.hasTarget() || this.isMoving())) {
            // Reset defense state when its over
            if (this.isDefending() && !this.isAttacking()) {
                this.defend = false;
                this.assault = false;
            }
            // Check guard area when not defending & attacking or assault
            if (!this.isDefending() && (this.assault || (!this.isAttacking() && !this.isMoving()))) {
                if (Maths.time() - this.guardTimer > 500) {
                    if (this.player instanceof AI) {
                        this.guard();
                    } else {
                        if (!this.isMoving()) {
                            this.guard();
                        }
                    }
                    this.guardTimer = Maths.time();
                }
            }
        }
    }

    @Override
    public boolean assignDestination(int tx, int ty) {
        boolean found = super.assignDestination(tx, ty);
        if (this.orderX == -1 && this.assault) {
            this.orderX = tx;
            this.orderY = ty;
        }
        return found;
    }

    public void reAssignDestination() {
        if (this.orderX != -1 && this.orderY != -1) {
            this.stopAttack();
            this.setTarget(null);
            super.assignDestination(this.orderX, this.orderY);
        } else {
            this.stopAttack();
            this.stopMoves();
        }
    }

    @Override
    public void stop() {
        this.stopAttack();
        super.stop();
    }

    protected void guard() {
        int fov = this.getFieldOfView() - 1;
        for (int v = this.getYInTile() - fov; v <= this.getYInTile() + fov; v++) {
            for (int h = this.getXInTile() - fov; h <= this.getXInTile() + fov; h++) {
                try {
                    int eid = this.map.getRef(v, h);
                    if (eid > 0 && eid != this.id) {
                        AbstractEntry<Tile, ModelSkill, Attributes> e = ModelUnit.get(eid);
                        if (e == null) {
                            e = ModelBuilding.get(eid);
                        }
                        if (e.isAlive() && e.isVisible() && e.getOwnerID() != this.getOwnerID() && e.getOwnerID() > 0 && e.isActive()) {
                            this.guards.add(e);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        }
        int min = Integer.MAX_VALUE;
        AbstractEntry<Tile, ModelSkill, Attributes> closest = null;
        for (AbstractEntry<Tile, ModelSkill, Attributes> e : this.guards) {
            int dist = this.getDistance(e);
            // Priority to unit
            if (closest instanceof AbstractBuilding && e instanceof AbstractUnit) {
                min = dist;
                closest = e;
            } else if (!(closest instanceof AbstractUnit && e instanceof AbstractBuilding) || closest == null) {
                if (dist < min) {
                    min = dist;
                    closest = e;
                }
            }
        }
        this.guards.clear();
        if (closest != null) {
            this.guardAction(closest);
        }
    }

    protected void guardAction(AbstractEntry<Tile, ModelSkill, Attributes> e) {
        // Priority to attacker model
        if (this.getTarget() instanceof ModelAttacker && !(e instanceof ModelAttacker)) {
            return;
        }
        this.attack(e);
    }

    @Override
    public void onHit(AbstractEntry<Tile, ModelSkill, Attributes> attacker) {
        super.onHit(attacker);
        if (this.isAlive() && this.riposte) {
            // AI gives priority to unit riposte
            if (attacker instanceof AbstractUnit && this.getTarget() instanceof AbstractBuilding && this.player instanceof AI) {
                this.attack(attacker);
                return;
            }
            // Keep closest target only
            boolean closest = false;
            if (this.hasTarget()) {
                closest = this.getDistance(attacker) < this.getDistance(this.getTarget());
            }
            if ((this.hasTarget() || closest) && this.getOwnerID() != attacker.getOwnerID()) {
                this.attack(attacker);
            }
        }
    }

    @Override
    public void onKilled(AbstractEntry<Tile, ModelSkill, Attributes> attacker) {
        if (this.assault) {
            this.reAssignDestination();
        }
    }

    public void setRiposte(boolean state) {
        this.riposte = state;
    }

    public void setAssault(boolean state) {
        this.assault = state;
    }

    public boolean getAssault() {
        return this.assault;
    }

    public void setDefend(boolean state) {
        this.defend = state;
    }

    public boolean isDefending() {
        return this.defend;
    }

    @Override
    public boolean isPassive() {
        return super.isPassive() && !this.isAttacking();
    }

    public boolean hasTarget() {
        return this.getTarget() != null;
    }
}
