

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class statePrepare extends JFrame implements ActionListener {

	// Pub・SubできるTopic数
	static int num_of_topic = 1;
	static int num_of_state = 1;

	// UIのWi-FiおよびMQTTサーバー接続設定のテキストエリア（入力フォーム）
	JTextField TOPICNUM;//Topic数
	JTextField WIFI_SSID;//Wi-Fi通信におけるのSSID
	JPasswordField WIFI_PASSWORD;//Wi-Fi通信におけるのPassword
	JTextField MQTT_CLIENTID;//MQTT通信におけるClientID.他のパブリッシャー、サブスクライバーとClientIDで被ってはいけない。
	JTextField MQTT_SERVER;//MQTT通信における接続するサーバー名
	JTextField MQTT_PORT;//MQTT通信における接続するのサーバーのポート
	JTextField MQTT_USERNAME;//MQTT通信におけるユーザーネーム
	JPasswordField MQTT_PASSWORD;//MQTT通信におけるユーザーネームに対するパスワード
	JTextField COMPORT;//COMポートを入力、ArduinoのIDEで確認


	// UIのPubSub設定のテキストエリア
	JComboBox<String> mqtt_frame = new JComboBox<String>();// Arduinoのボード選択
	ArrayList<JComboBox<String>> pubsub_combo = new ArrayList<JComboBox<String>>();//PubかSubを設定するコンボボックス
	ArrayList<JTextField> topic = new ArrayList<JTextField>();//topicを入力する欄
	ArrayList<JButton> config = new ArrayList<JButton>();//topicの設定をするボタン
	ArrayList<JLabel> what = new ArrayList<JLabel>();//
	ArrayList<stateMain> state = new ArrayList<stateMain>();

	// UI自体が持つstatic変数
	static int select;//Topicを複数設定するときに使う。何番目のTopicか
	ArrayList<Topic> topicArray = new ArrayList<Topic>();//TopicクラスのArrayList
	static Connection connect;//Wi-FiやMQTT通信に関するデータが入っているクラス

	static statePrepare frame;//UIに関するクラス

	statePrepare(){
		connect = new Connection();
		connect.setWifi_ssid("hogeID");
		connect.setWifi_password("hogePass".toCharArray());
		connect.setMqtt_clientid("hogeClient");
		connect.setMqtt_server("hogeServer");
		connect.setMqtt_port("hogePort");
		connect.setMqtt_username("hogeUsei");
		connect.setMqtt_password("hogee!!".toCharArray());

	}
	// メインUIを描写する関数
	statePrepare(String title) {
		setVisible(true);//UIを可視化
		setResizable(false);//UIのサイズ変更不可

		//UIの初期設定
		setBounds(30, 30, 700, num_of_topic * 50 + 300);//UIのサイズ指定
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//画面の×ボタン押したときに綴じるように設定
		setTitle(title);//UIのタイトル指定
		ImageIcon icon = new ImageIcon("./img/MQTT.png");//UIのアイコンURL指定
		setIconImage(icon.getImage());//アイコンをUIに張り付ける

		//ボードのファイルの選択肢
		File file = new File("./\\ins");//insファイルを参照
		File files[] = file.listFiles();
		mqtt_frame.addItem("");//ボードのコンボボックスに空白を追加
		for (int i = 0; i < files.length; i++) {//このfor分でinsフォルダの中のうち、ファイル名の最初3文字がConであるファイルをボードのコンボボックスに追加
			String filename = files[i].getName();
			if (filename.substring(0, 3).equals("Con")) {
				mqtt_frame.addItem(filename.substring(4, filename.length() - 4));
			}
		}

		//UIの通信関係の入力フォーム・ボタンの初期化・設定
		WIFI_SSID = new JTextField("", 20);
		WIFI_PASSWORD = new JPasswordField("", 20);
		MQTT_CLIENTID = new JTextField("", 20);
		MQTT_SERVER = new JTextField("", 20);
		MQTT_PORT = new JTextField("", 20);
		MQTT_USERNAME = new JTextField("", 20);
		MQTT_PASSWORD = new JPasswordField("", 20);
		COMPORT = new JTextField(5);
		JButton button_save = new JButton("Save");
		JButton button_open = new JButton("Open");
		JButton button_finish = new JButton("Finish");
		button_save.addActionListener(this);
		button_open.addActionListener(this);
		button_finish.addActionListener(this);
		mqtt_frame.addActionListener(this);







		//以下、UIの描写に関する内容。一行空きはUIにおける改行部分
		setLayout(new BorderLayout());

		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.add(mqtt_frame);
		p.add(button_save);
		p.add(button_open);
		p.add(button_finish);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel wifi_ssid_print = new JLabel("Wi-Fi SSID");
		wifi_ssid_print.setPreferredSize(new Dimension(110, 20));
		p.add(wifi_ssid_print);
		p.add(WIFI_SSID);
		JLabel wifi_password_print = new JLabel("Wi-Fi PASSWORD");
		wifi_password_print.setPreferredSize(new Dimension(110, 20));
		p.add(wifi_password_print);
		p.add(WIFI_PASSWORD);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel clientid_print = new JLabel("Client ID");
		clientid_print.setPreferredSize(new Dimension(110, 20));
		p.add(clientid_print);
		p.add(MQTT_CLIENTID);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel server_print = new JLabel("MQTT SERVER");
		server_print.setPreferredSize(new Dimension(110, 20));
		p.add(server_print);
		p.add(MQTT_SERVER);
		JLabel port_print = new JLabel("MQTT PORT");
		port_print.setPreferredSize(new Dimension(110, 20));
		p.add(port_print);
		p.add(MQTT_PORT);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel username_print = new JLabel("MQTT USERNAME");
		username_print.setPreferredSize(new Dimension(110, 20));
		p.add(username_print);
		p.add(MQTT_USERNAME);
		JLabel password_print = new JLabel("MQTT PASSWORD");
		password_print.setPreferredSize(new Dimension(110, 20));
		p.add(password_print);
		p.add(MQTT_PASSWORD);
		main.add(p);

		add("North", main);

		connect=new Connection();
	}


	public Connection returnConnect(){
		return connect;
	}

	// ボタンを押したときに呼ばれる関数
	public void actionPerformed(ActionEvent e) {
		String cn = e.getActionCommand();//コマンド名の取得

		//connectクラスに通信系の設定をsetする
		connect.setWifi_ssid(WIFI_SSID.getText());
		connect.setWifi_password(WIFI_PASSWORD.getPassword());
		connect.setMqtt_clientid(MQTT_CLIENTID.getText());
		connect.setMqtt_server(MQTT_SERVER.getText());
		connect.setMqtt_port(MQTT_PORT.getText());
		connect.setMqtt_username(MQTT_USERNAME.getText());
		connect.setMqtt_password(MQTT_PASSWORD.getPassword());



		//もしボードがArduinoYunならWi-Fiの設定を入力不可にする
		if (mqtt_frame.getSelectedItem().equals("ArduinoYun")) {
			WIFI_SSID.setEditable(false);
			WIFI_SSID.setText("");
			WIFI_PASSWORD.setEditable(false);
			WIFI_PASSWORD.setText("");
		} else {
			WIFI_SSID.setEditable(true);
			WIFI_PASSWORD.setEditable(true);
		}

		//以下コマンド名による場合分け、コマンド名はボタン名に準ずる
		if(cn.equals("Finish")){
			Component c = (Component) e.getSource();
			Window w = SwingUtilities.getWindowAncestor(c);
			w.dispose();
		}else if(cn.equals("Save")){//もしボタン名がSaveなら
			try {
				connect.saveData();//Connectionクラスから通信関係のデータを保存する
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}

		}else if(cn=="Open"){//もしボタン名がOpenなら
			connect = new Connection();
			connect.openData();//Connectionクラスから通信関係のデータを呼び出す

			//以下、呼び出したデータを入力フォームに書き込む
			WIFI_SSID.setText(connect.getWifi_ssid());
			WIFI_PASSWORD.setText(String.valueOf(connect.getWifi_password()));
			MQTT_CLIENTID.setText(connect.getMqtt_clientid());
			MQTT_SERVER.setText(connect.getMqtt_server());
			MQTT_PORT.setText(connect.getMqtt_port());
			MQTT_USERNAME.setText(connect.getMqtt_username());
			MQTT_PASSWORD.setText(String.valueOf(connect.getMqtt_password()));


		}

	}

}
