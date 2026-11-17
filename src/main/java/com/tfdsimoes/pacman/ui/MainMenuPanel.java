package com.tfdsimoes.pacman.ui;

import com.tfdsimoes.pacman.GameConfig;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public final class MainMenuPanel extends JPanel {
    private static final Color GOLD = new Color(255, 220, 0);
    private final JButton playButton;
    private final JButton aiButton;

    public MainMenuPanel(Consumer<Boolean> onModeSelected) {
        Objects.requireNonNull(onModeSelected);
        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(GameConfig.MAIN_MENU_WIDTH, GameConfig.MAIN_MENU_HEIGHT));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(42, 48, 36, 48));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("PAC-MAN", SwingConstants.CENTER);
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setForeground(GOLD);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
        JLabel subtitle = new JLabel("A CLASSIC ARCADE CHALLENGE", SwingConstants.CENTER);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setForeground(new Color(179, 205, 255));
        subtitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        JLabel prompt = new JLabel("CHOOSE HOW TO PLAY", SwingConstants.CENTER);
        prompt.setAlignmentX(CENTER_ALIGNMENT);
        prompt.setForeground(Color.WHITE);
        prompt.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        playButton = createButton("PLAY YOURSELF");
        playButton.setName("playButton");
        playButton.addActionListener(event -> onModeSelected.accept(false));
        aiButton = createButton("WATCH AI PLAY");
        aiButton.setName("aiButton");
        aiButton.addActionListener(event -> onModeSelected.accept(true));

        content.add(title);
        content.add(Box.createVerticalStrut(4));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(48));
        content.add(prompt);
        content.add(Box.createVerticalStrut(22));
        content.add(playButton);
        content.add(Box.createVerticalStrut(14));
        content.add(aiButton);
        content.add(Box.createVerticalGlue());
        JLabel controls = new JLabel("ARROWS / WASD TO MOVE", SwingConstants.CENTER);
        controls.setAlignmentX(CENTER_ALIGNMENT);
        controls.setForeground(new Color(146, 164, 205));
        controls.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        content.add(controls);
        add(content, BorderLayout.CENTER);
    }

    JButton playButton() {
        return playButton;
    }

    JButton aiButton() {
        return aiButton;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D canvas = (Graphics2D) graphics.create();
        try {
            canvas.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            canvas.setPaint(new GradientPaint(0, 0, new Color(7, 13, 49),
                    getWidth(), getHeight(), new Color(18, 30, 82)));
            canvas.fillRect(0, 0, getWidth(), getHeight());
            canvas.setColor(new Color(53, 98, 202, 70));
            for (int x = 25; x < getWidth(); x += 38) {
                for (int y = 24; y < getHeight(); y += 38) {
                    canvas.fillOval(x, y, 3, 3);
                }
            }
        } finally {
            canvas.dispose();
        }
    }

    private static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(GameConfig.MAIN_MENU_BUTTON_WIDTH, GameConfig.MAIN_MENU_BUTTON_HEIGHT));
        button.setPreferredSize(new Dimension(GameConfig.MAIN_MENU_BUTTON_WIDTH, GameConfig.MAIN_MENU_BUTTON_HEIGHT));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(34, 91, 196));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(106, 165, 255), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setOpaque(true);
        return button;
    }
}
