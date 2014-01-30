package org.flxbox2d.examples;

import org.flixel.FlxButton;
import org.flixel.FlxG;
import org.flixel.FlxState;
import org.flixel.FlxText;
import org.flixel.FlxU;
import org.flixel.event.IFlxButton;
import org.flxbox2d.B2FlxB;
import org.flxbox2d.B2FlxState;
import org.flxbox2d.collision.shapes.B2FlxBox;
import org.flxbox2d.collision.shapes.B2FlxShape;
import org.flxbox2d.dynamics.joints.B2FlxMouseJoint;
import org.flxbox2d.system.debug.B2FlxDebug;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

/**
 * A parent class for testing.
 * 
 * @author Ka Wing Chin
 */
public class PlayState extends B2FlxState
{
	public static int currentTest = 0;
	private static final int AMOUNT_OF_TESTS = 24;
	public FlxText title;
	public FlxText info;
	public B2FlxMouseJoint mouse;
	public final short WALL = 0x0001;
	
	@Override
	public void create()
	{		
		super.create();
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		if(!FlxG.mobile)
			FlxG.mouse.show();
		
		B2FlxB.setGravity(0, 9.8f);
		FlxG.visualDebug = true;
		FlxG.setBgColor(0xff000000);
		
		if(FlxG.mobile || Gdx.app.getType() == ApplicationType.WebGL)
			B2FlxDebug.drawCollisions = false;
		
//		B2FlxDebug.drawAABBs = true;
		
		B2FlxB.world.setWarmStarting(true);
		
		FlxG.camera.setBounds(0,0,1024,640,true);
		
		FlxText text = new FlxText(FlxG.width-190, 5, 180, "FlxBox2D for 2.55").setFormat(null, 8, 0xFFFFFF, "right");
		text.scrollFactor.x = text.scrollFactor.y = 0;
		text.ignoreDrawDebug = true;
		add(text);

		add(text = new FlxText(-10, 16, FlxG.width, "'Left/Right' arrows to go to previous/next example\n 'R' to reset.")
		.setFormat(null, 8, 0xFFFFFF, "right"));
		text.scrollFactor.x = text.scrollFactor.y = 0;
		text.ignoreDrawDebug = true;

		add(title = new FlxText(-10, 48, FlxG.width, "").setFormat(null, 16, 0x00CCFF, "right"));
		title.scrollFactor.x = title.scrollFactor.y = 0;
		title.ignoreDrawDebug = true;
		add(info = new FlxText(10, 5, FlxG.width, "").setFormat(null, 8, 0xFFFFFF));
		info.scrollFactor.x = info.scrollFactor.y = 0;
		info.ignoreDrawDebug = true;
		
		// Create walls
		// Left
		add(new B2FlxBox(0, 0, 5, FlxG.height).setType(B2FlxShape.STATIC).setCategoryBits((short) WALL).create());
		// Right
		add(new B2FlxBox(FlxG.width-5, 0, 5, FlxG.height).setType(B2FlxShape.STATIC).setCategoryBits((short) WALL).create());
		// Top
		add(new B2FlxBox(0, 0, FlxG.width, 5).setType(B2FlxShape.STATIC).setCategoryBits((short) WALL).create());
		// Bottom
		add(new B2FlxBox(0, FlxG.height-5, FlxG.width, 5).setType(B2FlxShape.STATIC).setCategoryBits((short) WALL).create());
		
		// Add mouse joint.
		add(mouse = new B2FlxMouseJoint());
				
		// Mobile
		if(FlxG.mobile)
		{
			add(createButton(2, FlxG.height - 20, "Previous", new IFlxButton(){@Override public void callback(){prev();}}));
			add(createButton(82, FlxG.height - 20, "Next", new IFlxButton(){@Override public void callback(){next();}}));
			add(createButton(162, FlxG.height - 20, "Reset", new IFlxButton(){@Override public void callback(){reset();}}));
		}
		String className = FlxU.getClassName(this, true);
		if(className.equals("PlayState") || className.equals("org.flxbox2d.examples.PlayState"))
		{
			try
			{
				switchState(currentTest);
			}
			catch(Exception e)
			{
				FlxG.log(e.getMessage());
				return;
			}
		}
	}
	
	public B2FlxBox createBox(float x, float y, float width, float height)
	{
		return new B2FlxBox(x, y, width, height)
		.setFriction(.8f)
		.setRestitution(.3f)
		.setDensity(.7f)
		.setDraggable(true)
		.create();
	}
	
	public FlxButton createButton(float x, float y, String label, IFlxButton callback)
	{
		FlxButton button = new FlxButton(x, y, label, callback);
		button.ignoreDrawDebug = true;
		button.scrollFactor.x = button.scrollFactor.y = 0;
		button.setSolid(false);
		button.moves = false;
		return button;
	}
	
	@Override
	public void update()
	{
		if(FlxG.keys.justPressed("RIGHT"))
			next();
		else if(FlxG.keys.justPressed("LEFT"))
			prev();
		else if(FlxG.keys.justPressed("R"))
			reset();
		super.update();
	}
	
	private void next()
	{
		if(AMOUNT_OF_TESTS <= ++currentTest)
			currentTest = 0;
		try
		{
			switchState(currentTest);
		}
		catch(Exception e)
		{
			FlxG.log(e.getMessage());
			return;
		}
	}
	
	private void prev()
	{
		if(0 > --currentTest)
			currentTest = AMOUNT_OF_TESTS-1;
		try
		{
			switchState(currentTest);
		}
		catch(Exception e)
		{
			FlxG.log(e.getMessage());
			return;
		}
	}
	
	private void switchState(int current)
	{
		FlxState state = null;
		switch(current)
		{			
			case 0:
				state = new TestShapes(); 
				break;
			case 1:
				state = new TestDistanceJoint();
				break;
			case 2:
				state = new TestRopeJoint(); 
				break;
			case 3:
				state = new TestRevoluteJoint(); 
				break;
			case 4:
				state = new TestPrismaticJoint(); 
				break;
			case 5:
				state = new TestPulleyJoint(); 
				break;
			case 6:
				state = new TestGearJoint(); 
				break;
			case 7:
				state = new TestFrictionJoint(); 
				break;
			case 8:
				state = new TestWeldJoint(); 
				break;
			case 9:
				state = new TestWheelJoint(); 
				break;
			case 10:
				state = new TestCart(); 
				break;
			case 11:
				state = new TestRagdolls(); 
				break;
			case 12:
				state = new TestCompound(); 
				break;
			case 13:
				state = new TestCrankGearsPulley(); 
				break;
			case 14:
				state = new TestBridge(); 
				break;
			case 15:
				state = new TestStack(); 
				break;
			case 16:
				state = new TestCCD(); // Continuous Collision Detection
				break;
			case 17:
				state = new TestBuoyancy(); 
				break;
			case 18:
				state = new TestGravity(); 
				break;
			case 19:
				state = new TestOneSidedPlatform(); 
				break;
			case 20:
				state = new TestBreakable(); 
				break;
			case 21:
				state = new TestSensor(); 
				break;
			case 22:
				state = new TestCollisionDetection(); 
				break;
			case 23:
				state = new TestExplosion(); // Explosion & Implosion
				break;
			default:
				break;
		}
		if(state != null)
			FlxG.switchState(state);	
//		TheoJansen.class 			// Theo Jansen
//		TestEdges.class,			// Edges
//		TestRaycast.class,			// Raycast
	}
	
	private void reset()
	{
		FlxG.resetState();
	}
	
	@Override
	public void destroy()
	{		
		super.destroy();
		title = null;
		info = null;
		mouse = null;
	}
}

