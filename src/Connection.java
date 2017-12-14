

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Connection extends JFrame{
	static String arduino;//arduinoのボード
	static String wifi_ssid;//Wi-Fi通信のSSID
	static char[] wifi_password;//Wi-Fi通信のPassword
	static String mqtt_clientid;//MQTT通信のCliendID
	static String mqtt_server;//MQTT通信のサーバー名
	static String mqtt_port;//MQTT通信のサーバーのポート番号
	static String mqtt_username;//MQTT通信のユーザーネーム
	static char[] mqtt_password;//MQTT通信のユーザーネームに対するパスワード

	//log,pos,attは同じ番号で1セットとなる。本クラスではボード関係のコードが入っている
	static ArrayList<String> log = new ArrayList<String>();//Arduinoファイルの一行分のコード内容
	static ArrayList<String> pos = new ArrayList<String>();//logを挿入する位置。MakeCodeでコードを挿入する時に使う
	static ArrayList<String> att = new ArrayList<String>();//logの属性。属性を使う事で効率的な仕分けが可能になる。


	String board;//Arduinoのボード名
	boolean isConnected = false;

	//Arduinoのボード名を返す
	public String getboard(){
		return board;
	}

	//insフォルダのCon_ファイルを読み込み、本クラスに状態を保存
	public void setup(String Con){
		try {
			File file = new File("./ins/Con_" + Con + ".txt");//insフォルダのCon_ファイルを指定
			FileReader filereader = new FileReader(file);
			board ="";//Arduinoのボード名を初期化

			//log,pos,attを初期化
			log.clear();
			pos.clear();
			att.clear();

			int ch;//chは一文字分をintにしたもの、ASCIIコード参照
			int m = 0;//列番号、改行するごとに0になる

			String temp = "";//文字chを足し合わせるもの
			String command = "";//tempによって完成した単語であり、各行の一列目の内容を示す
			while ((ch = filereader.read()) != -1) {
				if (ch == 10) {//chが改行文字ならm=0にする
					m = 0;
				} else if (ch == 58) {//chがコロンなら・・・
					if (m == 0) {//m=0ならtempで繋ぎ合わせた文字をcommandに代入
						command = temp;
					}
					if (command.equals("Package")) {//もしcommandがPackageかつm=1ならboardにtempの文字を足す
						if(m==1){
							board = board + temp;
							board = board + ":";
						}
					}
					if (command.equals("Arch")) {//もしcommandがArchかつm=1ならboardにtempの文字を足す
						if(m==1){
							board = board + temp;
							board = board + ":";
						}
					}
					if (command.equals("Board")) {//もしcommandがBoardかつm=1ならboardにtempの文字を足す
						if(m==1){
							board = board + temp;
						}
					}
					if (command.equals("Log")) {//もしcommandがLogかつm=1ならlogにtempを挿入、attにarduinoを挿入
						if (m == 1) {
							log.add(temp);
							att.add("arduino");
						} else if (m == 2) {//もしcommandがLogかつm=2ならposにtempを代入
							pos.add(temp);
						}
					}
					temp = "";//tempの内容を使ったので空白にする
					m = m + 1;
				} else {
					if (ch != 13) {//もし前の条件を満たさず（改行かコロン）かつCR以外ならtempにchを付け足す
						temp = temp + (char) ch;

					}
				}
			}
			filereader.close();
		} catch (FileNotFoundException er) {
			System.out.println(er);
		} catch (IOException er) {
			System.out.println(er);
		}

	}

	public boolean mqttisconnected(){//mqttサーバーとつながっているかどうかを出力
		return isConnected;
	}


	public String getLog(int i) {//i番目のlogを出力
		return log.get(i);
	}

	public String getPos(int i) {//i番目のposを出力
		return pos.get(i);
	}

	public String getAtt(int i) {//i番目のattを出力
		return att.get(i);
	}




	public int getLogsize() {//logのsizeを出力
		return log.size();
	}


	//以下、Wi-FiとMQTT通信に関するデータのゲッター及びセッター



	public static String getArduino() {
		return arduino;
	}

	public static String getWifi_ssid() {
		return wifi_ssid;
	}

	public static char[] getWifi_password() {
		return wifi_password;
	}

	public static String getMqtt_clientid() {
		return mqtt_clientid;
	}

	public static String getMqtt_server() {
		return mqtt_server;
	}

	public static String getMqtt_port() {
		return mqtt_port;
	}

	public static String getMqtt_username() {
		return mqtt_username;
	}

	public static char[] getMqtt_password() {
		return mqtt_password;
	}

	public void setArduino(String ar) {
		Connection.arduino = ar;
	}

	public void setWifi_ssid(String wifi_ssid) {
		Connection.wifi_ssid = wifi_ssid;
	}

	public void setWifi_password(char[] cs) {
		Connection.wifi_password = cs;
	}

	public void setMqtt_clientid(String mqtt_clientid) {
		Connection.mqtt_clientid = mqtt_clientid;
	}

	public void setMqtt_server(String mqtt_server) {
		Connection.mqtt_server = mqtt_server;
	}

	public void setMqtt_port(String mqtt_port) {
		Connection.mqtt_port = mqtt_port;
	}

	public void setMqtt_username(String mqtt_username) {
		Connection.mqtt_username = mqtt_username;
	}

	public void setMqtt_password(char[] cs) {
		Connection.mqtt_password = cs;
	}


	//トップ画面のUIにてConnectionのデータを保存する
	public void saveData() throws IOException{

		   JFileChooser filechooser = new JFileChooser("./connect");
		   FileNameExtensionFilter filter =
				      new FileNameExtensionFilter("テキストファイル", "txt");
		   filechooser.addChoosableFileFilter(filter);
		    filechooser.setAcceptAllFileFilterUsed(false);
		    int selected = filechooser.showSaveDialog(this);
		    if (selected == JFileChooser.APPROVE_OPTION){
		      File file = filechooser.getSelectedFile();
		      file = new  File("./connect/"+file.getName()+".txt");
		      PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		      pw.println(arduino);
		      pw.println(wifi_ssid);
		      pw.println(wifi_password);
		      pw.println(mqtt_clientid);
		      pw.println(mqtt_server);
		      pw.println(mqtt_port);
		      pw.println(mqtt_username);
		      pw.println(mqtt_password);
		      pw.close();
		    }else if (selected == JFileChooser.CANCEL_OPTION){
		    }else if (selected == JFileChooser.ERROR_OPTION){
		    }

	}

	//トップ画面のUIにてsaveDataにて保存したConnectionのデータを呼び出す
	public void openData(){
	    JFileChooser filechooser = new JFileChooser("./connect");
		   FileNameExtensionFilter filter =
				      new FileNameExtensionFilter("テキストファイル", "txt");
		   filechooser.addChoosableFileFilter(filter);
		    filechooser.setAcceptAllFileFilterUsed(false);

	    int selected = filechooser.showOpenDialog(this);
	    if (selected == JFileChooser.APPROVE_OPTION){
	      File file = filechooser.getSelectedFile();


	      try{
	        if (checkBeforeReadfile(file)){
	          BufferedReader br
	            = new BufferedReader(new FileReader(file));

	          String str;
	          int num=0;
	          while((str = br.readLine()) != null){
	        	  switch(num){
	        	  case 0:
	        		  arduino = str;
	        		  break;
	        	  case 1:
	        		  wifi_ssid = str;
	        		  break;
	        	  case 2:
	        		  wifi_password = str.toCharArray();
	        		  break;
	        	  case 3:
	        		  mqtt_clientid = str;
	        		  break;
	        	  case 4:
	        		  mqtt_server= str;
	        		  break;
	        	  case 5:
	        		  mqtt_port = str;
	        		  break;
	        	  case 6:
	        		  mqtt_username = str;
	        		  break;
	        	  case 7:
	        		  mqtt_password = str.toCharArray();
	        		  break;
	        	  }
	        	  num++;
	          }
	          br.close();
	        }else{
	          System.out.println("ファイルが見つからないか開けません");
	        }
	      }catch(FileNotFoundException err){
	        System.out.println(err);
	      }catch(IOException err){
	        System.out.println(err);
	      }
	    }
	}

	 private static boolean checkBeforeReadfile(File file){
		    if (file.exists()){
		      if (file.isFile() && file.canRead()){
		        return true;
		      }
		    }

		    return false;
		  }
}
