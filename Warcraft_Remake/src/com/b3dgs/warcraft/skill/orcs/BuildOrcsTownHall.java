package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.BuildModel;
import com.b3dgs.warcraft.skill.ModelSkill;

public class BuildOrcsTownHall extends BuildModel {

    public BuildOrcsTownHall(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
	super("ORCS_TOWNHALL", priority, owner);
	this.setUnlocked(true);
    }
}
