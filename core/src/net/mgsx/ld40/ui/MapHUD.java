package net.mgsx.ld40.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MapHUD extends Table
{
	public MapHUD(Skin skin) {
		super(skin);
		
		add(new Label("Hello", skin, "small")).expand().right().bottom();
		add(new TextButton("Hello", skin, "small")).expand().right().bottom();
	}
}
