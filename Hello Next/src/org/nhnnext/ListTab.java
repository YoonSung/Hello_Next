package org.nhnnext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListTab extends Activity implements OnClickListener{

	private ArrayList<ListData> listDataArrayList = new ArrayList<ListData>();
	private String mID;
	private ArrayList<Map<String,String>> list;
	private ListView listView; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_tab);
		
		//StrictMode.setThreadPolicy(new StrictMode. ThreadPolicy.Builder().permitNetwork().build());
		
		SharedPreferences prefs = getSharedPreferences("pref", MODE_PRIVATE); 
		mID = prefs.getString("id", null);
		
		if (mID == null) {
			Toast.makeText(this, "로그인상태를 다시한번 확인해 주세요", 3000).show();
			finish();
		}
		
		listView = (ListView)findViewById(R.id.listtab_list);
		
/******************************************************************************************/
		String query = "SELECT m.name, m.image, s.village, GROUP_CONCAT(u.name) AS subname " 
								+ "FROM member m INNER JOIN status s ON m.id = s.member_id INNER JOIN "
								+ "subject u ON m.id = u.professor_id GROUP BY m.id;";
		
		Log.e("ListTab", "sql : " + query);
		Common.getInstance().setNextExecuteQuery(query);
		Handler myHandler = new Handler();
		Runnable myRunner = new Runnable(){
		     public void run() {
		    	 new AsyncTask<Void, Void, String> () {
		    		 	Common dao;
		    			@Override
		    			protected void onPreExecute() {
		    			}
		    			
		    			@Override
		    			protected String doInBackground(Void... params) {
		    				dao = Common.getInstance();
		    				String jsonString = dao.getJsonFromServer();
		    				return jsonString;
		    			}
		    			
		    			@Override
		    			protected void onPostExecute(String result) {
		    				
		    				if (result != "error" ) {
		    					list = dao.getMapFromJsonString(result);
		    					updateView();
		    				}
		    			}
		    		}.execute();
		}};
		myHandler.post(myRunner);
/******************************************************************************************/
		
		
	}
	
	public void updateViewWithOption(String query) {
		Common.getInstance().setNextExecuteQuery(query);
		Handler myHandler = new Handler();
		Runnable myRunner = new Runnable(){
		     public void run() {
		    	 new AsyncTask<Void, Void, String> () {
		    		 	Common dao;
		    			@Override
		    			protected void onPreExecute() {
		    			}
		    			
		    			@Override
		    			protected String doInBackground(Void... params) {
		    				dao = Common.getInstance();
		    				String jsonString = dao.getJsonFromServer();
		    				return jsonString;
		    			}
		    			
		    			@Override
		    			protected void onPostExecute(String result) {
		    				
		    				if (result != "error" ) {
		    					list = dao.getMapFromJsonString(result);
		    					updateView();
		    				}
		    			}
		    		}.execute();
		}};
		myHandler.post(myRunner);
	}
	
	public void updateView () {
		if (list != null ) {
			for ( int i = 0 ; i < list.size() ; i++ ) {
				Map<String, String> map = list.get(i);
				
//				Log.e("ListTab_map_name", map.get("name"));
//				Log.e("ListTab_map_village", map.get("village"));
//				Log.e("ListTab_map_subname", map.get("subname"));
				
				listDataArrayList.add(
						new ListData(
								map.get("name"),
								map.get("image"),
								Integer.parseInt(map.get("village")),
								map.get("subname")
								));
			}
		}
		
		ListTabAdapter ListTabAdapter = new ListTabAdapter(this, R.layout.list_tab_cell, listDataArrayList);
		listView.setAdapter(ListTabAdapter);
		
		
	}
	
	private class ListTabAdapter extends ArrayAdapter<ListData> implements
			OnClickListener {

		private ArrayList<ListData> items;
		private Common common;
		private ListData listData;
		//BookListData k;

		public ListTabAdapter(Context context, int textViewResourceId,
				ArrayList<ListData> items) {
			super(context, textViewResourceId, items);
			this.items = items;
			this.common = Common.getInstance();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.list_tab_cell, null);
			}

			listData = items.get(position);
			if (listData != null) {

				TextView name, subject, village;
				ImageView image;
				LinearLayout row;

				row = (LinearLayout) view.findViewById(R.id.listtab_row);
				name = (TextView) view.findViewById(R.id.listtab_custom_name);
				subject = (TextView) view.findViewById(R.id.listtab_custom_subject);
				village = (TextView) view.findViewById(R.id.listtab_custom_village);
				image = (ImageView) view.findViewById(R.id.listtab_custom_image);
				
				row.setTag(position);
				row.setOnClickListener(this);

//				Log.e("ListTab", "getName : "+listData.getName());
//				Log.e("ListTab", "getVillage : "+listData.getVillage());
//				Log.e("ListTab", "getSubject : "+listData.getSubject());

				
				name.setText("이름 : "+listData.getName());
				village.setText("마을 : "+listData.getVillage());
				subject.setText("수업 : "+listData.getSubject());
				
				if ( listData.getImgURL() != null ) {
					new DownloadImageTask((ImageView) view.findViewById(R.id.listtab_custom_image))
			       .execute(listData.getImgURL());
					//image.setImageDrawable(common.LoadImageFromWebOperations(listData.getImgURL()));
				} else {
					new DownloadImageTask((ImageView) view.findViewById(R.id.listtab_custom_image))
			        .execute("https://cdn2.iconfinder.com/data/icons/picons-basic-1/57/basic1-025_book_reading-32.png");
					//image.setImageDrawable(common.LoadImageFromWebOperations("https://cdn2.iconfinder.com/data/icons/picons-basic-1/57/basic1-025_book_reading-32.png"));
				}
			}

			return view;
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
		
		
		@Override
		public void onClick(View v) {

			//bookdata에서 책정보를 다음액티비티로 전달한다.
			//Intent intent = new Intent(ListTab.this, LogList.class);
			//intent.putExtra("bookTitle", bookData.getTitle());
			
			//startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = null;
		
//		switch (v.getId()) {
//		case R.id.booklist_btnWrite:
//			intent = new Intent(BookList.this, ReadBook.class);
//			break;
//		case R.id.booklist_findGroup:
//			intent = new Intent(BookList.this, FindGroup.class);
//			break;
//		}
		
		if ( intent != null )
			startActivity(intent);
		
	}
}