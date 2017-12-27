package com.lok.game.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.lok.game.ability.Ability;

public class AbilityComponent implements Component, Poolable {
    public final Array<Ability>	abilities	      = new Array<Ability>();
    public Ability		abilityToCast	      = null;
    public Ability		currentCastingAbility = null;

    @Override
    public void reset() {
	abilities.clear();
	this.abilityToCast = null;
	this.currentCastingAbility = null;
    }

}
