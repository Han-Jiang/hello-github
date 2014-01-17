package com.b3dgs.warcraft.building.orcs;

import com.b3dgs.warcraft.building.ModelBuilding;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.EntryHandler;

public class OrcsFarm extends ModelBuilding {

    public OrcsFarm(Map map, RessourcesHandler rsch, EntryHandler handler) {
        super(map, rsch, rsch.get("ORCS_FARM"), rsch.get("CONSTRUCTION").ressource, handler);
    }
}
