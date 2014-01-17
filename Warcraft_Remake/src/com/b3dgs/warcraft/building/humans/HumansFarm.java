package com.b3dgs.warcraft.building.humans;

import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.EntryHandler;

public class HumansFarm extends ModelBuilding {

    public HumansFarm(Map map, RessourcesHandler rsch, EntryHandler handler) {
	super(map, rsch, rsch.get("HUMANS_FARM"), rsch.get("CONSTRUCTION").ressource, handler);
    }
}
