package org.nhnnext;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class Join extends Activity implements OnClickListener {

	private LinearLayout basicFrame, additionalFrame;
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

		btnProfile.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.join_imgbtn_profile:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(Images.Media.CONTENT_TYPE);
			intent.setData(Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, REQUEST_IMAGE);
			break;
		case R.id.join_btn_next:
			new Thread() {
				public void run() {
					
					
				}
			}.start();
			
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
			new Thread() {
				public void run() {
					Common common = new Common(Join.this);
					common.uploadProfile(303, filePath);
					common.getJsonFromServer("select * from book");
				}
			}.start();
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
				Log.e("Join","filePath : "+filePath);
				fileName = uri.getLastPathSegment();
				Log.e("Join","fileName : "+fileName);

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
}
