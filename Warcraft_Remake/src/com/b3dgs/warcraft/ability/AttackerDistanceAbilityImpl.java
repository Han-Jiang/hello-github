package com.b3dgs.warcraft.ability;

import com.b3dgs.lionengine.game.Projectile;
import com.b3dgs.lionengine.game.strategy.AbstractUnit;
import com.b3dgs.lionengine.game.strategy.ability.AbstractAttackerDistanceAbility;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.unit.ModelAttacker;

public class AttackerDistanceAbilityImpl extends AbstractAttackerDistanceAbility<Tile, ModelSkill, Attributes> {

    private final ModelAttacker attacker;

    public AttackerDistanceAbilityImpl(AbstractUnit<Tile, ModelSkill, Attributes> attacker, Projectile<?> projectile) {
        super(attacker, projectile);
        this.attacker = (ModelAttacker) attacker;
    }

    @Override
    public void onThrown() {
        if (this.attacker.isOnScreen()) {
            ControlPanel.playSfx(SFX.arrow_thrown);
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
        this.attacker.setAnimation("ATTACK");
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
