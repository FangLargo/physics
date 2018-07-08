package com.mf.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class MainMenuScreen implements Screen {
	
	final Physics game;
	
	//In my world, one Box2D unit is one metre. 
	World world;
	OrthographicCamera camera;
	
	Box2DDebugRenderer debugRenderer;
	
	int pixelsToMetres;
	
	Body playerBody;
	Body groundBody;
	Body wallBody;
	Body elevatorBody;
	
	public MainMenuScreen(Physics gam) {
		game = gam;
		
		Box2D.init();
		
		world = new World(new Vector2(0, -10), true);
		debugRenderer = new Box2DDebugRenderer();
		
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth()*20/Gdx.graphics.getHeight(), 20);

		
		//First set a definition. (Body shape)
		BodyDef playerDef = new BodyDef();
		//Set the body to dynamic.
		playerDef.type = BodyType.DynamicBody;
		playerDef.position.set(5, 5);
		//Add body to the world.
		playerBody = world.createBody(playerDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(1f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.5f;
		fixtureDef.friction = 20f;
		fixtureDef.restitution = 0.1f;
		
		Fixture fixture = playerBody.createFixture(fixtureDef);
		
		//FixtureDef and BodyDef don't need disposing, but for some reason shapes do. 
		circle.dispose();
		
		//Doing the same for the ground.
		//Set BodyDef and position.
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(17,2);
		
		groundBody = world.createBody(groundDef);
		//Make a shape.
		PolygonShape groundShape = new PolygonShape();
		//The polygon is now a box.
		groundShape.setAsBox(16, 0.5f);
		//The shape is added to the fixture in the body...
		groundBody.createFixture(groundShape, 0.2f);
		groundShape.dispose();
		
		
		
		/*
		 * Here is what I understand:
		 * Body = The thing bouncing around.
		 * BodyDef = The various software properties of the body.
		 * Shape = The geometric properties of the body.
		 * Fixture/FixtureDef = The physical properties of the body.
		 * 
		 * 1. Make a BodyDef.
		 * 2. Add the Body to the world with the BodyDef.
		 * 3. Create a Shape.
		 * 4. Create a FixtureDef and add the Shape to it. 
		 * 5. Create a Fixture to assign to the body as a FixtureDef, 
		 * ala Fixture fixture = playerBody.createFixture(fixtureDef);;
		 */
		
		BodyDef elevatorBodyDef = new BodyDef();
		elevatorBodyDef.position.set(17,2);
		elevatorBodyDef.type = BodyType.KinematicBody;
		elevatorBody = world.createBody(elevatorBodyDef);
		PolygonShape elevatorShape = new PolygonShape();
		elevatorShape.setAsBox(3, 0.3f);
		elevatorBody.createFixture(elevatorShape, 0.2f);
		elevatorShape.dispose();
		
		elevatorBody.setLinearVelocity(0, 0.8f);
		
		
		world.setContactListener(new CollisionListener());
		
	}
	
	public class CollisionListener implements ContactListener {

		public CollisionListener() {
			System.out.println("Listening for collisions.");
		}
		
		@Override
		public void beginContact(Contact contact) {
			// TODO Auto-generated method stub
			Body a = contact.getFixtureA().getBody();
			Body b = contact.getFixtureB().getBody();
			
			/*
			if((a.getType() == BodyType.DynamicBody && b.getType() == BodyType.KinematicBody) || 
					(a.getType() == BodyType.KinematicBody && b.getType() == BodyType.DynamicBody)) {
				System.out.println("On Elevator");
			}*/
			if((a.getType() == BodyType.DynamicBody || 
					b.getType() == BodyType.DynamicBody) && 
					!Gdx.input.isKeyPressed(Keys.RIGHT) && 
					!Gdx.input.isKeyPressed(Keys.LEFT) &&
					!Gdx.input.isTouched()) {
				playerBody.setLinearVelocity(0, playerBody.getLinearVelocity().y);
				System.out.println("Colli-ai-aide!");
			}
			
		}

		@Override
		public void endContact(Contact contact) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
			// TODO Auto-generated method stub
			Body a = contact.getFixtureA().getBody();
			Body b = contact.getFixtureB().getBody();
			
			if((a.getType() == BodyType.DynamicBody || 
					b.getType() == BodyType.DynamicBody) && 
					!Gdx.input.isKeyPressed(Keys.RIGHT) && 
					!Gdx.input.isKeyPressed(Keys.LEFT)) {
				playerBody.setLinearVelocity(0, playerBody.getLinearVelocity().y);
				System.out.println("Colli-ai-aide!");
			}
			
		}

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
			// TODO Auto-generated method stub
			
		}
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		world.step(1/45f, 6, 2);
		
		debugRenderer.render(world, camera.combined);
		
		elevatorMove();
		playerMove();
		
		if(Gdx.input.isKeyJustPressed(Keys.L) || Gdx.input.isTouched(3)) {
			game.setScreen(new TiledLevelScreen(game));
		}

	}
	
	public void elevatorMove() {
		if(elevatorBody.getPosition().y > 7f) {
			elevatorBody.setLinearVelocity(0, -0.8f);
		}
		else if (elevatorBody.getPosition().y < 0.5) {
			elevatorBody.setLinearVelocity(0, 0.8f);
		}
	}
	
	public void playerMove() {
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
		/*
		else if(!Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.LEFT) && (Math.abs(playerBody.getLinearVelocity().y) < 0.01f)) {
			playerBody.setLinearVelocity(0, playerBody.getLinearVelocity().y);
		}*/
		
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			
			Vector3 objectPos = new Vector3(playerBody.getPosition().x, playerBody.getPosition().y, 0);
			
			Vector3 forceVector = new Vector3(CalculateForce(objectPos, touchPos));
			
			playerBody.applyForceToCenter(forceVector.x, forceVector.y, true);
		}
	}
	
	public Vector3 CalculateForce(Vector3 coord1, Vector3 coord2) {
		float x1 = coord1.x;
		float y1 = coord1.y;
		
		float x2 = coord2.x;
		float y2 = coord2.y;
		
		float x = x2 - x1;
		float y = y2 - y1;
		
		float targetForce = 45;
		
		float hypotenuse = (float) Math.sqrt((double)(x*x)+(y*y));
		
		float factor = targetForce/hypotenuse;
		
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

	}

}
