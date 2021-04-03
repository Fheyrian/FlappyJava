package com.flappyjava.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class FlappyJava extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture []bird;
	Texture upperpipes;
	Texture bottompipes;
	Texture gameover;

	int state=0, birdstate=0, score=0, scorecount=0, birdCount=0;
	float birdY;
	float gravity=2;
	float Vvelocity;
	BitmapFont font;
	BitmapFont beginText;

	ArrayList<Integer> upperpipesX = new ArrayList<>();
	ArrayList<Integer> upperpipesY = new ArrayList<>();
	ArrayList<Integer> bottompipesX = new ArrayList<>();
	ArrayList<Integer> bottompipesY = new ArrayList<>();
	int gap, pipecount;
	Random random;

	ArrayList<Rectangle> upRect = new ArrayList<>();
	ArrayList<Rectangle> botRect = new ArrayList<>();
	Rectangle birdRect = new Rectangle();
	ArrayList<Rectangle> gapRect = new ArrayList<>();

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		bird = new Texture[2];
			bird[0] = new Texture("bird.png");
			bird[1] = new Texture("bird2.png");
		birdY = Gdx.graphics.getHeight()/2;
		upperpipes = new Texture("toptube.png");
		bottompipes = new Texture("bottomtube.png");
		random = new Random();
		gap = bird[birdstate].getHeight()*5;
		gameover = new Texture("gameover.png");
		font = new BitmapFont();
			font.setColor(Color.WHITE);
			font.getData().setScale(8);
		beginText = new BitmapFont();
			beginText.setColor(Color.WHITE);
			beginText.getData().setScale(6);

	}

	public void makePipes(int gap){
		float height = (Gdx.graphics.getHeight()-gap)*random.nextFloat();
		bottompipesY.add((int)height);
		bottompipesX.add(Gdx.graphics.getWidth());
		upperpipesY.add(gap+(int)height);
		upperpipesX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {

		batch.begin();

		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		//in game
		if (state==1){
			batch.draw(bird[birdstate],Gdx.graphics.getWidth()/2-bird[birdstate].getWidth()/2,birdY);
			font.draw(batch,String.valueOf(score), 50, Gdx.graphics.getHeight()-50);

			Vvelocity += gravity;
			birdY -= Vvelocity+gravity;

			if (Gdx.input.justTouched()){
				Vvelocity -= Vvelocity+(gravity*20);
			}

			if (birdY<=0) {
				Vvelocity -= Vvelocity +gravity*10 ;
			}
			else if (birdY>=Gdx.graphics.getHeight()){
				Vvelocity =0;
			}

			if (Vvelocity>0){
				birdstate=0;
			} else {
				birdstate=1;
			}

			if (pipecount<75){
				pipecount++;
			} else {
				pipecount = 0;
				makePipes(gap);
			}

			upRect.clear();
			botRect.clear();
			gapRect.clear();
			for (int i=0; i<bottompipesX.size(); i++){
				batch.draw(bottompipes, bottompipesX.get(i), bottompipesY.get(i)-bottompipes.getHeight());
				bottompipesX.set(i, bottompipesX.get(i)-10);
				botRect.add(new Rectangle(bottompipesX.get(i), 0, bottompipes.getWidth(),bottompipesY.get(i)));
				gapRect.add(new Rectangle(bottompipesX.get(i), 0, 1, Gdx.graphics.getHeight()));
				batch.draw(upperpipes, upperpipesX.get(i),upperpipesY.get(i));
				upperpipesX.set(i,upperpipesX.get(i)-10);
				upRect.add(new Rectangle(upperpipesX.get(i),upperpipesY.get(i), upperpipes.getWidth(), upperpipesY.get(i)));

			}

			for (int i=0;i<upRect.size();i++){
				if (Intersector.overlaps(birdRect,upRect.get(i)) || Intersector.overlaps(birdRect,botRect.get(i))){
					state=2;
				}
			}
			for (int i=0;i<upRect.size();i++) {
				if (Intersector.overlaps(birdRect, gapRect.get(i))) {
					if (scorecount<12){
						scorecount++;
					} else {
						score++;
						scorecount = 0;
					}
				}
			}

			birdRect = new Rectangle(Gdx.graphics.getWidth()/2-bird[birdstate].getWidth()/2,birdY, bird[birdstate].getWidth(), bird[birdstate].getHeight());

		}

		//Begin screen
		else if (state==0){
			beginText.draw(batch, "Tap to play", Gdx.graphics.getWidth()/3-30, 150);

			batch.draw(bird[birdstate],Gdx.graphics.getWidth()/2-bird[birdstate].getWidth()/2, Gdx.graphics.getHeight()/2);

			if (birdCount<5){
				birdCount++;
			} else {
				birdCount=0;
				if (birdstate==0){
					birdstate=1;
				} else {
					birdstate=0;
				}
			}


			if (Gdx.input.justTouched()){
				state=1;
			}
		}

		//Game over
		else if (state==2){
			batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2, Gdx.graphics.getHeight()/2+gameover.getHeight());
			font.draw(batch,String.valueOf(score), Gdx.graphics.getWidth()/2-font.getScaleX()/2, Gdx.graphics.getHeight()/2);
			beginText.draw(batch, "Tap to play", Gdx.graphics.getWidth()/3-30, 150);
			upRect.clear();
			upperpipesX.clear();
			upperpipesY.clear();
			botRect.clear();
			bottompipesX.clear();
			bottompipesY.clear();

			Vvelocity=0;
			pipecount=0;

			if (Gdx.input.justTouched()){
				state=1;
				score=0;
			}
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
