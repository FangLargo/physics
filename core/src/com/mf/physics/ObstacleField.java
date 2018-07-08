package com.mf.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ObstacleField {

	public Array<Polygon> obstacles = new Array<Polygon>();
	public Array<Sprite> obstacleSprites = new Array<Sprite>();
	public Texture obstacleTexture = new Texture(Gdx.files.internal("block.png"));
	
	public Vector2 fieldCenter = new Vector2();
	
	private float halfWidth;
	private float halfHeight;
	
	//The density is the number of obstacles in an area of 200*200;
	private int obstacleDensity = 20;
	
	private Vector2 defaultObstacleSize = new Vector2(10, 30);
	
	private float variability = 5;
	
	public ObstacleField(float x, float y) {
		fieldCenter.set(x, y);
		for(int i = 0; i < obstacleDensity + 1; i++) {
			
			//Creates the random polygon
			Polygon rectangle = new Polygon();

			rectangle.setOrigin(0, 0);
			
			boolean goodToGo = false;
			
			while(!goodToGo) {
				//System.out.println("Fixing");
				
				halfWidth = MathUtils.random(defaultObstacleSize.x - variability, defaultObstacleSize.x + variability)/2;
				halfHeight = MathUtils.random(defaultObstacleSize.y - variability, defaultObstacleSize.y + variability)/2;
				
				rectangle.setVertices(new float[] {-halfWidth, -halfHeight, -halfWidth, halfHeight, halfWidth, halfHeight, halfWidth, -halfHeight});
				
				rectangle.setPosition(MathUtils.random(x-100, x+100), MathUtils.random(y-100, y+100));
				
				while(Math.abs(rectangle.getX()) < 60 && Math.abs(rectangle.getY()) < 60) {
					rectangle.setPosition(MathUtils.random(x-100, x+100), MathUtils.random(y-100, y+100));
				}
				
				rectangle.setRotation(MathUtils.random(0, 360));
				
				boolean goodToGo2 = true;
				
				for(Polygon polygon: this.obstacles) {
					if(Intersector.overlapConvexPolygons(polygon, rectangle)) {
						goodToGo2 = false;
						//System.out.println("Fixing");
					}
				}
				//System.out.println(goodToGo);
				
				if(goodToGo2) {
					goodToGo = true;
				}
			}
			
			obstacles.add(rectangle);
			//System.out.println("Added");
			
			//Sets up the rectangle sprite.
			Sprite rectangleSprite = new Sprite(obstacleTexture);
			
			
			rectangleSprite.setSize(halfWidth*2, halfHeight*2);
			rectangleSprite.setOriginCenter();
			rectangleSprite.setRotation(rectangle.getRotation());
			
			rectangleSprite.setCenter(rectangle.getX(), rectangle.getY());
			  
			obstacleSprites.add(rectangleSprite);
		}
	}
	
	
}
