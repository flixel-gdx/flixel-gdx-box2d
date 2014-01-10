![Logo](http://s6.postimg.org/4ss0wc9ov/flxbox2d_banner.png)
##What is FlxBox2D?
FlxBox2D is a wrapper for Box2D through libgdx. The Box2D is a full JNI wrapper which means there's no garabage collection issues at all and the JNI call overhead is minimal.

Box2D is originally written in C++ by Erin Catto and has been ported to many other programming language and environments. Libgdx team wrote a JNI wrapper for Box2D C++ and flixel-gdx team wrote a plugin, which named FlxBox2D, to make a tad easier to write code with Box2D in flixel.

####License
FlxBox2D is distributed under the New BSD License, meaning you can use it free of charge, without strings attached in commercial and non-commercial projects.

##Getting started
Before you start writing code with Box2D it is recommended you read the manual first. Box2D is not something simple to start off directly. The manual can be downloaded here: http://box2d.org/manual.pdf. If you don’t like reading the manual, there is also a website that teaches you from scratch: http://www.iforce2d.net/b2dtut/introductions. Either way, you shouldn’t proceed if you haven’t read the stuff.

###Things you need to know about FlxBox2D
The prefix of FlxBox2D classes begins with `B2Flx`. Some variables are already set as default or differ from Box2D. The table below shows which.
<table>
	<tr>
    	<td><code>B2FlxB.RATIO (32f)</code></td>
        <td>The ratio from meters to pixels.</td>
    </tr>
    <tr>
    	<td><code>B2FlxB._gravity (0, 9.8f)</code></td>
        <td>The gravity.</td>
    </tr>
    <tr>
    	<td><code>B2FlxB.contact</code></td>
        <td>The contact manager used for collision detection is intiliazed on setup.</td>
    </tr>
    <tr>
    	<td><code>B2FlxShape.resetAngle (true)</code></td>
        <td>Resets the angle to 0 when it reaches 360 or -360 to prevent overflow.</td>
    </tr>
    <tr>
    	<td>Radians to Degrees</td>
        <td>Box2D uses radians, but FlxBox2D converts them to degrees.</td>
    </tr>
</table>

To get Box2D objects working your state needs to inherit `B2FlxState` instead `FlxState`. The `World` will be created in `B2FlxState` and this will also avoid the spiral of death, because flixel uses delta time step while Box2D uses fixed time step. `B2FlxState` solves this by using fixed delta time step.

Depending on your game requirements, choose one of the collision algorithms and don’t mix them.

##Shapes
If you’ve read the manual, then you saw some snippets how to create a shape. It requires a fixture and body definition before it finally become a body that can freely move in the world. Fortunately FlxBox2D significantly simplify instantiation of bodies and provide a simple way to skin bodies with custom graphics.

**Box shape**
```java
new B2FlxBox(50,50, 50, 50)
	.setRestitution(.3f)
	.setFriction(.2f)
	.setDensity(.8f)
	.setDraggable(true)
	.create();
```

To make the body alive in world you need call `create()` at the end. There are five different shapes you can create.

Combining shapes together is also possible. You need to create a shapeless Shape and create fixtures from other shapes.
```java
// Shapeless. Attach shapes to it.
B2FlxSprite sprite = new B2FlxSprite(200, 200).create();
B2FlxPolygon polyshape = new B2FlxPolygon(0, 0, new float[][][]
	{
		{{-64,-64},{32,-32},{32,32},{-32,32}}
	})
	.setRestitution(.3f)
	.setFriction(.2f)
	.setDensity(.8f);
sprite.createFixtureFromPolygon(polyshape, true);
```

![Box2D Shapes](http://s6.postimg.org/hjpvt4041/box2d_shapes.png)

###Class diagram of shapes
![Box2D Shape Diagram](http://yuml.me/2f587b39)

##Joints
There are 10 different joints. All of them are created about the same way.
```java
B2FlxBox box1 = createBox(25, 25, 50, 50);
B2FlxBox box2 = createBox(100, 25, 50, 50);
new B2FlxDistanceJoint(box1, box2)
	.setAnchorA(box1.body.getWorldCenter())
	.setAnchorB(box2.body.getWorldCenter())
	.setShowLine(true)
	.create();
```

![Distance Joint](http://s6.postimg.org/lu4jop575/distance_joint.png)

Read the manual what a specific joint does and is used for.

###Class diagram of joints
![Box2D Joint Diagram](http://yuml.me/de049ba4)

###Debug
To enable to debug mode, you need to set `FlxG.debug` to true in your class that extends `FlxGame`. The debug settings can be found in `B2FlxDebug`.

_Note: the boundingbox of `FlxSprite` is not visible anymore when using Box2D objects._

##Collision usage in FlxBox2D
At default the fixtures are able to collide with other fixtures, but it’s also possible to setup collision filters to have a better control over which fixtures can collide with each other. The collision filter has three variables:
<table>
	<tr>
    	<td><code>categoryBits</code></td>
        <td>The shape that belongs to.</td>
    </tr>
    <tr>
    	<td><code>maskBits</code></td>
        <td>This states the categories that this shape would accept for collision. I'm categoryBits and collide with MaskBits.</td>
    </tr>
    <tr>
    	<td><code>groupIndex</code></td>
        <td>Collision groups allow a certain group of objects to never collide (negative) or always collide (positive). Zero means no collision group. Non-zero group filtering always wins against the mask bits.</td>
    </tr>
</table>

This tutorial (http://www.aurelienribon.com/blog/2011/07/box2d-tutorial-collision-filtering/) clearly tells how collision in Box2D works and it’s recommended you read this first.

The collision in Box2D happens with a listener. This means the collision check mustn’t be put inside the update loop. The event listener is created on default when `B2FlxState` is initialized. In FlxBox2D the `B2FlxContactManager` makes things a little easier. The only things you need to do are choosing the contact type and pass the `categoryBits` or `groupIndex` and the callback. Take a look below for an example.

```java
short PLAYER = 0x0001;
short ENEMY = 0x0002;

B2FlxBox player = new B2FlxBox(8, 10, 64, 64)
		.setGroupIndex(1)
		.setCategoryBits(PLAYER)
		.setMaskBits(ENEMY)
		.create();

B2FlxBox enemy = new B2FlxBox(20, 10, 64, 64)
		.setGroupIndex(1)
		.setCategoryBits(ENEMY)
		.setMaskBits(PLAYER)
		.create();

B2FlxB.contact.onBeginContact(PLAYER, ENEMY, onHit)

IB2FlxLister onHit = new IB2FlxListener()
{
	@Override
	public void onContact(B2FlxShape sprite1, B2FlxShape sprite2, Contact contact, Manifold oldManifold, ContactImpulse impulse)
	{
		sprite1.hurt();
	}
}
```
`player` object can collide with `enemy` object (maskbit `ENEMY` vs `PLAYER`) and both got positive `groupIndex (1)`. This example was single object handling, but you can also collide single against group and group against group. Check this [example](https://github.com/flixel-gdx/flixel-gdx-box2d/blob/master/flixel-gdx-box2d-examples/src/org/flxbox2d/examples/TestCollisionDetection.java 'Collision test') for the rest of the code.

**Examples**:
-	[Demos](https://github.com/flixel-gdx/flixel-gdx-box2d/tree/master/flixel-gdx-box2d-examples/src/org/flxbox2d/examples 'Box2D demos') to learn other classes.
-	Advanced stuff with Box2D at [iforce2d](http://www.iforce2d.net/ 'iforce2d').