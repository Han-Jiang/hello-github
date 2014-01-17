package com.b3dgs.warcraft.skill.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntry;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.BuildModel;
import com.b3dgs.warcraft.skill.ModelSkill;

public class BuildOrcsLumberMill extends BuildModel {

    public BuildOrcsLumberMill(int priority, AbstractEntry<Tile, ModelSkill, Attributes> owner) {
	super("ORCS_LUMBERMILL", priority, owner);
    }
}
