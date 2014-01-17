package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;

public class StdOrcsBuild extends ModelSkill {

    public StdOrcsBuild(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
	super("ORCS_STDBUILD", priority, owner);
	this.setOrder(false);
	this.setUnlocked(true);
    }

    @Override
    public void action() {
	this.owner.getSkill("MOVE").setIgnore(true);
	this.owner.getSkill("STOP").setIgnore(true);
	this.owner.getSkill("EXTRACT").setIgnore(true);
	this.owner.getSkill("REPAIR").setIgnore(true);
	this.owner.getSkill("ORCS_STDBUILD").setIgnore(true);
	this.owner.getSkill("ORCS_TOWNHALL").setIgnore(false);
	this.owner.getSkill("ORCS_FARM").setIgnore(false);
	this.owner.getSkill("ORCS_BARRACKS").setIgnore(false);
	this.owner.getSkill("ORCS_LUMBERMILL").setIgnore(false);
	this.owner.getSkill("ORCS_CANCEL").setIgnore(false);
    }
}
