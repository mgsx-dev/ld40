package net.mgsx.ld40.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.utils.Align;

public class TypingLabel extends Stack
{
	private String fullText;
	public float speed = 10;
	public float length = 0;
	private Label label;
	private float prefWidth, prefHeight;
	
	public TypingLabel(Label label) {
		this.label = label;
		label.setAlignment(Align.topLeft);
		add(label);
	}
	
	@Override
	public void act(float delta) 
	{
		super.act(delta);
		speed = 40;
		if(length < fullText.length()){
			length += delta * speed;
			int len = (int)length;
			label.setText(fullText.substring(0, len));
		}else{
			// length = 0; // XXX loop
		}
	}
	
	@Override
	public float getPrefWidth() {
		return prefWidth;
	}
	
	@Override
	public float getPrefHeight() {
		return prefHeight;
	}
	
	public void setNextText(String text){
		fullText = text;
		label.setText(fullText);
		validate();
		prefWidth = label.getPrefWidth();
		prefHeight = label.getPrefHeight();
		length = 0;
	}
	
}
