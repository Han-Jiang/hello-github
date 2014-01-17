package com.b3dgs.warcraft.building.orcs;

import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.building.ModelProductor;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.skill.orcs.ProdPeon;

public class OrcsTownHall extends ModelProductor {

    public OrcsTownHall(Map map, RessourcesHandler rsch, EntryHandler handler, EntryFactory factory) {
        super(map, rsch, rsch.get("ORCS_TOWNHALL"), rsch.get("CONSTRUCTION").ressource, handler, factory);
        this.addSkill(new ProdPeon(0, this));
    }
}
