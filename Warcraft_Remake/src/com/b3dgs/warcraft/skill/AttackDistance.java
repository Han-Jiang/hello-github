package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.unit.ModelAttacker;
import com.b3dgs.warcraft.unit.ModelAttackerDistance;
import com.b3dgs.warcraft.unit.ModelUnit;

public abstract class AttackDistance extends ModelSkill {

    private ModelAttacker attacker;
    private int dmgMin, dmgMax;

    public AttackDistance(String name, int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super(name, priority, owner);
        this.setOrder(true);
        this.setUnlocked(true);
        this.attacker = (ModelAttacker) owner;
        this.dmgMin = this.attacker.damages.getMin();
        this.dmgMax = this.attacker.damages.getMax();
    }

    @Override
    public void action() {
        if (this.owner instanceof ModelAttackerDistance) {
            int id = attacker.map.getRef(this.destY, this.destX);
            if (id > 0) {
                @SuppressWarnings("unchecked")
                AbstractEntry<Tile, ModelSkill, Attributes> t = (AbstractEntry<Tile, ModelSkill, Attributes>) ModelUnit.get(id);
                this.attacker.attack(t);
                ControlPanel.playSfx(attacker.getOwnerID(), this.attacker.faction, SFX.confirm);
            }
        }
    }

    @Override
    public void setLvl(int lvl) {
        super.setLvl(lvl);
        switch (lvl) {
            case 1:
                this.attacker.setDamages(this.dmgMin, this.dmgMax);
                break;
            case 2:
                this.attacker.setDamages(this.dmgMin, this.dmgMax + 1);
                break;
            case 3:
                this.attacker.setDamages(this.dmgMin + 1, this.dmgMax + 2);
                break;
        }
    }
}
