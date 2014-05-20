package com.trinetix.app.Helpers;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Подсвечивает кнопку при нажатии.
 * Created by fess on 5/19/14.
 */
public class HighlightHelper
{
	private static final int lightColor = Color.parseColor("#999999");
	private static final int darkColor = Color.parseColor("#000000");

	public static void setShadow(Button button)
	{
		View.OnTouchListener onTouchListener = new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					Button b = (Button) view;
					Drawable d = b.getBackground();
					if (d != null)
					{
						d.setColorFilter(new LightingColorFilter(lightColor, darkColor));
					}
					view.invalidate();
				}
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					Button b = (Button) view;
					Drawable d = b.getBackground();
					if (d != null)
					{
						d.clearColorFilter();
					}
					view.invalidate();
				}
				return false;
			}
		};
		button.setOnTouchListener(onTouchListener);
	}
}
