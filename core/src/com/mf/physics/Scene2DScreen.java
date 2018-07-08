package com.mf.physics;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Scene2DScreen implements Screen {
	
	final Physics game;
	
	BitmapFont anita12;
	
	Stage stage;
	Skin skin;
	Table table;
	
	float stageHeight = 800;
	float stageWidth = Gdx.graphics.getWidth()*800/Gdx.graphics.getHeight();
	
	OrthographicCamera camera;
	
	
	
	public Scene2DScreen(Physics gam) {
		game = gam;
		
		float[] testing = new float[] {2, 8, 1, 9, 10, 3};
		Arrays.sort(testing);
		for(int i = testing.length - 1; i >= 0; i--) {
			System.out.println(testing[i]);
		}
		System.out.println(testing.length);
		
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Anita semi square.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;
		anita12 = generator.generateFont(parameter);
		anita12.setColor(1, 1, 1, 1);
		generator.dispose();
		
		
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, stageWidth, stageHeight);
		camera.position.set(0, 0, 0);
		
		stage = new Stage();
		
		stage.setViewport(new ExtendViewport(1200, 600));
		stage.getViewport().setCamera(camera);
		//stage.getViewport().setScreenSize((int) stageWidth, (int) stageHeight);
		
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		table = new Table(skin);
		
		Label label = new Label("Press the Orb to begin", skin);
		label.setFillParent(false);
		
		Label label2 = new Label("Score", skin);
		label2.setText("You did not win");
		label2.setPosition(-stageWidth/2, stageHeight/2 - label2.getHeight());
		label2.setColor(Color.BLACK);
		label2.setScale(5);
		stage.addActor(label2);
		
		Image image = new Image(new TextureRegion(new Texture(Gdx.files.internal("freefall.png"))));
		image.setFillParent(false);
		image.scaleBy(0.002f);
		image.setPosition(-image.getWidth()/2, 120);
		
		stage.addActor(image);
		
		
		table.setFillParent(false);
		table.align(Align.center);
		table.setY(-190);
		
		//table.add().pad(200).colspan(2);
		//table.row();
		table.add("1").pad(3);
		table.add("9380");
		table.row();
		table.add("2").pad(3);
		table.add("47");;
		table.row();
		table.add("3").pad(3);
		table.add("12");;
		table.row();
		table.add().pad(5).colspan(2);
		table.row();
		//table.add(image);
		table.row();
		table.add(label).colspan(2);
		
		table.setDebug(false);
		
		stage.addActor(table);
		
		//Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
	}

}
