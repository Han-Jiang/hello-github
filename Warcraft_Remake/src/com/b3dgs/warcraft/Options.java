package com.b3dgs.warcraft;

import com.b3dgs.lionengine.core.Config;
import com.b3dgs.lionengine.engine.AbstractOptions;

public class Options extends AbstractOptions {

    private static final long serialVersionUID = 1L;
    private static final String[] availableLanguages = {"English"};
    private static final int[] availableRates = {0, 120, 75, 60, 50, 30, 25, 10};
    private static final int[][] availableResolutions = {
        {320, 200},
        {640, 400},
        {1280, 800}
    };

    public Options(final Launcher launcher, Config cfg) {
        super(launcher, cfg, availableResolutions, availableRates, availableLanguages);
    }
}
