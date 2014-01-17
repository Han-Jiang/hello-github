package com.b3dgs.warcraft.unit.humans;

import com.b3dgs.lionengine.game.strategy.AbstractEntryHandler;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.unit.ModelWorker;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.skill.humans.BuildHumansBarracks;
import com.b3dgs.warcraft.skill.humans.BuildHumansFarm;
import com.b3dgs.warcraft.skill.humans.BuildHumansLumberMill;
import com.b3dgs.warcraft.skill.humans.BuildHumansTownHall;
import com.b3dgs.warcraft.skill.humans.CancelHumansBuild;
import com.b3dgs.warcraft.skill.humans.StdHumansBuild;

public class Peasant extends ModelWorker {

    public Peasant(Map map, RessourcesHandler rsch, AbstractEntryHandler<Tile, ModelSkill, Attributes> handler, EntryFactory factory) {
	super(map, rsch, rsch.get("PEASANT"), handler, factory);

	this.addSkill(new StdHumansBuild(4, this));

	this.addSkill(new BuildHumansTownHall(0, this));
	this.addSkill(new BuildHumansFarm(1, this));
	this.addSkill(new BuildHumansBarracks(2, this));
	this.addSkill(new BuildHumansLumberMill(3, this));
	this.addSkill(new CancelHumansBuild(4, this));
    }
}
