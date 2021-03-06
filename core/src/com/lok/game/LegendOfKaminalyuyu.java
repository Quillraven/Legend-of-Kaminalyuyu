package com.lok.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.lok.game.assets.loader.AnimationLoader;
import com.lok.game.assets.loader.ConversationLoader;
import com.lok.game.assets.loader.EntityConfigurationLoader;
import com.lok.game.assets.loader.MapLoader;
import com.lok.game.conversation.Conversation;
import com.lok.game.ecs.EntityConfiguration;
import com.lok.game.map.Map;
import com.lok.game.screen.AssetsLoadingScreen;
import com.lok.game.screen.GameScreen;
import com.lok.game.screen.Screen;
import com.lok.game.screen.TownScreen;
import com.lok.game.ui.Animation;

public class LegendOfKaminalyuyu extends Game {
    private final static String				     TAG = LegendOfKaminalyuyu.class.getSimpleName();

    private AssetManager				     assetManager;
    private I18NBundle					     localizationBundle;
    private Skin					     uiSkin;

    private ObjectMap<Class<? extends Screen<?>>, Screen<?>> screenCache;
    private Screen<?>					     nextScreen;

    @Override
    public void create() {
	Gdx.app.setLogLevel(Application.LOG_DEBUG);

	assetManager = new AssetManager();
	assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
	assetManager.setLoader(Animation.class, new AnimationLoader(new InternalFileHandleResolver()));
	assetManager.setLoader(Map.class, new MapLoader(new InternalFileHandleResolver()));
	assetManager.setLoader(Conversation.class, new ConversationLoader(new InternalFileHandleResolver()));
	assetManager.setLoader(EntityConfiguration.class, new EntityConfigurationLoader(new InternalFileHandleResolver()));

	// load labels
	assetManager.load("localization/Labels", I18NBundle.class);
	assetManager.finishLoading();
	localizationBundle = assetManager.get("localization/Labels", I18NBundle.class);

	// load UI skin
	assetManager.load("ui/ui.json", Skin.class, new SkinLoader.SkinParameter("ui/ui.atlas"));
	assetManager.load("ui/village.jpg", Texture.class);
	assetManager.finishLoading();
	uiSkin = assetManager.get("ui/ui.json", Skin.class);
	uiSkin.add("village-bgd", new Image(new TextureRegionDrawable(new TextureRegion(assetManager.get("ui/village.jpg", Texture.class)))), Image.class);

	// set custom colors used for markup language for any UI text
	Colors.put("Highlight", new Color(0x4250f4ff));
	Colors.put("Thought", new Color(0x9fa2a3ff));

	Gdx.graphics.setTitle(getLabel("GameWindow.Title"));
	screenCache = null;
	this.nextScreen = new AssetsLoadingScreen(this, assetManager, uiSkin);
    }

    public AssetManager getAssetManager() {
	return assetManager;
    }

    public Skin getUISkin() {
	return uiSkin;
    }

    public String getLabel(String labelKey) {
	return localizationBundle.format(labelKey);
    }

    @Override
    public void setScreen(com.badlogic.gdx.Screen screen) {
	Gdx.app.error(TAG, "setScreen is called directly. setScreen(Class<? extends Screen> needs to be called instead!");
	return;
    }

    public void setScreen(Class<? extends Screen<?>> type) {
	if (screenCache == null) {
	    Gdx.app.debug(TAG, "Initializing screen cache");
	    screenCache = new ObjectMap<Class<? extends Screen<?>>, Screen<?>>();
	    screenCache.put(TownScreen.class, new TownScreen(this, assetManager, uiSkin));
	    screenCache.put(GameScreen.class, new GameScreen(this, assetManager, uiSkin));
	}

	final Screen<?> screen = screenCache.get(type);

	if (screen == null) {
	    Gdx.app.error(TAG, "Trying to set a screen that is not loaded into the cache yet: " + type);
	    return;
	}

	this.nextScreen = screen;
    }

    @Override
    public void render() {
	if (screen == null && nextScreen == null) {
	    return;
	}

	if ((screen == null && nextScreen != null) || !screen.equals(nextScreen)) {
	    if (this.screen != null) {
		this.screen.hide();
	    }
	    this.screen = nextScreen;
	    if (this.screen != null) {
		this.screen.show();
		this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    }
	}

	if (screen != null) {
	    // use raw delta time instead of deltatime because deltaTime
	    // uses an average value instead of the real value between two frames
	    screen.render(Gdx.graphics.getRawDeltaTime());
	}
    }

    @Override
    public void dispose() {
	uiSkin.dispose();
	assetManager.dispose();
	if (screenCache != null) {
	    for (Screen<?> screen : screenCache.values()) {
		screen.dispose();
	    }
	} else if (screen != null) {
	    screen.dispose();
	}
    }
}
