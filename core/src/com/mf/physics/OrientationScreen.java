package com.mf.physics;

import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class OrientationScreen implements Screen {

	final Physics game;
	
	OrthographicCamera camera;
	
	ShapeRenderer shapeRenderer;

	//Height is always 150 units high. 
	float screenHeight = 150;
	float pixelsPerUnit = Gdx.graphics.getHeight()/screenHeight;
	
	//Player Related Things
	Polygon playerPolygon;
	Vector2 playerLocation = new Vector2();
	Vector2 playerSquare = new Vector2();
	float velocity = -35;
	float radius = 7;
	float distance = 0;
	
	//ObstacleField obstacleField;
	Array<ObstacleField> obstacleFields = new Array<ObstacleField>();
	
	//Stage Stuff
	Stage stage;
	Skin skin;
	Table setupTable;
	Table overTable;
	Image logo;
	
	Label firstLabel;
	Label secondLabel;
	Label thirdLabel;
	Label currentLabel;
	
	Color darkGray = new Color(0.3f, 0.3f, 0.3f, 1);
	
	Label counterLabel;
	
	float stageHeight = 800;
	float stageWidth = Gdx.graphics.getWidth()*800/Gdx.graphics.getHeight();
	
	OrthographicCamera stageCamera;
	
	ScoreBoard scoreBoard;
	float[] scores = new float[]{};
	
	Json json = new Json();
	
	public OrientationScreen (Physics gam) {
		game = gam;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth()/pixelsPerUnit, screenHeight);
		//camera.translate(-camera.viewportWidth/2, screenHeight/-2);
		camera.position.set(0, 0, 0);
		
		shapeRenderer = new ShapeRenderer();

		Gdx.input.setInputProcessor(new GestureDetector(gestureListener));

		setupPlayerPolygon();
		
		/*
		//Testing the iterator. It works.
		Array<String> list = new Array<String>();
		list.add("Post");
		list.add("Wasp");
		list.add("Hlue");
		list.add("Uolp");
		list.add("Rtol");
		
		Array<String> list2 = new Array<String>();
		list2.add("Mang");
		list2.add("Wasp");
		list2.add("Popl");
		list2.add("Post");
		
		for(int i = list.size - 1; i >= 0; i--) {
			boolean printable = true;
			
			for(String word: list2) {
				if(word == list.get(i)) {
					list.removeIndex(i);
					printable = false;
				}
			}
			
			if(printable == true) {
				System.out.println(list.get(i));
			}
		}
		*/
		
		//resetGame();
		
		//Setup the stage and world.
		stageCamera = new OrthographicCamera();
		stageCamera.setToOrtho(false, stageWidth, stageHeight);
		stageCamera.position.set(0, 0, 0);
		
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		stage = new Stage();
		
		stage.setViewport(new ExtendViewport(stageWidth, stageHeight));
		stage.getViewport().setCamera(stageCamera);
		
		//The counter
		counterLabel = new Label("Score", skin);
		counterLabel.setText(Integer.toString(MathUtils.round(distance)));
		counterLabel.setPosition(-stageWidth/2 + 5, stageHeight/2 - counterLabel.getHeight() - 5);
		counterLabel.setColor(Color.OLIVE);
		counterLabel.setScale(5);
		stage.addActor(counterLabel);
		
		//The other labels.
		Label fst = new Label("1st", skin);
		fst.setColor(darkGray);
		Label snd = new Label("2nd", skin);
		snd.setColor(darkGray);
		Label trd = new Label("3rd", skin);
		trd.setColor(darkGray);
		Label cL = new Label("You", skin);
		cL.setColor(darkGray);
		
		firstLabel = new Label("PH", skin);
		secondLabel = new Label("PH", skin);
		thirdLabel = new Label("PH", skin);
		currentLabel = new Label("PH", skin);
		
		//The logo at launch.
		logo = new Image(new TextureRegion(new Texture(Gdx.files.internal("freefall.png"))));
		logo.setFillParent(false);
		logo.scaleBy(0.002f);
		logo.setPosition(-logo.getWidth()/2, 120);
		logo.setColor(darkGray);
		
		stage.addActor(logo);
		
		//ScoreBoardThing
		scoreBoard = new ScoreBoard();
		scores = new float[] {0, 0, 0};
		setupScores();
		
		//The overtable, which shows when the game is over. 
		overTable = new Table(skin);
		
		setupScoreTable();
		
		overTable.setDebug(false);
		
		stage.addActor(overTable);
		
		//The overtable, which shows when the game is over. 
		setupTable = new Table(skin);
		setupTable.setFillParent(false);
		setupTable.align(Align.center);
		setupTable.setY(-230);
		
		setupTable.add("").pad(3);
		setupTable.add("");
		setupTable.row();
		setupTable.add("").pad(3);
		setupTable.add("");
		setupTable.row();
		setupTable.add("").pad(3);
		setupTable.add("");
		setupTable.row();
		setupTable.add("").pad(3);
		setupTable.add("");
		setupTable.row();
		setupTable.add().pad(5).colspan(2);
		setupTable.row();
		setupTable.row();
		Label setupLabel1 = new Label("Tap the Orb to start", skin);
		Label setupLabel2 = new Label("Rotate your device, or drag the screen", skin);
		setupLabel1.setColor(darkGray);
		setupTable.add(setupLabel1).colspan(2);
		setupTable.row();
		setupLabel2.setColor(darkGray);
		setupTable.add(setupLabel2);
		
		
		setupTable.setDebug(false);
				
		stage.addActor(setupTable);

	
	}
	
	private void setupScores() {
		if(Gdx.files.local("settings/scores.sav").exists()) {
			scoreBoard = json.fromJson(ScoreBoard.class, Gdx.files.local("settings/scores.sav"));
			scores = scoreBoard.scores;
		}
		else {
			scoreBoard.scores = new float[] {0, 0, 0};
			scores = scoreBoard.scores;
		}
	}
	
	private void saveScores() {
		FileHandle file = Gdx.files.local("settings/scores.sav");
		json.setUsePrototypes(false);
		file.writeString(json.prettyPrint(scoreBoard), false);
	}
	
	private void addScore(float newScore) {
		float[] temp = {scoreBoard.scores[0], scoreBoard.scores[1], scoreBoard.scores[2], newScore};
		Arrays.sort(temp);
		
		float[] sorted = new float[] {temp[temp.length-1], temp[temp.length-2], temp[temp.length-3]};
		
		scoreBoard.scores = sorted;
	}
	
	private void updateScoreTable() {
		firstLabel.setText(Integer.toString(MathUtils.round(scoreBoard.scores[0])));
		firstLabel.setColor(darkGray);
		secondLabel.setText(Integer.toString(MathUtils.round(scoreBoard.scores[1])));
		secondLabel.setColor(darkGray);
		thirdLabel.setText(Integer.toString(MathUtils.round(scoreBoard.scores[2])));
		thirdLabel.setColor(darkGray);
		currentLabel.setText(Integer.toString(MathUtils.round(distance)));
		currentLabel.setColor(darkGray);
	}
	
	private void setupScoreTable() {
		Label fst = new Label("1st", skin);
		fst.setColor(darkGray);
		Label snd = new Label("2nd", skin);
		snd.setColor(darkGray);
		Label trd = new Label("3rd", skin);
		trd.setColor(darkGray);
		Label cL = new Label("You", skin);
		cL.setColor(darkGray);
		
		firstLabel = new Label(Integer.toString(MathUtils.round(scoreBoard.scores[0])), skin);
		firstLabel.setColor(darkGray);
		secondLabel = new Label(Integer.toString(MathUtils.round(scoreBoard.scores[1])), skin);
		secondLabel.setColor(darkGray);
		thirdLabel = new Label(Integer.toString(MathUtils.round(scoreBoard.scores[2])), skin);
		thirdLabel.setColor(darkGray);
		currentLabel = new Label(Integer.toString(MathUtils.round(distance)), skin);
		currentLabel.setColor(darkGray);
		
		//The overtable, which shows when the game is over. 
		//overTable = new Table(skin);
		overTable.setFillParent(false);
		overTable.align(Align.center);
		overTable.setY(-230);

		overTable.add(fst).pad(3);
		overTable.add(firstLabel);
		overTable.row();
		overTable.add(snd).pad(3);
		overTable.add(secondLabel);
		overTable.row();
		overTable.add(trd).pad(3);
		overTable.add(thirdLabel);
		overTable.row();
		overTable.add(cL).pad(3);
		overTable.add(currentLabel);
		overTable.row();
		overTable.add().pad(5).colspan(2);
		overTable.row();
		Label overLabel = new Label("Tap anywhere to try again", skin);
		overLabel.setColor(darkGray);
		overTable.add(overLabel).colspan(2);
		
		stage.addActor(overTable);
	}
	
	private void setupPlayerPolygon() {
		float[] verts = new float[] {0, 1, 0.383f, 0.924f, 0.707f, 0.707f, 0.924f, 0.383f, 1, 0, 0.924f, -0.383f, 0.707f, -0.707f, 0.383f, -0.924f, 0, -1, -0.383f, -0.924f, -0.707f, -0.707f, -0.924f, -0.383f, -1f, 0f, -0.924f, 0.383f, -0.707f, 0.707f, -0.383f, 0.924f};
		float scale = radius;
		
		for(int i = 0; i < verts.length; i++) {
			verts[i] = verts[i] * scale;
		}
		
		playerPolygon = new Polygon();
		playerPolygon.setOrigin(0, 0);
		playerPolygon.setVertices(verts);
		playerPolygon.setPosition(playerLocation.x, playerLocation.y);
		
	}
	                                                                                                       
	@Override 
	public void render(float delta) {
		// TODO Auto-generated method stub
		if(collided == false) {
			Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
		}
		else {
			Gdx.gl.glClearColor(0.9f, 0.3f, 0.3f, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		
		rotateControls();
		renderCompassLine();
		
		updatePlayer(delta);
		
		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();

		Array<Vector2> needToRender = new Array<Vector2>();
		
		needToRender = getSurroundingFields();

		for(ObstacleField field: obstacleFields) {
			if(needToRender.contains(field.fieldCenter, false)) {
				for(Sprite sprite: field.obstacleSprites) {
					sprite.draw(game.batch);
					//System.out.println("Rendering");
				}
			}
		}
		
		game.batch.end();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.circle(playerLocation.x, playerLocation.y, radius);
		shapeRenderer.end();
		
		if(Gdx.input.justTouched()) {
			if(isPlaying <= 3) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos);
				if(Intersector.isPointInPolygon(playerPolygon.getVertices(), 0, playerPolygon.getVertices().length, touchPos.x, touchPos.y)) {
					isPlaying = 3;
					overTable.setVisible(false);
				}
				
				if(collided == true) {
					overTable.setVisible(false);
					resetGame();
				}
			}
		}
		
		counterLabel.setText(Integer.toString(MathUtils.round(distance)));
		manipulateStage();
		stage.draw();
		
	}
	
	public void manipulateStage() {
		if(isPlaying == 0) {
			logo.setVisible(true);
			counterLabel.setVisible(false);
			overTable.setVisible(false);
			setupTable.setVisible(true);
		}
		else if(isPlaying == 1) {
			logo.setVisible(false);
			counterLabel.setVisible(false);
			overTable.setVisible(false);
			setupTable.setVisible(true);
		}
		else if(isPlaying == 2) {
			logo.setVisible(false);
			counterLabel.setVisible(false);
			overTable.setVisible(true);
			setupTable.setVisible(false);
		}
		else if(isPlaying == 3) {
			logo.setVisible(false);
			counterLabel.setVisible(true);
			overTable.setVisible(false);
			setupTable.setVisible(false);
		}
	}
	
	//isPlaying shows state.
	//0 = Just launched.
	//1 = Setup
	//2 = Collided
	//3 = Middle of playing
	int isPlaying = 0;
	
	public void resetGame() {
		playerLocation.set(0, 0);
		playerSquare.set(0, 0);
		lastChecked.set(5, 5);
		
		velocity = -20;
		distance = 0;
		
		obstacleFields.clear();
		
		playerAngle = 0;
		cameraAngle = 0;
		
		isPlaying = 1;
	}
	
	Vector3 deltaPos = new Vector3();
	Vector3 endPos = new Vector3();
	float deltaAngle = 0;
	
	float lastGoodNumber = 0;
	
	float playerAngle = 0;
	float cameraAngle = 0;
	
	
	//This part controls how far the camera rotates. The player does not rotate. Only the camera. 
	//Deals with the moveAngle.
	public void rotateControls() {
		
		
		float moveAngle = getBearing(0, 0, Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY()) - 90;
		if(Gdx.input.getAccelerometerX() != 0 && Gdx.input.getAccelerometerY() != 0) {
			lastGoodNumber = moveAngle;
		}
		
		if(Gdx.input.getAccelerometerY() == 0) {
			moveAngle = moveAngle + 180;
		}
		
		if(Gdx.input.getAccelerometerX() == 0) {
			moveAngle = lastGoodNumber;
		}
		
		if(moveAngle < 0) {
			moveAngle = 360 + moveAngle;
		}
		if(moveAngle > 360) {
			moveAngle = moveAngle - 360;
		}
		
		playerAngle = cameraAngle + moveAngle;

	}

	//The gestureListener is for rotating the screen, which essentially rotates the player and the camera
	//simultaneously. Deals with the deltaAngle.
	GestureListener gestureListener = new GestureListener() {

		@Override
		public boolean touchDown(float x, float y, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean longPress(float x, float y) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY) {
			// TODO Auto-generated method stub
			endPos.set(x, y, 0);
			camera.unproject(endPos);
			
			deltaPos.set(x-deltaX, y-deltaY, 0);
			camera.unproject(deltaPos);
			
			deltaAngle = getBearing(new Vector2(camera.position.x, camera.position.y), new Vector2(endPos.x, endPos.y)) 
					- getBearing(new Vector2(camera.position.x, camera.position.y), new Vector2(deltaPos.x, deltaPos.y));
			
			playerAngle = playerAngle - deltaAngle;
			cameraAngle = cameraAngle - deltaAngle;

			
			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
				Vector2 pointer1, Vector2 pointer2) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void pinchStop() {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	
	
	/**
	 * Calculates the bearing of a position from the origin, with straight north as 0degrees.
	 * @param origin The coordinates of the origin.
	 * @param position Coordinates of the position to the bearing.
	 * @return Returns a positive angle in degrees.
	 */
	public float getBearing(Vector2 origin, Vector2 position) {
		float bearing = 0;
		if(position.y < origin.y) {
			bearing = (float) Math.toDegrees(Math.atan((double) (position.x-origin.x)/(position.y-origin.y))) + 180;
		}
		else {
			bearing = (float) Math.toDegrees(Math.atan((double) (position.x-origin.x)/(position.y-origin.y)));
		}
		
		if(bearing < 0) {
			bearing = 360 + bearing;
		}
		
		return bearing;
	}
	
	/**
	 * Calculates the bearing of a position from the origin, with straight north as 0degrees.
	 * @param x1 x of origin.
	 * @param y1 y of origin.
	 * @param x2 x of position.
	 * @param y2 y of position.
	 * @return Returns the bearing in degrees.
	 */
	public float getBearing(float x1, float y1, float x2, float y2) {
		float bearing = 0;
		if(y2 < y1) {
			bearing = (float) Math.toDegrees(Math.atan((double) (x2-x1)/(y2-y1))) + 180;
		}
		else {
			bearing = (float) Math.toDegrees(Math.atan((double) (x2-x1)/(y2-y1)));
		}
		
		if(bearing < 0) {
			bearing = 360 + bearing;
		}
		
		return bearing;
	}
	
	/**
	 * Gets coordinate a certain bearing and distance away from the original coordinates.
	 * @param x The origin X.
	 * @param y The origin Y.
	 * @param bearing The bearing clockwise from the origins.
	 * @param distance The distance to travel. 
	 * @return Returns the coordinate in Vector2 form.
	 */
	public Vector2 getCoordinates(float x, float y, float bearing, float distance) {
		
		Vector2 coord = new Vector2();
		
		coord.set((float) Math.sin(Math.toRadians((double) bearing)) * distance, (float) Math.cos(Math.toRadians((double) bearing)) * distance);
		coord.add(x, y);
		
		return coord;
	}
	
	
	public float getDistance(float x1, float y1, float x2, float y2) {
		float x = x2-x1;
		float y = y2-y1;
		
		float dist = (float) Math.sqrt((double) (x*x) + (y*y));
		
		return Math.abs(dist);
		
	}
	
	//For testing purposes only.
	Vector2 downVector = new Vector2();
	Vector2 homeVector = new Vector2();
	
	public void renderCompassLine() {
		//endVector.set(calculateVector(moveAngle + lineAngle + 180));
		downVector.set(getCoordinates(playerLocation.x, playerLocation.y, playerAngle + 180, 17));
		homeVector.set(getCoordinates(playerLocation.x, playerLocation.y, getBearing(playerLocation.x, playerLocation.y, 0, 0), 30));
		
		
		
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.line(playerLocation.x, playerLocation.y, downVector.x, downVector.y);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.line(playerLocation, homeVector);
		shapeRenderer.end();
	}
	
	public void updatePlayer(float delta) {
		Vector2 pos = new Vector2(getCoordinates(playerLocation.x, playerLocation.y, playerAngle, velocity*delta));
		
		if(isPlaying == 3) {
			playerLocation.set(pos);
			velocity -= delta/6;
		}
		camera.position.set(playerLocation, 0);
		
		camera.up.set(0, 1, 0);
		camera.direction.set(0, 0, -1);
		camera.rotate(cameraAngle);

		playerPolygon.setPosition(playerLocation.x, playerLocation.y);
		
		playerSquare.set(playerLocation);
		playerSquare.set(playerSquare.x/200, playerSquare.y/200);
		int xR = Math.round(playerSquare.x);
		int yR = Math.round(playerSquare.y);
		
		playerSquare.set(xR*200, yR*200);
		//System.out.println(playerSquare.x + "," + playerSquare.y);
		
		//System.out.println(playerSquare.toString() + lastChecked.toString());
		
		checkPlayerCollision();
		buildObstacles();
		
		distance = getDistance(0, 0, playerLocation.x, playerLocation.y)/5;
	}
	
	boolean collided = false;
	
	public void checkPlayerCollision() {
		boolean collidedT = false;
		
		Array<Vector2> toCheck = new Array<Vector2>();
		toCheck = getSurroundingFields();
		
		for(ObstacleField field: obstacleFields) {
			if(toCheck.contains(field.fieldCenter, false)) {
				for (Polygon polygon: field.obstacles) {
					if(Intersector.overlapConvexPolygons(polygon, playerPolygon)) {
						if(isPlaying == 3) {
							addScore(distance);
							saveScores();
							updateScoreTable();
							overTable.setVisible(true);
						}
							
						collidedT = true;
						isPlaying = 2;
						
					}
				}
			}
		}
		
		/*
		for(Polygon obstacle: obstacleField.obstacles) {
			if(Intersector.overlapConvexPolygons(obstacle, playerPolygon)) {
				collidedT = true;
			}
		}
		*/
		collided = collidedT;
	}
	
	
	Vector2 lastChecked = new Vector2(5, 5);
	
	public void buildObstacles() {
		
		if(playerSquare != lastChecked) {
			
			
			Array<Vector2> needToBuild = new Array<Vector2>();
			Array<Vector2> alreadyBuilt = new Array<Vector2>();
			
			needToBuild = getSurroundingFields();
			
			for(ObstacleField field: obstacleFields) {
				alreadyBuilt.add(field.fieldCenter);
				
			}
			
			for(Vector2 coord: needToBuild) {
				if(alreadyBuilt.contains(coord, false)) {
					
				}
				else {
					obstacleFields.add(new ObstacleField(coord.x, coord.y));
					System.out.println(coord.toString());
				}
			}
			lastChecked.set(playerSquare);
		}

	}
	
	public Array<Vector2> getSurroundingFields() {
		
		Array<Vector2> list = new Array<Vector2>();
		
		list.add(playerSquare);
		list.add(new Vector2(playerSquare.x-200, playerSquare.y-200));
		list.add(new Vector2(playerSquare.x-200, playerSquare.y));
		list.add(new Vector2(playerSquare.x-200, playerSquare.y+200));
		list.add(new Vector2(playerSquare.x, playerSquare.y-200));
		list.add(new Vector2(playerSquare.x, playerSquare.y+200));
		list.add(new Vector2(playerSquare.x+200, playerSquare.y-200));
		list.add(new Vector2(playerSquare.x+200, playerSquare.y));
		list.add(new Vector2(playerSquare.x+200, playerSquare.y+200));
		
		return list;
		
	}
	
	/*
	public Vector2 calculateVector(float degrees) {
		float x = (float) Math.sin(Math.toRadians((double) degrees));
		float y = (float) Math.cos(Math.toRadians((double) degrees));
		
		return new Vector2(x*30,y*30);
	}
	*/
	
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
