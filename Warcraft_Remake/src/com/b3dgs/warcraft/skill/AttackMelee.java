package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.unit.ModelAttackerMelee;
import com.b3dgs.warcraft.unit.ModelUnit;

public abstract class AttackMelee extends ModelSkill {

    public AttackMelee(String name, int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super(name, priority, owner);
        this.setOrder(true);
        this.setUnlocked(true);
    }

    @Override
    public void action() {
        if (this.owner instanceof ModelAttackerMelee) {
            ModelAttackerMelee attacker = (ModelAttackerMelee) this.owner;
            int id = attacker.map.getRef(this.destY, this.destX);
            if (id > 0) {
                @SuppressWarnings("unchecked")
                AbstractEntry<Tile, ModelSkill, Attributes> t = (AbstractEntry<Tile, ModelSkill, Attributes>) ModelUnit.get(id);
                attacker.attack(t);
                ControlPanel.playSfx(attacker.getOwnerID(), attacker.faction, SFX.confirm);
            }
        }
    }
}
