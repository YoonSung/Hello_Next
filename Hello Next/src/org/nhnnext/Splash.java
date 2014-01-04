package org.nhnnext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Splash extends Activity {
	private WaitingHandler handler;
	private static ImageView backgroundImage;
	private static LinearLayout appTitle, loginFrame;
	private static TextView textRegister;
	
	private static int TYPE_LOGIN = 0; 
	private static int TYPE_ANIMATION_START = 1;
	private static int TYPE_ANIMATION_END = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		handler = new WaitingHandler(Splash.this);
		handler.sendEmptyMessageDelayed( TYPE_ANIMATION_START, 2000);

		backgroundImage = (ImageView) findViewById(R.id.splash_img_bg);
		appTitle = (LinearLayout) findViewById(R.id.splash_title);
		loginFrame = (LinearLayout) findViewById(R.id.splash_login_frame);
		textRegister = (TextView) findViewById(R.id.splash_text_join);
	}

	static class WaitingHandler extends Handler {

		Animation ani;
		int duraction = 500;

		private final Splash activity;

		WaitingHandler(Splash splash) {
			activity = splash;
		}

		
		@Override
		public void handleMessage(Message msg) {
			if ( msg.what == TYPE_LOGIN ) {
				 activity.startActivity(new Intent(activity, Main.class));
				 activity.finish();
				
			} else if ( msg.what == TYPE_ANIMATION_START ) {
				ani = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
						(float) 0.0, Animation.RELATIVE_TO_PARENT, (float) 0.0,
						Animation.RELATIVE_TO_PARENT, (float) 0.0,
						Animation.RELATIVE_TO_PARENT, (float) -0.3);
				ani.setDuration(duraction);
				ani.setFillAfter(true);
				appTitle.startAnimation(ani);
				this.sendEmptyMessageDelayed(TYPE_ANIMATION_END, duraction);
				
			} else if ( msg.what == TYPE_ANIMATION_END ) {
				loginFrame.setVisibility(View.VISIBLE);
				textRegister.setVisibility(View.VISIBLE);
				backgroundImage.setAlpha(0.5f);
			}
		}
	}

}
