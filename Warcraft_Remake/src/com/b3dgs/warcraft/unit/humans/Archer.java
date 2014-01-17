package com.b3dgs.warcraft.unit.humans;

import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.Arrow;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.unit.ModelAttackerDistance;
import com.b3dgs.warcraft.skill.humans.AttackArrow;

public class Archer extends ModelAttackerDistance {

    public Archer(Map map, RessourcesHandler rsch) {
	super(map, rsch, rsch.get("ARCHER"), new Arrow(rsch));
	this.addSkill(new AttackArrow(2, this));
	this.attacker.setAttackFrame(2);
    }
}
