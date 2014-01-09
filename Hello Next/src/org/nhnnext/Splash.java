package org.nhnnext;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Splash extends Activity implements OnClickListener {
	private WaitingHandler handler;
	private static ImageView backgroundImage;
	private static LinearLayout appTitle, loginFrame;
	private static TextView textRegister;
	private EditText edtId, edtPassword;
	private Button btnLogin;

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
		textRegister.setOnClickListener(this);

		edtId = (EditText) findViewById(R.id.splash_edt_id);
		edtPassword =  (EditText) findViewById(R.id.splash_edt_pw);
		
		btnLogin = (Button)findViewById(R.id.splash_btnLogin);
		btnLogin.setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		
		switch (v.getId()) {
		case R.id.splash_btnLogin :
			intent = new Intent(Splash.this, Main.class);
			
			
			/******************************************************************************************/
			String query = "SELECT id, pw FROM member WHERE id="
					 + edtId.getText().toString().trim() + " AND pw= " 
					+ "'" + edtPassword.getText().toString().trim() +"'" + ";";
			
			Log.e("Main", "sql : " + query);
			Common.getInstance().setNextExecuteQuery(query);
			Handler myHandler = new Handler();
			Runnable myRunner = new Runnable(){
			     public void run() {
			    	 new AsyncTask<Void, Void, String> () {
			    		 	private ProgressDialog dialog;
			    		 	Common common;
			    			@Override
			    			protected void onPreExecute() {
			    				common = Common.getInstance();
			    				dialog = new ProgressDialog(Splash.this);
			    				this.dialog.setMessage("로그인 중입니다...");
			    				   this.dialog.setCancelable(false);
			    				   this.dialog.show();
			    			}
			    			
			    			@Override
			    			protected String doInBackground(Void... params) {
			    				String jsonString = common.getJsonFromServer();
			    				return jsonString;
			    			}
			    			
			    			@Override
			    			protected void onPostExecute(String result) {
			    				
			    				this.dialog.setCancelable(true);
			    				   if(this.dialog.isShowing()==true)
			    				         this.dialog.dismiss();
			    				
			    				if (result != "error" ) {
			    					ArrayList<Map<String,String>> list = common.getMapFromJsonString(result);
			    					
			    					if ( list.size() == 0 ) {
			    						Toast.makeText(Splash.this, "아이디아 비밀번호를 다시한번 확인해 주세요", 3000).show();
			    						return;
			    					}
			    					Log.e("Splash", "list.size : "+list.size());
			    					Log.e("Splash", "liset.get(0) : "+list.get(0));
			    					Map<String,String> map = list.get(0);
			    					
			    					
			    					//로그인 성공시
			    					if ( (""+map.get("id")).equalsIgnoreCase(edtId.getText().toString().trim()) &&
			    						 (""+map.get("pw")).equalsIgnoreCase(edtPassword.getText().toString().trim())) {
			    						Log.e("Splash", "in login ok");
			    						SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
			    				        SharedPreferences.Editor editor = pref.edit();
			    				        editor.putString("id", edtId.getText().toString().trim());
			    				        editor.commit();
			    						
			    						startActivity(new Intent(Splash.this, Main.class));
			    					} else {
			    						Toast.makeText(Splash.this, "아이디아 비밀번호를 다시한번 확인해 주세요", 3000).show();
			    					}
			    				}
			    			}
			    		}.execute();
			}};
			myHandler.post(myRunner);
/******************************************************************************************/
			
			break;
		case R.id.splash_text_join :
			startActivity(new Intent(Splash.this, Join.class));
			break;
		}
		
	}
}
