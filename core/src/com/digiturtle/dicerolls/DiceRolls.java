package com.digiturtle.dicerolls;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class DiceRolls extends ApplicationAdapter {
	
	private Stage stage;
	
	private BitmapFont text, fontAwesome;
	
	private ArrayList<Integer> rolls = new ArrayList<>();
	
	private ShapeRenderer shapeRenderer;
	
	private double[] experimentalRatios;
	
	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		stage = new Stage();
		int width = Gdx.graphics.getWidth(), height = Gdx.graphics.getHeight();
		generateFonts();
		stage.addActor(createButton(new Rectangle(0.00f, 0.9f, 0.25f, 0.1f), 0.01f, 
				getUnicodeCharacter(61527), "Clear", width, height, fontAwesome, Color.GRAY));
		stage.addActor(createButton(new Rectangle(0.75f, 0.9f, 0.25f, 0.1f), 0.01f, 
				getUnicodeCharacter(61563), "Save", width, height, fontAwesome, Color.GRAY));
		stage.addActor(createButton(new Rectangle(0.00f, 0.75f, 0.25f, 0.1f), 0.01f, "2", "Add_2", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.25f, 0.75f, 0.25f, 0.1f), 0.01f, "3", "Add_3", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.50f, 0.75f, 0.25f, 0.1f), 0.01f, "4", "Add_4", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.75f, 0.75f, 0.25f, 0.1f), 0.01f, "5", "Add_5", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.00f, 0.65f, 0.25f, 0.1f), 0.01f, "6", "Add_6", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.25f, 0.65f, 0.25f, 0.1f), 0.01f, "7", "Add_7", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.50f, 0.65f, 0.25f, 0.1f), 0.01f, "8", "Add_8", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.75f, 0.65f, 0.25f, 0.1f), 0.01f, "9", "Add_9", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.125f, 0.55f, 0.25f, 0.1f), 0.01f, "10", "Add_10", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.375f, 0.55f, 0.25f, 0.1f), 0.01f, "11", "Add_11", width, height, text, Color.BLACK));
		stage.addActor(createButton(new Rectangle(0.625f, 0.55f, 0.25f, 0.1f), 0.01f, "12", "Add_12", width, height, text, Color.BLACK));
		Gdx.input.setInputProcessor(stage);
	}
	
	private String getUnicodeCharacter(int value) {
		return new String(Character.toChars(value));
	}
	
	private Pixmap getPixmap(Color color) {
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fill();
		return pixmap;
	}
	
	private void generateFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
		text = generator.generateFont(new FreeTypeFontParameter() {
			{
				size = Gdx.graphics.getHeight() / 20;
			}
		});
		generator.dispose();
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			fontAwesome = new BitmapFont(Gdx.files.internal("FontAwesomeDesktop_v2.0.fnt"));
		}
		else {
			fontAwesome = new BitmapFont(Gdx.files.internal("FontAwesomeAndroid_v2.0.fnt"));
		}
	}
	
	private void onAction(String action) {
		if (action.equalsIgnoreCase("Clear")) {
			rolls.clear();
		}
		else if (action.equalsIgnoreCase("Save")) {
			double[] duplicate = Arrays.copyOf(experimentalRatios, experimentalRatios.length);
			for (int i = 2; i < duplicate.length; i++) {
				duplicate[i - 2] = Math.round(duplicate[i] * 1000.0) / 1000.0;
			}
			duplicate = Arrays.copyOf(duplicate, duplicate.length - 2);
			String json = "{\"rolls\": " + rolls.toString() + ", \"experimental-ratios\": " + 
					(experimentalRatios != null ? Arrays.toString(duplicate) : "[]") + "}";
			Gdx.app.getClipboard().setContents(json);
		}
		else if (action.toLowerCase().startsWith("add_")) {
			String[] info = action.split("_");
			int roll = Integer.parseInt(info[1]);
			rolls.add(roll);
		}
	}
	
	private Button createButton(Rectangle bounds, float padding, String text,final String action, int width, int height, final BitmapFont bitmapFont, final Color bg) {
		TextButton button = new TextButton(text, new TextButton.TextButtonStyle() {
			{
				font = bitmapFont;
				up = new TextureRegionDrawable(new TextureRegion(new Texture(getPixmap(bg))));
			}
		});
		bounds.x += padding;
		bounds.y += padding;
		bounds.width -= 2 * padding;
		bounds.height -= 2 * padding;
		button.setBounds(bounds.x * width, bounds.y * height, bounds.width * width, bounds.height * height);
		button.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				onAction(action);
			}
		});
		return button;
	}
	
	private void drawRectangle(Rectangle rectangle, Color color) {
		rectangle.x *= Gdx.graphics.getWidth();
		rectangle.y *= Gdx.graphics.getHeight();
		rectangle.width *= Gdx.graphics.getWidth();
		rectangle.height *= Gdx.graphics.getHeight();
		shapeRenderer.setColor(color);
		shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}
	
	private void drawBar(int index, Color color, float ratio, int total) {
		float barSize = (0.9f - 2 * 0.025f) / (float) total;
		Rectangle rectangle = new Rectangle(0.05f + 0.025f + index * barSize, 0.025f + 0, barSize * 0.9f, ratio * 0.4f);
		drawRectangle(rectangle, color);
	}
	
	private double[] getRatios(int[] data) {
		int total = Arrays.stream(data).sum();
		double[] ratios = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			ratios[i] = (double) data[i] / (double) total;
		}
		return ratios;
	}
	
	private void drawGraph() {
		Rectangle rectangle = new Rectangle(0.05f, 0.025f, 0.9f, 0.45f);
		drawRectangle(rectangle, new Color(0.9f, 0.9f, 0.9f, 1f));
		int[] theoretical = new int[] { 0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1 };
		double[] theoreticalRatios = getRatios(theoretical);
		final int[] experimental = new int[theoretical.length];
		Arrays.fill(experimental, 0);
		for (int i = 0; i < rolls.size(); i++) {
			experimental[rolls.get(i)]++;
		}
		experimentalRatios = rolls.size() > 0 ? getRatios(experimental) : null;
		double maxRatio = Math.max(
				Arrays.stream(theoreticalRatios).max().getAsDouble(), 
				experimentalRatios != null ? Arrays.stream(experimentalRatios).max().getAsDouble() : 0
			);
		int index = 0;
		for (int i = 2; i < theoretical.length; i++) {
			drawBar(index + 0, Color.GRAY, (float) (theoreticalRatios[i] / maxRatio), 22);
			if (experimentalRatios != null)
				drawBar(index + 1, Color.GREEN, (float) (experimentalRatios[i] / maxRatio), 22);
			index += 2;
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		shapeRenderer.begin(ShapeType.Filled);
		drawGraph();
		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {
		shapeRenderer.dispose();
		stage.dispose();
		text.dispose();
		fontAwesome.dispose();
	}
}
