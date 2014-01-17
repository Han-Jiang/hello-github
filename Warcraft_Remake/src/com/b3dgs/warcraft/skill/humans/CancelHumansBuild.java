package com.b3dgs.warcraft.skill.humans;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;

public class CancelHumansBuild extends ModelSkill {

    public CancelHumansBuild(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("HUMANS_CANCEL", priority, owner);
        this.setUnlocked(true);
        this.setIgnore(true);
        this.setOrder(false);
    }

    @Override
    public void action() {
        this.owner.getSkill("MOVE").setIgnore(false);
        this.owner.getSkill("STOP").setIgnore(false);
        this.owner.getSkill("EXTRACT").setIgnore(false);
        this.owner.getSkill("REPAIR").setIgnore(false);
        this.owner.getSkill("HUMANS_STDBUILD").setIgnore(false);
        this.owner.getSkill("HUMANS_TOWNHALL").setIgnore(true);
        this.owner.getSkill("HUMANS_FARM").setIgnore(true);
        this.owner.getSkill("HUMANS_BARRACKS").setIgnore(true);
        this.owner.getSkill("HUMANS_LUMBERMILL").setIgnore(true);
        this.owner.getSkill("HUMANS_CANCEL").setIgnore(true);
    }
}
