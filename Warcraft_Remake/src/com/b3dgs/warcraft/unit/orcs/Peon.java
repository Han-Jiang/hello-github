package com.b3dgs.warcraft.unit.orcs;

import com.b3dgs.lionengine.game.strategy.AbstractEntryHandler;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.unit.ModelWorker;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.skill.orcs.BuildOrcsBarracks;
import com.b3dgs.warcraft.skill.orcs.BuildOrcsFarm;
import com.b3dgs.warcraft.skill.orcs.BuildOrcsLumberMill;
import com.b3dgs.warcraft.skill.orcs.BuildOrcsTownHall;
import com.b3dgs.warcraft.skill.orcs.CancelOrcsBuild;
import com.b3dgs.warcraft.skill.orcs.StdOrcsBuild;

public class Peon extends ModelWorker {

    public Peon(Map map, RessourcesHandler rsch, AbstractEntryHandler<Tile, ModelSkill, Attributes> handler, EntryFactory factory) {
	super(map, rsch, rsch.get("PEON"), handler, factory);

	this.addSkill(new StdOrcsBuild(4, this));

	this.addSkill(new BuildOrcsTownHall(0, this));
	this.addSkill(new BuildOrcsFarm(1, this));
	this.addSkill(new BuildOrcsBarracks(2, this));
	this.addSkill(new BuildOrcsLumberMill(3, this));
	this.addSkill(new CancelOrcsBuild(4, this));
    }
}
