package com.quoc.webspire.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.quoc.webspire.managers.RunState;
import com.quoc.webspire.relics.Relic;

import java.util.List;

public class RelicBar extends Table {
    private final BitmapFont font;
    private Label tooltipLabel;

    public RelicBar(BitmapFont font, Label tooltipLabel) {
        this.font = font;
        this.tooltipLabel = tooltipLabel;
        refresh();
    }

    public void refresh() {
        clearChildren();

        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.GOLD);
        Label title = new Label("Cổ vật: ", titleStyle);
        add(title).padRight(8);

        List<Relic> relics = RunState.getInstance().getPlayer().getRelics();
        Label.LabelStyle relicStyle = new Label.LabelStyle(font, Color.ORANGE);

        for (Relic relic : relics) {
            Label relicLabel = new Label("[" + relic.getName() + "]", relicStyle);

            relicLabel.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (pointer == -1 && tooltipLabel != null) {
                        tooltipLabel.setText(relic.getName() + ": " + relic.getDescription());
                        tooltipLabel.setVisible(true);
                    }
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    if (pointer == -1 && tooltipLabel != null) {
                        tooltipLabel.setVisible(false);
                    }
                }
            });

            add(relicLabel).padRight(10);
        }
    }
}
