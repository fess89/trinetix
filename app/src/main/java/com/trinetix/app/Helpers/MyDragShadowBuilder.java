package com.trinetix.app.Helpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

/**
 * Создаём тень изображения для Drag&Drop.
 * Created by fess on 5/19/14.
 */
public class MyDragShadowBuilder extends View.DragShadowBuilder
{
	// The drag shadow image, defined as a drawable thing
	private static Drawable shadow;

	// Defines the constructor for myDragShadowBuilder
	public MyDragShadowBuilder(ImageView imageView)
	{
		// Stores the View parameter passed to myDragShadowBuilder.
		super(imageView);

		// Creates a draggable image that will fill the Canvas provided by the system.
		Drawable d = imageView.getDrawable();
		if (null == d)
		{
			//stub image - just light gray color in our case
			shadow = new ColorDrawable(Color.LTGRAY);
		}
		else
		{
			//copy of the initial drawable (direct assignment not ok, deep copy needed)
			shadow = d.getConstantState().newDrawable();
		}
	}

	// Defines a callback that sends the drag shadow dimensions and touch point back to the
	// system.
	@Override
	public void onProvideShadowMetrics(Point size, Point touch)
	{
		// Sets the width of the shadow to 1/4 of the width of the original View
		int width = getView().getWidth() / 3;

		// Sets the height of the shadow to 1/4 of the height of the original View
		int height = getView().getHeight() / 3;

		// The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
		// Canvas that the system will provide. As a result, the drag shadow will fill the
		// Canvas.
		shadow.setBounds(0, 0, width, height);

		// Sets the size parameter's width and height values. These get back to the system
		// through the size parameter.
		size.set(width, height);

		// Sets the touch point's position to be in the middle of the drag shadow
		touch.set(width / 2, height / 2);
	}

	// Defines a callback that draws the drag shadow in a Canvas that the system constructs
	// from the dimensions passed in onProvideShadowMetrics().
	@Override
	public void onDrawShadow(Canvas canvas)
	{
		// Draws the ColorDrawable in the Canvas passed in from the system.
		shadow.draw(canvas);
	}
}
