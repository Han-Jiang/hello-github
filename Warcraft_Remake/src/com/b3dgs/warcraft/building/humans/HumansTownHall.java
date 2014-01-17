package com.b3dgs.warcraft.building.humans;

import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.building.ModelProductor;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import com.b3dgs.warcraft.skill.humans.ProdPeasant;

public class HumansTownHall extends ModelProductor {

    public HumansTownHall(Map map, RessourcesHandler rsch, EntryHandler handler, EntryFactory factory) {
        super(map, rsch, rsch.get("HUMANS_TOWNHALL"), rsch.get("CONSTRUCTION").ressource, handler, factory);
        this.addSkill(new ProdPeasant(0, this));
    }
}
