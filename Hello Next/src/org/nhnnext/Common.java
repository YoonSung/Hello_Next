package org.nhnnext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
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

	public String getJsonFromServer() {

		BufferedReader br = null;
		StringBuilder sb = null;

		try {

			URL url = new URL("http://10.73.43.166:3080/DBProject/");// +URLEncoder.encode("select *from YoonSung",
																		// "UTF-8"));

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
			out.print("select * from YoonSung");
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
}
