package com.mf.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class TiledLevelScreen implements Screen {
	
	final Physics game;
	
	OrthographicCamera camera;
	float pixelsPerUnit = Gdx.graphics.getHeight()/20;
	
	World world;
	Box2DDebugRenderer debugRenderer;
	
	Body playerBody;
	
	private Texture playerTexture;
	//private SpriteBatch batch;
	private Sprite playerSprite;
	
	public Vector3 touchPos;
	
	public TiledLevelScreen (Physics gam) {
		game = gam;
		
		camera = new OrthographicCamera();
		
		camera.setToOrtho(false, Gdx.graphics.getWidth()*20/Gdx.graphics.getHeight(), 20);
		
		Box2D.init();
		world = new World(new Vector2(0, -5.7f), true);
		debugRenderer = new Box2DDebugRenderer();
		
		shapeRenderer = new ShapeRenderer();
		
		stretchSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Shephard.wav"));
		stretchSound.setLooping(0, true);
		stretchSound.loop();
		stretchSound.pause();
		
		
		CreatePlayerBody();
		CreateWorldBody();
		
		touchPos = new Vector3(playerBody.getPosition().x, playerBody.getPosition().y + 3, 0);
		attachment.set(touchPos.x, touchPos.y);
		
		playerTexture = new Texture(Gdx.files.internal("Guy.png"));
		playerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		game.batch = new SpriteBatch();
		
		playerSprite = new Sprite(playerTexture);
		playerSprite.setSize(1*pixelsPerUnit, 1*pixelsPerUnit);
		playerSprite.setOrigin(playerSprite.getWidth()/2, playerSprite.getHeight()/2);
		//playerSprite.setScale(1, 1);
		//playerSprite.setOrigin(0.5f, 0.5f);
		playerSprite.setCenter(playerBody.getPosition().x*pixelsPerUnit, playerBody.getPosition().y*pixelsPerUnit);
		
		playerCameraPosition = new Vector2();
	}
	
	private void CreatePlayerBody() {
		//First set a definition. (Body shape)
		BodyDef playerDef = new BodyDef();
		//Set the body to dynamic.
		playerDef.type = BodyType.DynamicBody;
		playerDef.position.set(5, 5);
		//Add body to the world.
		playerBody = world.createBody(playerDef);
				
		CircleShape circle = new CircleShape();
		circle.setRadius(0.5f);
				
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 2.3f;
		fixtureDef.friction = 20f;
		fixtureDef.restitution = 0.1f;
				
		Fixture fixture = playerBody.createFixture(fixtureDef);
		
		playerBody.setLinearDamping(0.23f);
				
		//FixtureDef and BodyDef don't need disposing, but for some reason shapes do. 
		circle.dispose();
	}
	
	Body worldBody;
	
	private void CreateWorldBody() {
		BodyDef worldDef = new BodyDef();
		worldDef.type = BodyType.StaticBody;
		worldDef.position.set(12,10);
		worldBody = world.createBody(worldDef);
		
		PolygonShape worldShape = new PolygonShape();
		worldShape.setAsBox(3, 3);
		worldBody.createFixture(worldShape, 3);
		worldShape.dispose();
		
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0.2f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		world.step(1/45f, 6, 2);
		
		debugRenderer.render(world, camera.combined);
		playerMove();

		renderPlayer();
		
		if(Gdx.input.isKeyJustPressed(Keys.L) || Gdx.input.isTouched(3)) {
			game.setScreen(new OrientationScreen(game));
		}

		//cameraMove(delta);
	}
	
	ShapeRenderer shapeRenderer;
	Sound stretchSound;
	float cameraSpeed = 1;
	float cameraDistance = 0;
	
	public void cameraMove(float delta) {
		System.out.println(camera.position.x);
		System.out.println(Gdx.graphics.getWidth()/pixelsPerUnit);
		
		camera.translate(cameraSpeed*delta, 0, 0);
		cameraDistance = camera.position.x - camera.viewportWidth/2;
	}
	
	RayCastCallback ray = new RayCastCallback() {

		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point,
				Vector2 normal, float fraction) {
			// TODO Auto-generated method stub
			click.set(point);
			System.out.println(point.x + "and" + point.y);
			return 0;
		}
		
	};
	
	Vector2 attachment = new Vector2();
	Vector2 click = new Vector2();

	public void playerMove() {
		/*
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			playerBody.applyForce(0, 30, playerBody.getPosition().x, playerBody.getPosition().y, true);
		}
		else if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			playerBody.applyForce(0, -20, playerBody.getPosition().x, playerBody.getPosition().y, true);
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT) && playerBody.getLinearVelocity().x < 6) {
			playerBody.applyForce(8, 0, playerBody.getPosition().x, playerBody.getPosition().y, true);
		}
		else if(Gdx.input.isKeyPressed(Keys.LEFT) && playerBody.getLinearVelocity().x > -6) {
			playerBody.applyForce(-8, 0, playerBody.getPosition().x, playerBody.getPosition().y, true);
		}
		
		else if(!Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.LEFT) && (Math.abs(playerBody.getLinearVelocity().y) < 0.01f)) {
			playerBody.setLinearVelocity(0, playerBody.getLinearVelocity().y);
		}*/
		
		//Vector3 touchPos = new Vector3();
		
		if(Gdx.input.isTouched() && !Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			click.set(0, 0);
			world.rayCast(ray, playerBody.getPosition(), new Vector2(touchPos.x, touchPos.y));
			
		}
		else if(Gdx.input.isTouched(1) || Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			click.set(-1, -1);
			attachment.set(click);
		}
		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
		
		
		//if click is positive, then copy to attachment and do the force thing.
		if(click.x > 0) {
			attachment.set(click);
		}
		//if click is -1, don't do the force thing.
		//if click is 0, do the force thing, but don't copy to attachment.
		
		if(click.x >= 0) {
			if(attachment.x > 0) {
			Vector3 objectPos = new Vector3(playerBody.getPosition().x, playerBody.getPosition().y, 0);
			
			Vector3 forceVector = new Vector3(CalculateForce(objectPos, new Vector3(attachment.x, attachment.y, 0)));
		
			playerBody.applyForceToCenter(forceVector.x, forceVector.y, true);
		
			shapeRenderer.setProjectionMatrix(camera.combined);
			
			shapeRenderer.line(attachment.x, attachment.y, objectPos.x, objectPos.y);
			}
		}

		
		
		Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mousePos);
		
		shapeRenderer.line(mousePos.x, mousePos.y, playerBody.getPosition().x, playerBody.getPosition().y);
		shapeRenderer.end();
		
	}	
	
	Vector2 playerCameraPosition;
	
	public void renderPlayer() {
		//playerWorldPosition.set(playerBody.getPosition().x, playerBody.getPosition().y);
		
		//playerSprite.setCenter(playerBody.getPosition().x*pixelsPerUnit, playerBody.getPosition().y*pixelsPerUnit);
		//System.out.println("Rendering");
		
		playerCameraPosition.set(playerBody.getPosition().x-camera.position.x, playerBody.getPosition().y-camera.position.y);
		playerSprite.setCenter((playerBody.getPosition().x-cameraDistance)*pixelsPerUnit, playerBody.getPosition().y*pixelsPerUnit);
		
		game.batch.begin();
		playerSprite.draw(game.batch);
		game.batch.end();
		
	}
	
	public Vector3 CalculateForce(Vector3 coord1, Vector3 coord2) {
		//Object Position
		float x1 = coord1.x;
		float y1 = coord1.y;
		
		//Target Position
		float x2 = coord2.x;
		float y2 = coord2.y;
		
		//Build RAT with width and height.
		float x = x2 - x1;
		float y = y2 - y1;
		
		//The linear force to apply. 
		float targetForce = 24;
		float weakerForce = 3f;
		float minDistance = 3.5f;
		
		float hypotenuse = (float) Math.sqrt((double)(x*x)+(y*y));
		
		//If the hypotenuse is smaller than the minDistance, apply the weakerForce instead.
		float factor = 0;
		
		if(hypotenuse > minDistance) {
			factor = targetForce/hypotenuse;
		}
		else {
			factor = weakerForce/hypotenuse;
		}
		
		//float factor = targetForce/hypotenuse;
		
		return new Vector3(x*factor, y*factor, 0);
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
		playerTexture.dispose();
		world.dispose();
		debugRenderer.dispose();
		//batch.dispose();
		shapeRenderer.dispose();
	}

}
