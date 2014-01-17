package com.b3dgs.warcraft.gameplay;

/**
 * Informations related to a construction.
 * Data are loaded from costs.xml file.
 */
public class Cost {

    public final Ressource gold;
    public final Ressource wood;
    public final int time, tw, th;

    public Cost(int gold, int wood, int time, int tw, int th) {
	this.gold = new Ressource(gold);
	this.wood = new Ressource(wood);
	this.time = time;
	this.tw = tw;
	this.th = th;
    }
}
