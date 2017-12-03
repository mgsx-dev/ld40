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
	public static String levelHints;
	public static String levelWords;
	
	
	public static boolean initLevel() 
	{
		if(levelID == 1){
			levelTitle = "Level 1";
			levelSubTitle = "Nibble at the Clearing";
			levelHints = "Help crazy snake to find the forest exit.";
			levelWords = "but don't be too greedy...";
		}else if(levelID == 2){
			levelTitle = "Level 2";
			levelSubTitle = "Dangerous Hungry";
			levelHints = "Watch out for thorns !";
			levelWords = "stuff your face at your own risk...";
		}else if(levelID == 3){
			levelTitle = "Level 3";
			levelSubTitle = "Self-serve Buffet";
			levelHints = "Things moving .. take care !";
			levelWords = "greed is a bad thing...";
		}else if(levelID == 4){
			levelTitle = "Level 4";
			levelSubTitle = "The Starter before the Dessert !";
			levelHints = "It becomes challenging !";
			levelWords = "attention to indigestion...";
		}else if(levelID == 5){
			levelTitle = "Level 5";
			levelSubTitle = "The devil's Feast";
			levelHints = "Final meal !";
			levelWords = "eat as much as you can... or not";
		}else{
			return false;
		}
		levelMap = "level" + levelID + ".tmx";
		return true;
	}
	
	
}
