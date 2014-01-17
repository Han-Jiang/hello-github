package com.b3dgs.warcraft.unit.orcs;

import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.unit.ModelAttackerMelee;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.skill.orcs.AttackAxe;

public class Grunt extends ModelAttackerMelee {

    public Grunt(Map map, RessourcesHandler rsch) {
	super(map, rsch, rsch.get("GRUNT"));
	this.addSkill(new AttackAxe(2, this));
    }
}
