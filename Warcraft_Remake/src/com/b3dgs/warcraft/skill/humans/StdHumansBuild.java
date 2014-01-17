package com.b3dgs.warcraft.skill.humans;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;

public class StdHumansBuild extends ModelSkill {

    public StdHumansBuild(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
        super("HUMANS_STDBUILD", priority, owner);
        this.setOrder(false);
        this.setUnlocked(true);
    }

    @Override
    public void action() {
        this.owner.getSkill("MOVE").setIgnore(true);
        this.owner.getSkill("STOP").setIgnore(true);
        this.owner.getSkill("EXTRACT").setIgnore(true);
        this.owner.getSkill("REPAIR").setIgnore(true);
        this.owner.getSkill("HUMANS_STDBUILD").setIgnore(true);
        this.owner.getSkill("HUMANS_TOWNHALL").setIgnore(false);
        this.owner.getSkill("HUMANS_FARM").setIgnore(false);
        this.owner.getSkill("HUMANS_BARRACKS").setIgnore(false);
        this.owner.getSkill("HUMANS_LUMBERMILL").setIgnore(false);
        this.owner.getSkill("HUMANS_CANCEL").setIgnore(false);
    }
}
