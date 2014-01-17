package com.b3dgs.warcraft.building.humans;

import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.skill.humans.UpgradeArrow;

public class HumansLumberMill extends ModelBuilding {

    public HumansLumberMill(Map map, RessourcesHandler rsch, EntryHandler handler) {
	super(map, rsch, rsch.get("HUMANS_LUMBERMILL"), rsch.get("CONSTRUCTION").ressource, handler);
	this.addSkill(new UpgradeArrow(0, this));
    }
}
