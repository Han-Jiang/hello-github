package com.b3dgs.warcraft.unit;

import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.ability.AttackerMeleeAbilityImpl;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.lionengine.game.MediaRessource;
import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.ability.AttackerAbility;
import java.awt.image.BufferedImage;

public abstract class ModelAttackerMelee extends ModelAttacker {

    public final AttackerAbility<Tile, ModelSkill, Attributes> attacker;

    public ModelAttackerMelee(Map map, RessourcesHandler rsch, MediaRessource<BufferedImage> rsc) {
        super(map, rsch, rsc);
        this.attacker = new AttackerMeleeAbilityImpl(this);
        this.setAttackTimer(this.getDataInt("ATT_TIME"));
        this.attacker.setAttackFrame(5);
        this.attacker.setMinimalAttackDistance(1);
        this.attacker.setMaximalAttackDistance(1);
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
