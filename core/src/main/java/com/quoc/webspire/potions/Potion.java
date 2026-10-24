package com.quoc.webspire.potions;

import com.badlogic.gdx.graphics.Color;
import com.quoc.webspire.entities.Character;
import com.quoc.webspire.entities.Enemy;

public interface Potion {
    String getName();
    String getDescription();
    Color getColor();
    void use(Character player, Enemy monster);
}
