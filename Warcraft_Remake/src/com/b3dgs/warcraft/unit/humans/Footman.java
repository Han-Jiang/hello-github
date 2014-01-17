package com.b3dgs.warcraft.unit.humans;

import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.unit.ModelAttackerMelee;
import com.b3dgs.warcraft.skill.humans.AttackSword;

public class Footman extends ModelAttackerMelee {

    public Footman(Map map, RessourcesHandler rsch) {
	super(map, rsch, rsch.get("FOOTMAN"));
	this.addSkill(new AttackSword(2, this));
    }
}
