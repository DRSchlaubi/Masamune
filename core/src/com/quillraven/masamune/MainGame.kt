package com.quillraven.masamune

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Colors
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.viewport.FitViewport
import com.quillraven.masamune.ecs.ComponentMapper
import com.quillraven.masamune.ecs.ECSEngine
import com.quillraven.masamune.event.GameEventManager
import com.quillraven.masamune.map.MapManager
import com.quillraven.masamune.physic.B2DUtils
import com.quillraven.masamune.physic.Q2DContactListener
import com.quillraven.masamune.screen.LoadingScreen
import com.quillraven.masamune.screen.Q2DScreen
import com.quillraven.masamune.serialization.Q2DSerializer


private const val TAG = "Main"
internal const val UNIT_SCALE = 1 / 32f
private const val SKIN_ATLAS_PATH = "ui/ui.atlas"
private const val SKIN_PATH = "ui/ui.json"
private const val FONT_PATH = "ui/font.ttf"

class MainGame : Q2DGame() {
    internal val gameViewPort by lazy { FitViewport(16f, 9f) }

    internal val shaderOutline by lazy {
        val prog = ShaderProgram(assetManager.fileHandleResolver.resolve("shader/outline/vertex.glsl"), assetManager.fileHandleResolver.resolve("shader/outline/fragment.glsl"))
        if (!prog.isCompiled) throw GdxRuntimeException("Could not compile shaderOutline ${prog.log}")
        prog
    }

    internal val batch by lazy { SpriteBatch(1000) }
    internal val stage by lazy { Stage(FitViewport(1280f, 720f), batch) }

    internal val skin by lazy {
        val resources = ObjectMap<String, Any>()
        val fontGenerator = FreeTypeFontGenerator(assetManager.fileHandleResolver.resolve(FONT_PATH))
        val fontParam = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParam.minFilter = Texture.TextureFilter.Linear
        fontParam.magFilter = Texture.TextureFilter.Linear
        for (size in 23..50 step 9) {
            fontParam.size = size
            val font = fontGenerator.generateFont(fontParam)
            font.data.markupEnabled = true
            resources.put("font_$size", font)
        }
        fontGenerator.dispose()

        Colors.put("DIALOG_TITLE_LIGHT", Color(0x784e2cff))
        Colors.put("HIGHLIGHT", Color(0xff6a00ff.toInt()))
        Colors.put("BLACK", Color.BLACK)
        Colors.put("WHITE", Color.WHITE)
        Colors.put("GREEN", Color.GREEN)
        Colors.put("BLUE", Color.BLUE)
        Colors.put("GOLD", Color.GOLDENROD)
        Colors.put("RED", Color.RED)
        Colors.put("LIME", Color.LIME)
        Colors.put("SKY", Color.SKY)

        val skinParameter = SkinLoader.SkinParameter(SKIN_ATLAS_PATH, resources)
        assetManager.load(SKIN_PATH, Skin::class.java, skinParameter)
        assetManager.finishLoading()
        assetManager.get(SKIN_PATH, Skin::class.java)
    }

    internal val ecsEngine by lazy { ECSEngine(this) }
    internal val cmpMapper by lazy { ComponentMapper() }

    internal val world by lazy {
        val contactListener = Q2DContactListener(this)
        World(Vector2(0f, 0f), true).apply { setContactListener(contactListener) }
    }
    internal val b2dUtils by lazy { B2DUtils(world) }

    internal val assetManager by lazy { AssetManager() }

    internal val gameEventManager by lazy { GameEventManager() }

    internal val mapManager by lazy { MapManager(this) }

    internal val json by lazy { Json(JsonWriter.OutputType.minimal) }
    internal val serializer by lazy { Q2DSerializer(this) }

    internal val resourceBundle by lazy {
        assetManager.load("i18n/i18n", I18NBundle::class.java)
        assetManager.finishLoading()
        assetManager.get("i18n/i18n", I18NBundle::class.java)
    }

    override fun initialize() {
        Gdx.input.inputProcessor = stage
    }

    override fun getFirstScreenType(): Class<out Q2DScreen> {
        return LoadingScreen::class.java
    }

    override fun dispose() {
        Gdx.app.debug(TAG, "Maximum sprites in batch: ${batch.maxSpritesInBatch}. Current batch size is 1000. Increase it if max is higher.")

        ecsEngine.dispose()
        batch.dispose()
        shaderOutline.dispose()
        stage.dispose()
        world.dispose()
        super.dispose()
    }
}
