package com.quoc.webspire.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class FloatingTextManager {

    public static void spawnText(Stage stage, BitmapFont font, String text, Color color, float startX, float startY) {
        Label.LabelStyle style = new Label.LabelStyle(font, color);
        Label label = new Label(text, style);
        label.setPosition(startX, startY);

        label.addAction(Actions.sequence(
            Actions.parallel(
                Actions.moveBy(0, 45f, 0.75f, Interpolation.pow2Out),
                Actions.fadeOut(0.75f, Interpolation.pow2In)
            ),
            Actions.removeActor()
        ));

        stage.addActor(label);
    }
}
