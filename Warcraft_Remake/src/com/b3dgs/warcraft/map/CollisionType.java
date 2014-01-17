package com.b3dgs.warcraft.map;

public class CollisionType extends com.b3dgs.lionengine.game.platform.CollisionType {

    public static final int NONE = 0;
    public static final int GROUND = 1;
    public static final int BORDER = 2;
    public static final int WATER = 3;
    public static final int TREE = 4;

    public CollisionType(String name, int pattern, int min, int max) {
	super(name, pattern, min, max);
    }
}
