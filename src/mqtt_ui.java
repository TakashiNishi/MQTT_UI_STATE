

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class mqtt_ui extends JFrame implements ActionListener {

	// state数
	static int num_of_state = 1;

	// UIのボード設定
	JComboBox<String> mqtt_frame = new JComboBox<String>();// Arduinoのボード選択
	JTextField COMPORT;//COMポートを入力、ArduinoのIDEで確認

	// stateの設定
	ArrayList<stateMain> state = new ArrayList<stateMain>();//MSの生成
	ArrayList<JLabel> mainLabel= new ArrayList<JLabel>();//MSの名前
	ArrayList<JButton> mainConfig = new ArrayList<JButton>();//MSの設定をするボタン
	ArrayList<JButton> mainAdd = new ArrayList<JButton>();//MSを追加するボタン
	ArrayList<JButton> mainDelete = new ArrayList<JButton>();//MSを削除するボタン

	static statePrepare sp;

	// UI自体が持つstatic変数
	static int select;//MSを複数設定するときに使う。何番目のMSか
	static mqtt_ui frame;//UIに関するクラス

	public static void main(String[] args) {
		sp = new statePrepare();
		frame = new mqtt_ui("MQTT UI For Arduino");//UIを呼び出す
		frame.setVisible(true);//UIを可視化
		frame.setResizable(false);//UIのサイズ変更不可
	}

	// メインUIを描写する関数
	mqtt_ui(String title) {
		//UIの初期設定
		setBounds(30, 30, 700, num_of_state * 50 + 300);//UIのサイズ指定
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
		JButton button_send = new JButton("Send");
		JButton button_code = new JButton("Code");
		button_send.addActionListener(this);
		button_code.addActionListener(this);

		mqtt_frame.addActionListener(this);

		JButton button_wait = new JButton("Wait");
		JButton button_prepare = new JButton("Prepare");
		JButton button_close = new JButton("Close");
		button_wait.addActionListener(this);
		button_prepare.addActionListener(this);
		button_close.addActionListener(this);



		//以下、UIの描写に関する内容。一行空きはUIにおける改行部分
		setLayout(new BorderLayout());

		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton button_addS = new JButton("Add new State");
		button_addS.addActionListener(this);
		p.add(button_addS);
		main.add(p);


		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel prepare_print = new JLabel("Prepare");
		prepare_print.setPreferredSize(new Dimension(145, 20));
		p.add(prepare_print);
		p.add(button_prepare);
		main.add(p);

		for (int i = 0; i < num_of_state; i++) {
			state.add(new stateMain(i));
			mainLabel.add(new JLabel(state.get(i).state));
			mainLabel.get(i).setPreferredSize(new Dimension(145, 20));
			mainConfig.add(new JButton("Config"));
			mainDelete.add(new JButton("Delete"));
			mainConfig.get(i).setActionCommand("Config" + i);
			mainDelete.get(i).setActionCommand("Delete" + i);
			mainConfig.get(i).addActionListener(this);
			mainDelete.get(i).addActionListener(this);

			p= new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			p.add(mainLabel.get(i));
			p.add(mainConfig.get(i));
			p.add(mainDelete.get(i));
			main.add(p);
		}


		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		COMPORT = new JTextField(5);
		JLabel COMPORT_print = new JLabel("COM");
		p.add(COMPORT_print);
		p.add(COMPORT);
		p.add(button_send);
		p.add(button_code);
		main.add(p);

		add("North", main);



	}



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		String cn =e.getActionCommand();


		if(cn.equals("Add new State")){
			num_of_state++;
			frame.setVisible(false);
			frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			frame = new mqtt_ui("MQTT");
			frame.setVisible(true);
			frame.setResizable(false);
		}else if(cn.length()>6&&cn.contains("Delete")){
				int number = Integer.parseInt(cn.substring(6,cn.length()));
				num_of_state--;

				state.remove(number);
				mainLabel.remove(number);
				mainConfig.remove(number);
				mainDelete.remove(number);


				frame.setVisible(false);
				frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				frame = new mqtt_ui("MQTT");
				frame.setVisible(true);
				frame.setResizable(false);
		}else if(cn.length()>6&&cn.contains("Config")){
			int number = Integer.parseInt(cn.substring(6,cn.length()));
			state.get(number).review(number,frame);
		}else if(cn.equals("Prepare")){
			sp=new statePrepare("prepare");
		}
		if (cn == "Send" || cn == "Code") {//もしボタン名がSend,Codeなら

		statePrepare.connect.setup((String) sp.mqtt_frame.getSelectedItem());//ボード情報を読み取り、Arduinoコード化をする
			MakeCode code = new MakeCode(statePrepare.connect, state);//MakeCodeクラスにて、Topicで設定した部分をコード化する
			try {
				String name = System.getProperty("user.home");//ユーザーのディレクトリのパスを取得（Windows限定）
				File dir = new File(name + "\\Documents\\arduino\\mufa");//arduinoのファイルが入っているフォルダのディレクトリを指定（Windows限定）
				if (!dir.exists()) {//もしdirのフォルダが存在しなかったら
					dir.mkdir();//dirのフォルダを作成する
				}
				File file = new File(name + "\\Documents\\arduino\\mufa\\mufa.ino");//本UIで作成されたarduinoのファイルのディレクトリを指定（Windows限定）

				//本UIにて作成されるArduinoファイルの名前はmufaである
				if (file.isFile() && file.canWrite() && file.exists()) {//もし以前に本UIを用い、Arduinoファイルが作られていたら内容を新しいのに書き換える
					PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
					for (int i = 0; i < code.getCodeSize(); i++) {
						pw.println(code.getCode(i));
					}
					pw.close();
				} else {//もし初めてUIを使用するときは新しいArduinoファイルを作成、書き込む
					file.createNewFile();
					PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
					for (int i = 0; i < code.getCodeSize(); i++) {
						pw.println(code.getCode(i));
					}
					pw.close();
				}

				//以下OSのコマンドラインを用いてArduinoを起動する
				Runtime r = Runtime.getRuntime();
				if (cn == "Send") {//もしボタン名がSendなら
					//OSに以下のコマンドを送り、Arduino本体にコードを送る
					r.exec("C:\\Program Files (x86)\\Arduino\\arduino --upload --board " + sp.connect.getboard()
							+ " --port COM" + COMPORT.getText() + " " + name + "/Documents/arduino/mufa/mufa.ino");
				} else {//もしボタン名がCodeなら
					//OSに以下のコマンドを送り、コードをArduinoのIDEで開く
					r.exec("C:\\Program Files (x86)\\Arduino\\arduino " + name + "\\Documents\\arduino\\mufa\\mufa.ino");
				}
			} catch (IOException error) {
				System.out.println(e);
			}

		}
	}
}