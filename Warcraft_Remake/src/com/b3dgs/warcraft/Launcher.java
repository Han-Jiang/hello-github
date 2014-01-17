package com.b3dgs.warcraft;

import com.b3dgs.lionengine.engine.AbstractLauncher;
import com.b3dgs.lionengine.engine.Initializer;
import com.b3dgs.lionengine.engine.Loader;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import static com.b3dgs.lionengine.Engine.ENGINE;
import static com.b3dgs.lionengine.utility.Swing.addButton;

public class Launcher extends AbstractLauncher {

    private static final long serialVersionUID = 1L;

    public Launcher(final Applet applet, final Initializer initializer) {
        super(initializer, 320, 240);

        this.loadOptions();
        JPanel centerPanel = new JPanel(new GridLayout(2, 2));
        this.mainPanel.add(centerPanel, BorderLayout.CENTER);

        addButton("Warcraft Remake Demo", centerPanel, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Loader game = ENGINE.createLoader(initializer, config, Launcher.this);
                startSequence(new Menu(game), game);
            }
        });

        JPanel southPanel = new JPanel();
        this.mainPanel.add(southPanel, BorderLayout.SOUTH);
        addButton("Options", southPanel, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Options options = new Options(Launcher.this, config);
                options.start();
            }
        });
        addButton("Exit", southPanel, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                saveOptions();
                if (applet == null) {
                    System.exit(0);
                } else {
                    applet.terminate();
                }
            }
        });
    }
}
