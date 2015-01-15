package org.flxbox2d.dynamics.joints;

import org.flixel.FlxBasic;
import org.flixel.FlxG;
import org.flxbox2d.B2FlxState;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Like mouse joint, but supports multiple pointers.
 * 
 * @author Jan Martin
 */
public class B2FlxMultiTouchJoint extends FlxBasic 
{
	/**
	 * Parent state, used for attaching and detaching mouse joints
	 */
	private B2FlxState _state;
	/**
	 * Map of pointer indices and models
	 */
	private ObjectMap<Integer, B2FlxMouseJoint> _pointers;

	/**
	 * Constructor
	 */
	public B2FlxMultiTouchJoint(B2FlxState State) 
	{
		_state = State;
		_pointers = new ObjectMap<Integer, B2FlxMouseJoint>();
	}

	/**
	 * Main loop for the mouse joint.
	 */
	public void update() 
	{
		updateMouseWorld();
		mouseDown();
		mouseDrag();
		mouseUp();
	}

	/**
	 * Updates the mouse position in box2d world.
	 */
	private void updateMouseWorld() 
	{
		for (B2FlxMouseJoint pointer : _pointers.values()) 
		{
			pointer.updateMouseWorld();
		}
	}

	/**
	 * Check whether a body got pressed.
	 */
	private void mouseDown()
	{
		for (int p = 0; p < FlxG.mouse.activePointers; p++) 
		{
			if (FlxG.mouse.pressed(p) && !_pointers.containsKey(p)) 
			{
				B2FlxMouseJoint joint = new B2FlxMouseJoint(p);
				_state.add(joint);
				_pointers.put(p, joint);
			}
		}

		for (B2FlxMouseJoint pointer : _pointers.values()) 
		{
			pointer.mouseDown();
		}
	}

	/**
	 * If a body got pressed, the mouse joint will be updated with the mouse
	 * coordinates.
	 * 
	 * @return
	 */
	public void mouseDrag() 
	{
		for (B2FlxMouseJoint pointer : _pointers.values()) 
		{
			pointer.mouseDrag();
		}
	}

	/**
	 * On release the mouse joint will be destroyed.
	 */
	private void mouseUp() 
	{
		for(B2FlxMouseJoint pointer : _pointers.values())
		{
			pointer.mouseUp();
		}
	}

	/**
	 * Clean up the memory. This is an internal method. Do not use it!
	 */
	public void destroy() 
	{
		for (B2FlxMouseJoint pointer : _pointers.values()) 
		{
			pointer.destroy();
		}
	}

	@Override
	public void draw() 
	{
		for (B2FlxMouseJoint pointer : _pointers.values()) 
		{
			pointer.draw();
		}
	}

	/**
	 * Draws the debug line when the mouse drags.
	 */
	@Override
	public void drawDebug() 
	{
		for (B2FlxMouseJoint pointer : _pointers.values()) 
		{
			pointer.drawDebug();
		}
	}
}
