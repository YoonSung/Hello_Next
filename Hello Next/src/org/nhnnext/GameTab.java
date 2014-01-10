package org.nhnnext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GameTab extends Activity implements OnClickListener{

	Button btn1, btn2, btn3, btn4, btn5, btnNewQuestion;
	ImageView imgQuestion;
	
	TextView txtQuestion;
	int answerNumber;
	private ArrayList<Map<String,String>> list;
	private ArrayList<Button> btnArray;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.game_tab);
	    
	    imgQuestion = (ImageView) findViewById(R.id.game_img_question);
	    
	    txtQuestion = (TextView) findViewById(R.id.game_txt_question);
	    
	    btnNewQuestion = (Button) findViewById(R.id.game_btn_newQuestion); 
	    btn1 = (Button) findViewById(R.id.game_btn_1);
	    btn2 = (Button) findViewById(R.id.game_btn_2);
	    btn3 = (Button) findViewById(R.id.game_btn_3);
	    btn4 = (Button) findViewById(R.id.game_btn_4);
	    btn5 = (Button) findViewById(R.id.game_btn_5);
	    
	    btnNewQuestion.setOnClickListener(this);
	    btn1.setOnClickListener(this);
	    btn2.setOnClickListener(this);
	    btn3.setOnClickListener(this);
	    btn4.setOnClickListener(this);
	    btn5.setOnClickListener(this);
	    
	    btnArray = new ArrayList<Button>();
	    btnArray.add(btn1);
	    btnArray.add(btn2);
	    btnArray.add(btn3);
	    btnArray.add(btn4);
	    btnArray.add(btn5);
	    
	    fetchData();
	}

	@Override
	public void onClick(View v) {
		
		int choiceNum = 0;
		
		switch (v.getId()) {
		case R.id.game_btn_newQuestion :
			fetchData();
			return;
		case R.id.game_btn_1:
			choiceNum = 0;
			break;
		case R.id.game_btn_2:
			choiceNum = 1;
			break;
		case R.id.game_btn_3:
			choiceNum = 2;
			break;
		case R.id.game_btn_4:
			choiceNum = 3;
			break;
		case R.id.game_btn_5:
			choiceNum = 4;
			break;
		}
		
		if ( choiceNum == answerNumber ){
			txtQuestion.setText("정답을 맞추셨습니다.\n점수를 획득했습니다.");
		} else {
			txtQuestion.setText("틀렸습니다.\n정답은 '"+answerNumber+"'번입니다.\n점수가 차감됩니다.");
		}
	}

	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		  ImageView bmImage;

		  public DownloadImageTask(ImageView bmImage) {
		      this.bmImage = bmImage;
		  }

		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      Bitmap mIcon11 = null;
		      try {
		        InputStream in = new java.net.URL(urldisplay).openStream();
		        mIcon11 = BitmapFactory.decodeStream(in);
		      } catch (Exception e) {
		          Log.e("Error", e.getMessage());
		          e.printStackTrace();
		      }
		      return mIcon11;
		  }

		  protected void onPostExecute(Bitmap result) {
		      bmImage.setImageBitmap(result);
		  }
		}
	
	public void fetchData() {
		/******************************************************************************************/
		String query = "SELECT name, image FROM member WHERE id IN " 
								+"(SELECT member_id FROM professor) ORDER BY RAND() LIMIT 5;";

		Log.e("GameTap", "sql : " + query);
		Common.getInstance().setNextExecuteQuery(query);
		Handler myHandler = new Handler();
		Runnable myRunner = new Runnable() {
			public void run() {
				new AsyncTask<Void, Void, String>() {
					Common dao;
					private ProgressDialog dialog;

					@Override
					protected void onPreExecute() {
						txtQuestion.setText("문제제출을 시작합니다.\n잠시만 기다려 주세요");

						dialog = new ProgressDialog(GameTab.this);
						this.dialog.setMessage("문제 생성중...");
						this.dialog.setCancelable(false);
						this.dialog.show();
					}

					@Override
					protected String doInBackground(Void... params) {
						dao = Common.getInstance();
						String jsonString = dao.getJsonFromServer();
						return jsonString;
					}

					@Override
					protected void onPostExecute(String result) {
						this.dialog.setCancelable(true);
						if (this.dialog.isShowing() == true)
							this.dialog.dismiss();

						if (result != "error") {
							list = dao.getMapFromJsonString(result);
							answerNumber = (int) (Math.random() * 5) ;
							Map<String, String> map;
							for ( int i = 0 ; i < list.size() ; ++i ) {
								map = list.get(i);
								btnArray.get(i).setText(map.get("name"));
							}
							
							map = list.get(answerNumber);
							executeAfter(map.get("image"));
							
							
							txtQuestion.setText("정답을 맞춰 보세요!");
						} else {
							txtQuestion.setText("문제생성에 실패했습니다. 다시한번 시도해 주세요.");
						}
					}
				}.execute();
			}
		};
		myHandler.post(myRunner);
		/******************************************************************************************/
	}	
	
	public void executeAfter (String url) {
		new DownloadImageTask((ImageView) findViewById(R.id.game_img_question))
		.execute(url);
	}
	
}
