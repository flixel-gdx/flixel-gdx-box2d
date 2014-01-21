package org.flxbox2d.client;

import org.flixel.client.FlxHtml5Application;
import org.flxbox2d.examples.Box2DDemo;

public class FlxBox2DExamplesStarter extends FlxHtml5Application
{

	public FlxBox2DExamplesStarter()
	{
		super(new Box2DDemo(), 640, 360);
	}
}
