package com.b3dgs.warcraft.skill.humans;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.BuildModel;
import com.b3dgs.warcraft.skill.ModelSkill;

public class BuildHumansLumberMill extends BuildModel {

    public BuildHumansLumberMill(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
	super("HUMANS_LUMBERMILL", priority, owner);
    }
}
