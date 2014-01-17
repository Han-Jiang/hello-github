package com.b3dgs.warcraft.unit.orcs;

import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.Arrow;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.unit.ModelAttackerDistance;
import com.b3dgs.warcraft.skill.orcs.AttackSpear;

public class Spearman extends ModelAttackerDistance {

    public Spearman(Map map, RessourcesHandler rsch) {
	super(map, rsch, rsch.get("SPEARMAN"), new Arrow(rsch));
	this.addSkill(new AttackSpear(2, this));
	this.attacker.setAttackFrame(3);
    }
}
