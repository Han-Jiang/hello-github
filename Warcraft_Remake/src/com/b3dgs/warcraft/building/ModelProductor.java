package com.b3dgs.warcraft.building;

import com.b3dgs.warcraft.EntryFactory;
import com.b3dgs.warcraft.RessourcesHandler;
import com.b3dgs.warcraft.gameplay.Attributes;
import com.b3dgs.warcraft.map.Map;
import com.b3dgs.warcraft.map.Tile;
import com.b3dgs.warcraft.ability.ProducerAbilityImpl;
import com.b3dgs.warcraft.skill.ModelSkill;
import com.b3dgs.lionengine.game.MediaRessource;
import com.b3dgs.lionengine.game.strategy.ability.ProducerAbility;
import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.warcraft.gameplay.EntryHandler;
import java.awt.image.BufferedImage;

public class ModelProductor extends ModelBuilding implements ProducerAbility<Tile, ModelSkill, Attributes> {

    private final ProducerAbility<Tile, ModelSkill, Attributes> producer;

    public ModelProductor(Map map, RessourcesHandler rsch, MediaRessource<BufferedImage> rsc,
            BufferedImage construction, EntryHandler handler, EntryFactory factory) {

        super(map, rsch, rsc, construction, handler);
        this.producer = new ProducerAbilityImpl(this, handler, factory);
    }

    @Override
    public void update(Keyboard keyboard, Mouse mouse, float extrp) {
        super.update(keyboard, mouse, extrp);
        this.updateProduction();
    }

    @Override
    public void addToProductionQueue(String name, int time) {
        this.producer.addToProductionQueue(name, time);
    }

    @Override
    public void updateProduction() {
        this.producer.updateProduction();
    }

    @Override
    public void setRallyLocation(int dx, int dy) {
        this.producer.setRallyLocation(dx, dy);
    }

    @Override
    public int getProductionProgress() {
        return this.producer.getProductionProgress();
    }

    @Override
    public String getProducingElement() {
        return this.producer.getProducingElement();
    }

    @Override
    public void stopProduction() {
        this.producer.stopProduction();
    }

    @Override
    public int getQueueLength() {
        return this.producer.getQueueLength();
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        this.stopProduction();
    }
}
