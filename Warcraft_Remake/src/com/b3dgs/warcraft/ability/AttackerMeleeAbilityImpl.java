package com.b3dgs.warcraft.ability;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.lionengine.game.strategy.AbstractUnit;
import com.b3dgs.lionengine.game.strategy.ability.AbstractAttackerMeleeAbility;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.unit.ModelAttacker;

public class AttackerMeleeAbilityImpl extends AbstractAttackerMeleeAbility<Tile, ModelSkill, Attributes> {

    private final ModelAttacker attacker;

    public AttackerMeleeAbilityImpl(AbstractUnit<Tile, ModelSkill, Attributes> attacker) {
        super(attacker);
        this.attacker = (ModelAttacker) attacker;
    }

    @Override
    public void updateAttack(float extrp) {
        super.updateAttack(extrp);
    }

    @Override
    public void onHitTarget(AbstractEntry<Tile, ModelSkill, Attributes> target) {
        target.life.decrease(this.attacker.damages.getRandomDmg());
        if (this.attacker.isOnScreen()) {
            ControlPanel.playSfx(SFX.hit);
        }
    }

    @Override
    public void onAttacked() {
        this.attacker.setAnimation("IDLE");
    }

    @Override
    public void onReaching() {
    }

    @Override
    public boolean onStartAttack() {
        boolean start = super.onStartAttack();
        if (start) {
            this.attacker.setAnimation("ATTACK");
        }
        return start;
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onLostTarget() {
        this.attacker.reAssignDestination();
    }
}
