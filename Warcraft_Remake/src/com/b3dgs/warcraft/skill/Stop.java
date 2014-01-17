package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.SFX;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.gameplay.ControlPanel;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.unit.ModelUnit;

public class Stop extends ModelSkill {

    public Stop(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("STOP", priority, owner);
        this.setOrder(false);
        this.setUnlocked(true);
        this.setLvl(this.getLvl(), true);
    }

    @Override
    public void action() {
        if (this.owner instanceof ModelUnit) {
            ModelUnit unit = (ModelUnit) this.owner;
            unit.stop();
            ControlPanel.playSfx(unit.getOwnerID(), unit.faction, SFX.confirm);
        }
    }
}
