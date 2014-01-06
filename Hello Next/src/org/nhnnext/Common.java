package org.nhnnext;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Common {

	Context context;
	static SharedPreferences spf;
	static SharedPreferences.Editor editor;

	public Common(Context context) {
		this.context = context;
		spf = PreferenceManager.getDefaultSharedPreferences(context);
		editor = spf.edit();
	}

	public static void savePreference(String inputText) {
		editor.putString("id", inputText);
		editor.commit();
	}

	public static void centerToast(Context context, String message) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
				0, 0);
		toast.show();
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

	public String getJsonFromServer(String query) {

		BufferedReader br = null;
		StringBuilder sb = null;

		try {
			String tempURL = "http://10.73.43.166:3080/DBProject/";// "http://192.168.1.135:3080/DBProject/";
			URL url = new URL(tempURL);
			// +URLEncoder.encode("select *from YoonSung","UTF-8"));

			// HttpURLConnection으로 url의 주소를 연결합니다.
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// 서버 접속시의 Time out(ms)
			conn.setConnectTimeout(10 * 100000);
			// Read시의 Time out(ms)
			conn.setReadTimeout(10 * 100000);

			conn.setDoOutput(true);
			// once you set the output to true, you don't really need to set the
			// request method to post, but I'm doing it anyway

			// 요청 방식 선택
			conn.setRequestMethod("POST");
			// 연결을 지속하도록 함
			conn.setRequestProperty("Connection", "Keep-Alive");
			// 캐릭터셋을 UTF-8로 요청
			conn.setRequestProperty("Accept-Charset", "UTF-8");

			// 캐시된 데이터를 사용하지 않고 매번 서버로부터 다시 받음
			conn.setRequestProperty("Cache-Controll", "no-cache");
			// 서버로부터 JSON 형식의 타입으로 데이터 요청
			conn.setRequestProperty("Accept", "application/json");

			// InputStream으로 서버에서부터 응답을 받겠다는 옵션
			conn.setDoInput(true);

			PrintWriter out = new PrintWriter(conn.getOutputStream());
			out.print(query);
			out.close();

			// 위에서 Request Header정보를 설정해 주고 connect()로 연결을 한다.
			conn.connect();

			int status = conn.getResponseCode();
			System.out.println("status : " + status);
			switch (status) {
			case 200:
			case 201:
				br = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				sb = new StringBuilder();
				String line;

				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				break;
			case 400:
				break;
			}

			System.out.println("result : " + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "test";// sb.toString();
	}

	public boolean uploadProfile(int id, String filePath) {
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		FileInputStream fis = null;
		URL url = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		int responseCode = 0;

		try {
			System.out.println("start\n");
			fis = new FileInputStream(filePath);
			Log.d("Common", "FileInputStream  is " + fis);
			String tempURL = "http://10.73.43.166:3080/DBProject/saveImage";// "http://192.168.1.135:3080/DBProject/saveImage";
			Log.e("Common", "tempURL: "+tempURL);
			url = new URL(tempURL);
			conn = (HttpURLConnection) url.openConnection();

			// 서버 접속시의 Time out(ms)

			conn.setConnectTimeout(10 * 100000);

			// Read시의 Time out(ms)

			conn.setReadTimeout(10 * 100000);
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");
			// 연결을 지속하도록 함
			conn.setRequestProperty("Connection", "Keep-Alive");

			// 캐릭터셋을 UTF-8로 요청
			conn.setRequestProperty("Accept-Charset", "UTF-8");

			// 캐시된 데이터를 사용하지 않고 매번 서버로부터 다시 받음
			conn.setRequestProperty("Cache-Controll", "no-cache");
			// conn.setRequestProperty("Transfer-Encoding", "chunked-Alive");

			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			conn.setDoInput(true);

			dos = new DataOutputStream(conn.getOutputStream());

			int bytesAvailable = fis.available();

			Log.e("Common", "bytesAvailable : " + bytesAvailable);

			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			Log.e("Common", "bufferSize : " + bufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = fis.read(buffer, 0, bufferSize);
			Log.e("Common", "bytesRead : " + bytesRead);

			// read image
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fis.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fis.read(buffer, 0, bufferSize);
				Log.e("Common", "bytesRead in while: " + bytesRead);
			}
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			dos.flush();

			responseCode = conn.getResponseCode();
			System.out.println("end\n");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
				// dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Log.e("Common", "responseCode : " + responseCode);

		if (responseCode == 200 || responseCode == 201)
			return true;

		return false;
	}

	public void DoFileUpload(String absolutePath) {
		HttpFileUpload("http://10.73.43.166:3080/DBProject/saveImage/16", "", absolutePath);
	}

	public void HttpFileUpload(String urlString, String params, String fileName) {
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {

			FileInputStream mFileInputStream = new FileInputStream(fileName);
			URL connectUrl = new URL(urlString);
			Log.d("Test", "mFileInputStream  is " + mFileInputStream);

			// open connection
			HttpURLConnection conn = (HttpURLConnection) connectUrl
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			// write data
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
					+ fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

			Log.d("Test", "image byte is " + bytesRead);

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
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			String s = b.toString();
			Log.e("Test", "result = " + s);
			//mEdityEntry.setText(s);
			dos.close();

		} catch (Exception e) {
			Log.d("Test", "exception " + e.getMessage());
			// TODO: handle exception
		}
	}
}
