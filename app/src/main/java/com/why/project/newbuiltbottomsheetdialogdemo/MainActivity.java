package com.why.project.newbuiltbottomsheetdialogdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import com.why.project.newbuiltbottomsheetdialogdemo.dialog.NewBuiltBottomSheetDialog;

public class MainActivity extends AppCompatActivity {

	private Button btn_add;
	private int displayHeight = 0;//屏幕显示的高度值（不包括虚拟导航栏的高度）

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViews();
		initEvents();
	}

	private void initViews() {
		btn_add = findViewById(R.id.btn_add);
	}

	private void initEvents() {
		//监听屏幕高度变化
		View rootView = this.getWindow().getDecorView();
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				//https://blog.csdn.net/u013872857/article/details/53750682
				int[] loc = new int[2];
				findViewById(R.id.bottom_layout).getLocationOnScreen(loc);
				displayHeight = loc[1] + getResources().getDimensionPixelSize(R.dimen.tab_bottom_height);//底部区域+底部的高度值=显示区域的高度值
			}
		});

		btn_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NewBuiltBottomSheetDialog bottomSheetDialog = new NewBuiltBottomSheetDialog(MainActivity.this,displayHeight);
				bottomSheetDialog.setOnCustomButtonClickListener(new NewBuiltBottomSheetDialog.OnCustomButtonClickListener() {
					@Override
					public void onAddNoteButtonClick() {
						//打开新建笔记界面
						Toast.makeText(MainActivity.this,"新建笔记",Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onAddFileButtonClick() {
						//打开新建文件界面
						Toast.makeText(MainActivity.this,"新建文件",Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onAddPhotoButtonClick() {
						//打开新建图片界面
						Toast.makeText(MainActivity.this,"新建图片",Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onAddVideoButtonClick() {
						//打开新建视频界面
						Toast.makeText(MainActivity.this,"新建视频",Toast.LENGTH_SHORT).show();
					}
				});
				bottomSheetDialog.show();
			}
		});
	}
}
