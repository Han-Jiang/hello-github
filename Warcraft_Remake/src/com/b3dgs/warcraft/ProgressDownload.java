package com.b3dgs.warcraft;

import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;
import javax.jnlp.DownloadServiceListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class ProgressDownload implements DownloadServiceListener {

    private Container container = null;
    private AppletStub stub = null;
    private JProgressBar progressBar = null;
    private boolean uiCreated = false;

    public ProgressDownload(Object surface) {
        this.init(surface, null);
    }

    public ProgressDownload(Object surface, Object stub) {
        this.init(surface, stub);
    }

    private void init(Object surface, Object stub) {
        try {
            this.container = (Container) surface;
            this.stub = (AppletStub) stub;
        } catch (ClassCastException cce) {
        }
    }

    @Override
    public void downloadFailed(java.net.URL url, java.lang.String version) {
    }

    @Override
    public void progress(URL url, String version, long readSoFar, long total, int overallPercent) {
        this.updateProgressUI(overallPercent);
    }

    @Override
    public void upgradingArchive(URL url, String version, int patchPercent, int overallPercent) {
        this.updateProgressUI(overallPercent);
    }

    @Override
    public void validating(URL url, String version, long entry, long total, int overallPercent) {
        this.updateProgressUI(overallPercent);
    }

    private void updateProgressUI(int overallPercent) {
        if (!this.uiCreated && overallPercent > 0 && overallPercent < 100) {
            this.create();
            this.uiCreated = true;
        }
        if (this.uiCreated) {
            this.progressBar.setValue(overallPercent);
        }
    }

    private void create() {
        JPanel top = createComponents();
        if (this.container != null) {
            this.container.add(top, BorderLayout.CENTER);
            this.container.invalidate();
            this.container.validate();
        }
    }

    private JPanel createComponents() {
        JPanel top = new JPanel();
        top.setBackground(Color.BLACK);
        top.setLayout(new BorderLayout(20, 20));

        Image image = new ImageIcon(this.getClass().getResource("ressources/sprites/title.jpg")).getImage();
        float ratio = this.container.getHeight() / 240.0f;
        if (ratio < 1) {
            ratio = 1.0f;
        }
        Image scale = image.getScaledInstance((int) (178 * ratio), (int) (64 * ratio), Image.SCALE_DEFAULT);
        JLabel statusLabel = new JLabel(new ImageIcon(scale));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        top.add(statusLabel, BorderLayout.CENTER);

        JPanel loader = new JPanel(new BorderLayout(20, 20));
        this.progressBar = new JProgressBar(0, 100);
        this.progressBar.setValue(0);
        this.progressBar.setStringPainted(true);
        this.progressBar.setPreferredSize(new Dimension(320, 32));
        loader.add(this.progressBar, BorderLayout.CENTER);
        top.add(loader, BorderLayout.SOUTH);

        return top;
    }
}
