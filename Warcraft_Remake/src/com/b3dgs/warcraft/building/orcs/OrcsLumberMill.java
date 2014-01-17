package com.b3dgs.warcraft.building.orcs;

import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.skill.orcs.UpgradeSpear;

public class OrcsLumberMill extends ModelBuilding {

    public OrcsLumberMill(Map map, RessourcesHandler rsch, EntryHandler handler) {
        super(map, rsch, rsch.get("ORCS_LUMBERMILL"), rsch.get("CONSTRUCTION").ressource, handler);
        this.addSkill(new UpgradeSpear(0, this));
    }
}
