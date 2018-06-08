package com.why.project.newbuiltbottomsheetdialogdemo.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.why.project.newbuiltbottomsheetdialogdemo.R;

import java.lang.reflect.Method;


/**
 * Created by HaiyuKing
 * Used
 */

public class NewBuiltBottomSheetDialog extends BottomSheetDialog {
	private static final String TAG = NewBuiltBottomSheetDialog.class.getSimpleName();

	private Context mContext;
	private int displayHeight_build;//屏幕显示的高度值，从activity中传入，用于判断是否存在虚拟导航栏

	private TextView tv_addNote;
	private TextView tv_addFile;
	private TextView tv_addPhoto;
	private TextView tv_addVideo;

	public NewBuiltBottomSheetDialog(@NonNull Context context, int displayHeight) {
		super(context);
		mContext = context;
		this.displayHeight_build = displayHeight;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_bottomsheet_new_built);

		//可以变相实现底部外边距效果
		int screenHeight = getScreenHeight(scanForActivity(mContext));
		int statusBarHeight = getStatusBarHeight(getContext());
		int navigationBarHeight = getNavigationBarHeight(getContext());//底部虚拟导航高度
		//如果传入的displayHeight_build == 0，那么就使用默认的方法（存在的问题是，显示虚拟导航栏打开APP后，使用过程中隐藏虚拟导航栏，再打开对话框的时候，显示的位置不正确）
		int dialogHeight = screenHeight - navigationBarHeight - dip2px(mContext,0);//dip2px(mContext,0)预留在这里，如果以后想要调整距离的话
		if(displayHeight_build > 0){
			dialogHeight = displayHeight_build - navigationBarHeight - dip2px(mContext,0);//dip2px(mContext,0)预留在这里，如果以后想要调整距离的话
		}
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
		//设置透明
		getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);

		initViews();
		initEvents();
	}

	/**获取实际屏幕高度，不包括虚拟功能高度*/
	private int getScreenHeight(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		Display d = activity.getWindowManager().getDefaultDisplay();
		d.getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

	/**获取状态栏高度值*/
	private int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = res.getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	/**
	 * 获取底部虚拟导航栏高度
	 */
	public int getNavigationBarHeight(Context activity) {
		boolean hasNavigationBar = navigationBarExist(scanForActivity(activity)) && !vivoNavigationGestureEnabled(activity);
		if (!hasNavigationBar) {//如果不含有虚拟导航栏，则返回高度值0
			return 0;
		}
		Resources resources = activity.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height",
				"dimen", "android");
		//获取NavigationBar的高度
		int height = resources.getDimensionPixelSize(resourceId);
		return height;
	}

	/*========================================方法1======================================================*/
	/**
	 * 通过获取不同状态的屏幕高度对比判断是否有NavigationBar
	 * https://blog.csdn.net/u010042660/article/details/51491572
	 * https://blog.csdn.net/android_zhengyongbo/article/details/68941464*/
	public boolean navigationBarExist(Activity activity) {
		WindowManager windowManager = activity.getWindowManager();
		Display d = windowManager.getDefaultDisplay();

		DisplayMetrics realDisplayMetrics = new DisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			d.getRealMetrics(realDisplayMetrics);
		}

		int realHeight = realDisplayMetrics.heightPixels;
		int realWidth = realDisplayMetrics.widthPixels;

		DisplayMetrics displayMetrics = new DisplayMetrics();
		d.getMetrics(displayMetrics);

		int displayHeight = displayMetrics.heightPixels;
		int displayWidth = displayMetrics.widthPixels;
		if(this.displayHeight_build > 0){
			displayHeight = displayHeight_build;
		}
		return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
	}

	/*========================================方法2======================================================*/
	/**
	 * 检测是否有底部虚拟导航栏【有点儿问题，当隐藏虚拟导航栏后，打开APP，仍然判断显示了虚拟导航栏】
	 * @param context
	 * @return
	 */
	public boolean checkDeviceHasNavigationBar(Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {

		}
		return hasNavigationBar;
	}

	/**
	 * 获取vivo手机设置中的"navigation_gesture_on"值，判断当前系统是使用导航键还是手势导航操作
	 * @param context app Context
	 * @return false 表示使用的是虚拟导航键(NavigationBar)， true 表示使用的是手势， 默认是false
	 * https://blog.csdn.net/weelyy/article/details/79284332#更换部分被拉伸的图片资源文件
	 * 由于全面屏手机都没有底部的Home,Back等实体按键，因此，大多数全面屏手机都是支持虚拟导航键，即通过上面的方法checkDeviceHasNavigationBar获取的返回值都是true。
	 */
	public boolean vivoNavigationGestureEnabled(Context context) {
		int val = Settings.Secure.getInt(context.getContentResolver(), "navigation_gesture_on", 0);
		return val != 0;
	}

	/**解决java.lang.ClassCastException: android.view.ContextThemeWrapper cannot be cast to android.app.Activity问题
	 * https://blog.csdn.net/yaphetzhao/article/details/49639097*/
	private Activity scanForActivity(Context cont) {
		if (cont == null)
			return null;
		else if (cont instanceof Activity)
			return (Activity)cont;
		else if (cont instanceof ContextWrapper)
			return scanForActivity(((ContextWrapper)cont).getBaseContext());

		return null;
	}

	/**
	 * 获取dp的px值*/
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	private void initViews() {
		tv_addNote = (TextView) findViewById(R.id.tv_addNote);
		tv_addFile = (TextView) findViewById(R.id.tv_addFile);
		tv_addPhoto = (TextView) findViewById(R.id.tv_addPhoto);
		tv_addVideo = (TextView) findViewById(R.id.tv_addVideo);
	}

	private void initEvents() {
		tv_addNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mOnCustomButtonClickListener != null){
					mOnCustomButtonClickListener.onAddNoteButtonClick();
				}
				dismiss();
			}
		});

		tv_addFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mOnCustomButtonClickListener != null){
					mOnCustomButtonClickListener.onAddFileButtonClick();
				}
				dismiss();
			}
		});

		tv_addPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mOnCustomButtonClickListener != null){
					mOnCustomButtonClickListener.onAddPhotoButtonClick();
				}
				dismiss();
			}
		});

		tv_addVideo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(mOnCustomButtonClickListener != null){
					mOnCustomButtonClickListener.onAddVideoButtonClick();
				}
				dismiss();
			}
		});
	}

	public static abstract interface OnCustomButtonClickListener
	{
		//新建笔记按钮的点击事件接口
		public abstract void onAddNoteButtonClick();
		//新建文件按钮的点击事件接口
		public abstract void onAddFileButtonClick();
		//新建图集按钮的点击事件接口
		public abstract void onAddPhotoButtonClick();
		//新建视频按钮的点击事件接口
		public abstract void onAddVideoButtonClick();
	}

	private OnCustomButtonClickListener mOnCustomButtonClickListener;

	public void setOnCustomButtonClickListener(OnCustomButtonClickListener mOnCustomButtonClickListener)
	{
		this.mOnCustomButtonClickListener = mOnCustomButtonClickListener;
	}
}
