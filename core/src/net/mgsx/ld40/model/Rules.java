package net.mgsx.ld40.model;

public class Rules {
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	
	public static final float SFX_VOLUME = 0.2f;
	
	public static final int maxLife = 12;
	public static final int startLife = 3;
	public static int life;
	
	public static String levelTitle;
	public static String levelSubTitle;
	public static int levelID = 0;
	public static String levelMap;
	
	public static int tailsWhenExit;
	
	
	public static boolean initLevel() 
	{
		if(levelID == 0){
			life = startLife;
			levelTitle = "Level 1";
			levelSubTitle = "Something to eat";
		}else if(levelID == 1){
			levelTitle = "Level 2";
			levelSubTitle = "Not too greedy";
		}else if(levelID == 2){
			levelTitle = "Level 3";
			levelSubTitle = "Take care !";
		}else if(levelID == 4){
			levelTitle = "Level 4";
			levelSubTitle = "Starter before Dessert !";
		}else if(levelID == 5){
			levelTitle = "Level 5";
			levelSubTitle = "Whooooo !";
		}else{
			return false;
		}
		levelMap = "level" + levelID + ".tmx";
		return true;
	}
	
	
}
