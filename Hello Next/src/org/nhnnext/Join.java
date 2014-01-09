package org.nhnnext;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class Join extends Activity implements OnClickListener,
		OnItemSelectedListener {

	private LinearLayout basicFrame, additionalFrame;

	private EditText edtId, edtPassword, edtPasswordConfirm, edtName,
			edtNickname, edtAddress;
	private Spinner spinnerBirth, spinnerGrade;
	private int mBirth, mGrade;

	private Button btnNext, btnRegister;
	private ImageButton btnProfile;
	private static final int REQUEST_IMAGE = 1;
	private String filePath;
	private String fileName;

	private Animation ani;
	private int duraction = 800;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join);

		basicFrame = (LinearLayout) findViewById(R.id.join_basic_frame);
		additionalFrame = (LinearLayout) findViewById(R.id.join_addtional_frame);
		btnNext = (Button) findViewById(R.id.join_btn_next);
		btnRegister = (Button) findViewById(R.id.join_btn_register);
		btnProfile = (ImageButton) findViewById(R.id.join_imgbtn_profile);

		edtId = (EditText) findViewById(R.id.join_edt_id);
		edtPassword = (EditText) findViewById(R.id.join_edt_pw);
		edtPasswordConfirm = (EditText) findViewById(R.id.join_edt_pwRe);
		edtName = (EditText) findViewById(R.id.join_edt_name);
		edtNickname = (EditText) findViewById(R.id.join_edt_nickname);

		edtAddress = (EditText) findViewById(R.id.join_edt_address);
		spinnerBirth = (Spinner) findViewById(R.id.join_spinner_birth);
		spinnerGrade = (Spinner) findViewById(R.id.join_spinner_grade);
		mBirth = 0;
		mGrade = 0;

		btnProfile.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnRegister.setOnClickListener(this);

		spinnerBirth.setOnItemSelectedListener(this);
		spinnerGrade.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = Integer.parseInt(edtId.getText().toString().trim());
		String password = edtPassword.getText().toString().trim();
		String passwordConfirm = edtPasswordConfirm.getText().toString().trim();
		String name = edtName.getText().toString().trim();
		String nickname = edtNickname.getText().toString().trim();;
		String address = edtAddress.getText().toString().trim();;
		int birth = 0;
		int grade = 0;
		birth = mBirth;
		grade = mGrade;

		
		switch (v.getId()) {
		case R.id.join_imgbtn_profile:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(Images.Media.CONTENT_TYPE);
			intent.setData(Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQUEST_IMAGE);
			break;
		case R.id.join_btn_next:

			

			if (!password.equalsIgnoreCase(passwordConfirm)) {
				Common.centerToast(Join.this, "비밀번호 입력값이 서로 다릅니다.");
				return;
			}

			if (id == 0 || password.equalsIgnoreCase("")
					|| passwordConfirm.equalsIgnoreCase("")
					|| name.equalsIgnoreCase("")
					|| nickname.equalsIgnoreCase("") || filePath == null) {
				Common.centerToast(Join.this, "필수영역을 모두 입력해 주세요");
				return;
			}

			ani = new AlphaAnimation(1.0f, 0.0f);
			ani.setDuration(duraction);
			ani.setFillAfter(true);
			basicFrame.startAnimation(ani);

			additionalFrame.setVisibility(View.VISIBLE);
			ani = new AlphaAnimation(0.0f, 1.0f);
			ani.setDuration(duraction);
			ani.setFillAfter(true);
			additionalFrame.startAnimation(ani);
			break;
		case R.id.join_btn_register:
			if (birth == 0 && grade == 0) {
				;
			} else if (grade == 0) {
				;
			} else if (birth == 0) {
				;
			}

			/******************************************************************************************/
			String query = "INSERT INTO member(id, pw, name, nickname, image) VALUES ("
					+ id
					+ ", "
					+ "'"
					+ password
					+ "'"
					+ ", "
					+ "'"
					+ name
					+ "'"
					+ ", "
					+ "'"
					+ nickname
					+ "'" + ", " + "'http://10.73.43.166:3080/images/" + id + ".png" + "'" + ");";

			Log.e("Main", "sql : " + query);
			Common.getInstance().setNextExecuteQuery(query);
			Handler myHandler = new Handler();
			Runnable myRunner = new Runnable() {
				public void run() {
					new AsyncTask<Void, Void, String>() {
						Common common;
						private ProgressDialog dialog;
						
						@Override
						protected void onPreExecute() {
							dialog = new ProgressDialog(Join.this);
		    				this.dialog.setMessage("회원가입 중입니다...");
		    				   this.dialog.setCancelable(false);
		    				   this.dialog.show();
						}

						@Override
						protected String doInBackground(Void... params) {
							common = Common.getInstance();
							String jsonString = common.getJsonFromServer();
							return jsonString;
						}

						@Override
						protected void onPostExecute(String result) {

							if (result != "error" ) {
		    					new AlertDialog.Builder(Join.this)
		    					.setTitle("회원가입을 축하합니다.")
		    					.setPositiveButton("로그인하러 가기", new DialogInterface.OnClickListener() {
		    						@Override
		    						public void onClick(DialogInterface dialog, int which) {
		    							finish();
		    						}
		    					}).create().show();
		    				} else {
		    					new AlertDialog.Builder(Join.this)
		    					.setTitle("오류")
		    					.setMessage("네트워크 오류로 회원가입에 실패하였습니다.\n잠시후 다시 시도해 주세요..")
		    					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
		    						@Override
		    						public void onClick(DialogInterface dialog, int which) {
		    						}
		    					}).create().show();
		    				}
						}
					}.execute();
				}
			};
			myHandler.post(myRunner);
			/******************************************************************************************/

			
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			if (requestCode == REQUEST_IMAGE) {
				Uri uri = getRealPathUri(data.getData());
				filePath = uri.toString();
				Log.e("Join", "filePath : " + filePath);
				fileName = uri.getLastPathSegment();
				Log.e("Join", "fileName : " + fileName);

				Bitmap bitmap = BitmapFactory.decodeFile(filePath);
				btnProfile.setImageBitmap(bitmap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Uri getRealPathUri(Uri uri) {
		Uri filePathUri = uri;
		if (uri.getScheme().toString().compareTo("content") == 0) {
			Cursor cursor = getApplicationContext().getContentResolver().query(
					uri, null, null, null, null);
			if (cursor.moveToFirst()) {
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				filePathUri = Uri.parse(cursor.getString(column_index));
			}
		}
		return filePathUri;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		Spinner spinner = (Spinner) parent;
		if (spinner.getId() == R.id.join_spinner_birth) {
			mBirth = Integer.parseInt(parent.getItemAtPosition(position)
					.toString());
		} else if (spinner.getId() == R.id.join_spinner_grade) {
			mGrade = Integer.parseInt(parent.getItemAtPosition(position)
					.toString());
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
