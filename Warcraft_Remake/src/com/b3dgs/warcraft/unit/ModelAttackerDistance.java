package com.b3dgs.warcraft.unit;

import com.b3dgs.warcraft.ability.AttackerDistanceAbilityImpl;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.MediaRessource;
import com.b3dgs.lionengine.game.Projectile;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.ability.AttackerAbility;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class ModelAttackerDistance extends ModelAttacker {

    protected final AttackerAbility<Tile, ModelSkill, Attributes> attacker;
    protected final Projectile<?> projectile;

    public ModelAttackerDistance(Map map, RessourcesHandler rsch, MediaRessource<BufferedImage> rsc, Projectile<?> projectile) {
        super(map, rsch, rsc);
        this.attacker = new AttackerDistanceAbilityImpl(this, projectile);
        this.projectile = projectile;
        this.setAttackTimer(this.getDataInt("ATT_TIME"));
        this.attacker.setMinimalAttackDistance(1);
        this.attacker.setMaximalAttackDistance(this.getFieldOfView());
        projectile.damages.setMin(this.getDataInt("DMG_MIN"));
        projectile.damages.setMax(this.getDataInt("DMG_MAX"));
    }

    @Override
    public void setDamages(int min, int max) {
        this.damages.setMin(min);
        this.damages.setMax(max);
        this.projectile.damages.setMin(min);
        this.projectile.damages.setMax(max);
    }

    @Override
    public void render(Graphics2D g, Camera camera) {
        super.render(g, camera);
        this.projectile.render(g, camera);
    }

    /*
     ***************************** Attacker handle *****************************
     * Uses of attacker ability.
     **************************************************************************
     */
    @Override
    public final void updateAttack(float extrp) {
        this.attacker.updateAttack(extrp);
    }

    @Override
    public final void attack(AbstractEntry<Tile, ModelSkill, Attributes> entry) {
        this.attacker.attack(entry);
    }

    @Override
    public final void stopAttack() {
        this.attacker.stopAttack();
    }

    @Override
    public final void setAttackTimer(int time) {
        this.attacker.setAttackTimer(time);
    }

    @Override
    public void setAttackFrame(int frame) {
        this.attacker.setAttackFrame(frame);
    }

    @Override
    public final void setMinimalAttackDistance(int min) {
        this.attacker.setMinimalAttackDistance(min);
    }

    @Override
    public final void setMaximalAttackDistance(int max) {
        this.attacker.setMaximalAttackDistance(max);
    }

    @Override
    public final boolean isAttacking() {
        return this.attacker.isAttacking();
    }
}
