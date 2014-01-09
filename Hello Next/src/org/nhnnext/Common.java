package org.nhnnext;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Common {

	Context context;
	private static Common instance = new Common();
	private String nextExecuteQuery;

	private Common() {}

	public static void centerToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
				0, 0);
		toast.show();
	}

	public Drawable LoadImageFromWebOperations(String strPhotoUrl) 
  {
      try {
      	InputStream is = (InputStream) new URL(strPhotoUrl).getContent();
      	Drawable drawable = Drawable.createFromStream(is, "src name");
      	return drawable;
      }catch (Exception e) {
      	Log.e("BookList","LoadImageFromWebOperations Error");
      	e.printStackTrace();
      }
		return null;
  }
	
	public static Common getInstance() {
		return instance;
	}
	
	public static boolean checkNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiConn = ni.isConnected();
		ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = ni.isConnected();
		if (isWifiConn == true || isMobileConn == true)
			return true;
		centerToast(context, "네트워크 연결에 실패하였습니다.\n인터넷 연결상태를 확인해 주세요.");
		return false;
	}

	public ArrayList<Map<String,String>> getMapFromJsonString(String json) {
		Gson gson = new Gson();
		
		ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Type listType = new TypeToken<List<Map<String, String>>>() {}.getType();
		list = gson.fromJson(json, listType);
		
		System.out.println(list);

		return list;
	}
	
	
	public void setNextExecuteQuery(String nextExecuteQuery) {
		this.nextExecuteQuery = nextExecuteQuery;
	}

	public boolean uploadProfile(int id, String filePath) {
		
		String tempURL = "http://10.73.43.166:3080/DBProject/saveImage";// "http://192.168.1.135:3080/DBProject/saveImage";
		
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		URL connectUrl  = null;
		HttpURLConnection conn = null;
		DataOutputStream dos  = null;
		
		FileInputStream mFileInputStream = null;
		try {

			mFileInputStream = new FileInputStream(filePath);
			connectUrl = new URL(tempURL);
			Log.d("Common", "mFileInputStream  is " + mFileInputStream);

			// open connection
			conn = (HttpURLConnection) connectUrl
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			// write data
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ id + ".png\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			Log.d("Common", "image byte is " + bytesRead);

			// read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			Log.e("Test", "File is written");
			mFileInputStream.close();
			dos.flush(); // finish upload...

			// get response
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer sb = new StringBuffer();
			while ((ch = is.read()) != -1) {
				sb.append((char) ch);
			}
			String s = sb.toString();
			Log.e("Common", "result = " + s);
			dos.close();

		} catch (Exception e) {
			Log.d("Common", "exception " + e.getMessage());
		}

		return true;
	}
	
	public String getJsonFromServer() {

		BufferedReader br = null;
		StringBuilder sb = null;
		String resultJsonString = "[]";
		try {
			String tempURL = "http://10.73.43.166:3080/DBProject/";
			URL url = new URL(tempURL); 	

			// HttpURLConnection으로 url의 주소를 연결합니다.
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// 서버 접속시의 Time out(ms)
			conn.setConnectTimeout(10 * 1000);
			// Read시의 Time out(ms)
			conn.setReadTimeout(10 * 1000);

			conn.setDoOutput(true);
			// once you set the output to true, you don't really need to set the
			// request method to post, but I'm doing it anyway

			// 요청 방식 선택
			conn.setRequestMethod("POST");
			// 연결을 지속하도록 함
			conn.setRequestProperty("Connection", "Keep-Alive");
			// 캐릭터셋을 UTF-8로 요청
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			//String qr = URLEncoder.encode(query,"UTF-8");
			Log.e("Common", "sql = "+nextExecuteQuery);
			// 캐시된 데이터를 사용하지 않고 매번 서버로부터 다시 받음
			conn.setRequestProperty("Cache-Controll", "no-cache");
			// 서버로부터 JSON 형식의 타입으로 데이터 요청
			conn.setRequestProperty("Accept", "application/json");

			// InputStream으로 서버에서부터 응답을 받겠다는 옵션
			conn.setDoInput(true);

			PrintWriter out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
			//out.print(query);
			out.print(nextExecuteQuery);
			out.flush();
			out.close();

			// 위에서 Request Header정보를 설정해 주고 connect()로 연결을 한다.
			//conn.connect();

			int status = conn.getResponseCode();
			System.out.println("status : " + status);
			switch (status) {
			case 200:
			case 201:
				br = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				sb = new StringBuilder();
				String line = br.readLine();

				
				if ( line == null ) {
					return resultJsonString;
				}
				Log.e("Common", "line : "+line);
				
				while ( line != null) {
					sb.append(line + "\n");
					line = br.readLine();
				}
				br.close();
				
				Log.e("Common","result : " + sb.toString());
				resultJsonString = sb.toString();
				break;
			case 400:
				resultJsonString = "error";
			}

			
		} catch (Exception e) {
			e.printStackTrace();
			resultJsonString = "error";
		}
		Log.e("Common", "resultJsonString : "+resultJsonString);
		return resultJsonString;
	}
}
