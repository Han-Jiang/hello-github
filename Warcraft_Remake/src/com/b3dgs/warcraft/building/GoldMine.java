package com.b3dgs.warcraft.building;

import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.Ressource;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.warcraft.gameplay.EntryHandler;

public class GoldMine extends ModelBuilding {

    public final Ressource gold;

    public GoldMine(Map map, RessourcesHandler rsch, EntryHandler handler) {
        super(map, rsch, rsch.get("GOLDMINE"), rsch.get("CONSTRUCTION").ressource, handler);
        this.gold = new Ressource(0);
    }

    @Override
    public void update(Keyboard keyboard, Mouse mouse, float extrp) {
        super.update(keyboard, mouse, extrp);
        this.gold.update(extrp, 4.0f);
        if (this.gold.get() <= 0) {
            this.life.set(0);
        }
    }

    public boolean isEmpty() {
        return !(this.gold.get() > 0);
    }
}
