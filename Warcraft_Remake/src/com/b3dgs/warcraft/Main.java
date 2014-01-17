package com.b3dgs.warcraft;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.engine.Initializer;
import com.b3dgs.lionengine.utility.Media;
import com.b3dgs.lionengine.utility.Theme;
import static com.b3dgs.lionengine.Engine.ENGINE;

public class Main {

    public static final String PROGRAM = "Warcraft Remake";
    public static final String VERSION = "0.0.6";
    private static final long serialVersionUID = 1L;

    private Main() {
    }

    public static Initializer initialize(boolean jar) {
        if (jar) {
            Media.loadFromJar(Main.class);
        }
        Engine.start(PROGRAM, VERSION, "ressources", true, Theme.SYSTEM);
        return ENGINE.createInitializer(320, 200, 32, 60);
    }

    public static void main(String[] args) {
        boolean jar = false;
        try {
            jar = Boolean.parseBoolean(args[0]);
        } catch (Exception e) {
        }
        Launcher launcher = new Launcher(null, initialize(jar));
        launcher.start();
    }
}
