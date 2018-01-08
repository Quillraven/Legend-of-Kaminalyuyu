package com.lok.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Animation extends com.badlogic.gdx.graphics.g2d.Animation<TextureRegion> {
    private static final String TAG = Animation.class.getSimpleName();

    public enum AnimationID {
	PLAYER_IDLE,
	PLAYER_WALK_LEFT,
	PLAYER_WALK_RIGHT,
	PLAYER_WALK_UP,
	PLAYER_WALK_DOWN,

	DEMON_01_IDLE,
	DEMON_01_WALK_LEFT,
	DEMON_01_WALK_RIGHT,
	DEMON_01_WALK_UP,
	DEMON_01_WALK_DOWN,

	BOSS_01_IDLE,
	BOSS_01_WALK_LEFT,
	BOSS_01_WALK_RIGHT,
	BOSS_01_WALK_UP,
	BOSS_01_WALK_DOWN,

	TOWNPORTAL,

	SELECTION_SPHERE;
    }

    private static Array<Animation> animationCache = null;

    public Animation(float frameDuration, Array<? extends TextureRegion> keyFrames) {
	super(frameDuration, keyFrames, PlayMode.NORMAL);
    }

    public static void initializeAnimationCache(AssetManager assetManager) {
	if (animationCache == null) {
	    Gdx.app.debug(TAG, "Initializing animation cache");
	    animationCache = new Array<Animation>();
	    for (AnimationID aniID : AnimationID.values()) {
		animationCache.add(assetManager.get(aniID.name(), Animation.class));
	    }
	} else {
	    Gdx.app.error(TAG, "Animation cache is initialized multiple times");
	}
    }

    public static Animation getAnimation(AnimationID animationID) {
	return animationCache.get(animationID.ordinal());
    }

}