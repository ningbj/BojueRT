package views;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ModelDialog extends Dialog {
	public ModelDialog(Activity activity, int layout, int style) {
		super(activity, style);
		setContentView(layout);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		// float density = getDensity(context);
		// params.width = (int) (default_width * density);
		// params.height = (int) (default_height * density);
		params.width = activity.getWindowManager().getDefaultDisplay()
				.getWidth();
		; // 默认宽度
			 /*params.height = activity.getWindowManager().getDefaultDisplay()
						.getHeight()/2;*/
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}

	public ModelDialog(Activity activity, int layout, int style,
			Boolean isMatchWidth) {
		super(activity, style);
		int default_width = activity.getWindowManager().getDefaultDisplay()
				.getWidth() * 5 / 6;
		if (!isMatchWidth) {
			default_width = default_width * 9 / 10;
		}
		setContentView(layout);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = default_width;
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}

	/*
	 * private float getDensity(Context context) { Resources resources =
	 * context.getResources(); DisplayMetrics dm =
	 * resources.getDisplayMetrics(); return dm.density; }
	 */


}