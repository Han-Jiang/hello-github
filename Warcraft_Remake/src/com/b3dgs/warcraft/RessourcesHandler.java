package com.b3dgs.warcraft;

import com.b3dgs.lionengine.audio.Sound;
import com.b3dgs.lionengine.game.AbstractRessourcesHandler;
import com.b3dgs.lionengine.game.MediaRessource;
import com.b3dgs.lionengine.utility.Media;
import com.b3dgs.warcraft.gameplay.Race;
import java.awt.image.BufferedImage;
import static com.b3dgs.lionengine.Audio.AUDIO;

/**
 * This class will handle unique instance for each entry type.<br/>
 * It is a good way to share gfx (all warriors will use the same gfx...
 * instead of having a copy for each of them !).<br/>
 * We will also declare a reference for our main ressources directories
 * (be a good programmer, avoid magic vars in your code; static keyword is for that).
 */
public class RessourcesHandler extends AbstractRessourcesHandler<BufferedImage> {

    //********************************* Roots dir *********************************//
    public static final String SPRITES_DIR = "sprites";
    public static final String BUILDINGS_DIR = "buildings";
    public static final String UNITS_DIR = "units";
    public static final String TILES_DIR = "tiles";
    public static final String LEVELS_DIR = "levels";
    public static final String SFX_DIR = "sfx";
    public static final String MUSICS_DIR = "musics";
    //********************************* Sound fx **********************************//
    public static final Sound[][] SND_SELECT = getRacedSounds(SFX.select, 4);
    public static final Sound[][] SND_CONFIRM = getRacedSounds(SFX.confirm, 2);
    public static final Sound[][] SND_DIE = getRacedSounds(SFX.die, 1);
    public static final Sound[] SND_HIT = getSounds(SFX.hit, 3);
    public static final Sound[] SND_ARROW_HIT = getSounds(SFX.arrow_hit, 1);
    public static final Sound[] SND_ARROW_THROWN = getSounds(SFX.arrow_thrown, 1);
    public static final Sound[] SND_CONSTRUCTION = getSounds(SFX.construction, 1);
    public static final Sound[] SND_DECONSTRUCTION = getSounds(SFX.deconstruction, 2);
    public static final Sound[] SND_CLICK = getSounds(SFX.click, 1);
    public static final Sound[] SND_VALIDED = getSounds(SFX.valided, 1);
    //*****************************************************************************//

    private static Sound[][] getRacedSounds(SFX prefix, int num) {
        Sound[][] sounds = new Sound[2][num];
        for (int i = 1; i <= num; i++) {
            sounds[0][i - 1] = AUDIO.loadSound(Media.get(SFX_DIR, Race.humans + "_" + prefix.name() + i + ".wav"));
            sounds[1][i - 1] = AUDIO.loadSound(Media.get(SFX_DIR, Race.orcs + "_" + prefix.name() + i + ".wav"));
        }
        return sounds;
    }

    private static Sound[] getSounds(SFX prefix, int num) {
        Sound[] sounds = new Sound[num];
        for (int i = 1; i <= num; i++) {
            sounds[i - 1] = AUDIO.loadSound(Media.get(SFX_DIR, prefix.name() + i + ".wav"));
        }
        return sounds;
    }

    public RessourcesHandler() {
        super();
        this.add("GOLDMINE", Media.get(BUILDINGS_DIR, Race.neutral.name(), "01"));

        this.add("HUMANS_TOWNHALL", getHumansBuilding("01"));
        this.add("HUMANS_FARM", getHumansBuilding("02"));
        this.add("HUMANS_BARRACKS", getHumansBuilding("03"));
        this.add("HUMANS_LUMBERMILL", getHumansBuilding("04"));

        this.add("PEASANT", getHumansUnit("01"));
        this.add("FOOTMAN", getHumansUnit("02"));
        this.add("ARCHER", getHumansUnit("03"));

        this.add("ORCS_TOWNHALL", getOrcsBuilding("01"));
        this.add("ORCS_FARM", getOrcsBuilding("02"));
        this.add("ORCS_BARRACKS", getOrcsBuilding("03"));
        this.add("ORCS_LUMBERMILL", getOrcsBuilding("04"));

        this.add("PEON", getOrcsUnit("01"));
        this.add("GRUNT", getOrcsUnit("02"));
        this.add("SPEARMAN", getOrcsUnit("03"));

        this.add("CONSTRUCTION", getSprite("construction"));
        this.add("ARROW", getSprite("arrow"));
        this.add("CORPSE", getSprite("corpse"));
        this.add("BURNING", getSprite("burning"));
        this.add("EXPLODE", getSprite("explode"));
    }

    private void add(String id, String file) {
        super.add(id, new MediaRessource<BufferedImage>(file + ".txt", this.getImage(file + ".png", false)));
    }

    public String getFilename(String id) {
        return this.get(id).file;
    }

    public BufferedImage getBufImage(String id) {
        return this.get(id).ressource;
    }

    public static String getHumansBuilding(String id) {
        return Media.get(BUILDINGS_DIR, Race.humans.name(), id);
    }

    public static String getHumansUnit(String id) {
        return Media.get(UNITS_DIR, Race.humans.name(), id);
    }

    public static String getOrcsBuilding(String id) {
        return Media.get(BUILDINGS_DIR, Race.orcs.name(), id);
    }

    public static String getOrcsUnit(String id) {
        return Media.get(UNITS_DIR, Race.orcs.name(), id);
    }

    public static String getSprite(String id) {
        return Media.get(SPRITES_DIR, id);
    }
}
