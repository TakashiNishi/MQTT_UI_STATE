


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


//publishするセンター、内容に関するUIクラス
public class Next extends JFrame implements ActionListener {

	ArrayList<JTextField> PubInput = new ArrayList<JTextField>();//pubのUIにおいて入力するフォーム
	ArrayList<JLabel> PubLabel = new ArrayList<JLabel>();//PubInputのラベル

	//log,pos,att,insは同じ番号で1セットとなる。本クラスではボード関係のコードが入っている
	ArrayList<String> PubLog = new ArrayList<String>();//Arduinoファイルの一行分のコード内容
	ArrayList<String> PubPos = new ArrayList<String>();//logを挿入する位置①。MakeCodeでコードを挿入する時に使う
	ArrayList<String> PubVal = new ArrayList<String>();//logの属性。属性を使う事で効率的な仕分けが可能になる。
	ArrayList<String> PubIns = new ArrayList<String>();//logを挿入する位置②,ここでifと中身のどちらに挿入するかを判別する。

	Description description;//説明用のUIクラス

	int row = 0;//行番号,UIの縦の長さを出力する時に使用
	int log = 0;//列番号
	Topic topic;//トピック
	int select;

	Next(Topic topic, String Pub,int select) {
		this.topic = topic;
		ImageIcon icon = new ImageIcon("./img/MQTT.png");//アイコン設定
		setIconImage(icon.getImage());

		setLayout(new BorderLayout());//UI表示
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.select = select;


		//以下txtファイルを解析し、それに応じてUIが変化する
		try {
			File file = new File("./ins/Next_" + Pub + ".txt");//insフォルダのPub_ファイルを指定
			FileReader filereader = new FileReader(file);
			int ch;//chは一文字分をintにしたもの、ASCIIコード参照
			int m = 0;//列番号、改行するごとに0になる
			int JL = 0;//JTextLabelの数
			int JTF = 0;//JTextFieldの数

			String temp = "";//tempは文字chを足し合わせるもの
			String command = "";//tempによって完成した単語であり、各行の一列目の内容を示す
			while ((ch = filereader.read()) != -1) {
				if (ch == 10) {//chが改行文字なら

					if (command.equals("Input")) {//もし行の一列目がInputなら

						//UIに入力フォームを挿入する
						PubLabel.get(JL).setPreferredSize(new Dimension(100, 20));
						PubInput.get(JTF).setPreferredSize(new Dimension(100, 20));
						p.add(PubLabel.get(JL));
						p.add(PubInput.get(JTF));
						main.add(p);
						p = new JPanel();
						p.setLayout(new FlowLayout(FlowLayout.LEFT));
						JL = JL + 1;
						JTF = JTF + 1;
						row = row + 1;
					}
					m = 0;

				} else if (ch == 58) {//chがコロンなら・・・
					if (m == 0) {//m=0ならtempで繋ぎ合わせた文字をcommandに代入
						command = temp;
					}

					if (command.equals("Input")) {//もしcommandがInput
						if (m == 0) {//一列目であれば入力フォームをadd
							PubInput.add(new JTextField());

						} else if (m == 1) {//二列目であればtempの部分をラベルにして挿入
							PubLabel.add(new JLabel(temp));
							PubVal.add(temp);
						}
					}

					if (command.equals("Log")) {//もしcommandがLog

						if (m == 1) {//二列目であればLogを追加する
							PubLog.add(temp);
							log = log + 1;

						} else if (m == 2) {//三列目であればPosを追加する
							PubPos.add(temp);

						} else if(m==3){//四列目であればInsを追加する
							PubIns.add(temp);
						}

					}
					if (command.equals("Description")) {//もしcommandがDescriptionであれば
						if (m == 1) {//説明用のUIウィンドウを出す
							description = new Description(Pub,temp);
						}
					}
					temp = "";

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

		//OKボタンとUIのコード
		JButton config_change = new JButton("OK");
		config_change.addActionListener(this);
		p.add(config_change);
		main.add(p);
		add("North", main);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setBounds(40, 40, 250, row * 50 + 50);

	}

	//ウィンドウを閉じる関数
	@Override
	public void dispose() {
	    super.dispose();
	    description.dispose();
	}

	//OKボタンを押したときに呼ばれる
	public void actionPerformed(ActionEvent e) {

		//topicに以前入力したPubやSubのLog情報があった場合、取り除く
		topic.removeLogAtt("Pub");
		topic.removeLogAtt("Sub");

		//Log,Pos,Att,の内容をTopicの配列に移す
		for (int i = 0; i < log; i++) {
			String temp = PubLog.get(i);
			ArrayList<String> cut = new ArrayList<String>();

			boolean toggle = false;
			while (temp.indexOf("\\") != -1) {

				if (toggle) {
					if (temp.substring(0, temp.indexOf("\\")).equals("topic")) {
						cut.add("\""+topic.getTopic()+"\"");
					} else if(temp.substring(0, temp.indexOf("\\")).equals("num")){
						cut.add(String.valueOf(select));
					} else {
						for (int k = 0; k < PubVal.size(); k++) {

							if (PubVal.get(k).equals(temp.substring(0, temp.indexOf("\\")))) {
								cut.add(PubInput.get(k).getText());
							}
						}
					}
					temp = temp.substring(temp.indexOf("\\") + 1, temp.length());
					toggle = false;
				} else {
					cut.add(temp.substring(0, temp.indexOf("\\")));
					temp = temp.substring(temp.indexOf("\\") + 1, temp.length());
					toggle = true;
				}

			}

			cut.add(temp);
			String code = "";
			for (int k = 0; k < cut.size(); k++) {
				code = code + cut.get(k);
			}
			if(PubIns.get(i).isEmpty()){
				topic.addLog("		"+code, PubPos.get(i), "Pub");
			}else{
				topic.addLog("		"+code, PubPos.get(i), "Pub",PubIns.get(i));
			}
		}
		Component c = (Component) e.getSource();
		Window w = SwingUtilities.getWindowAncestor(c);
		w.dispose();
		description.close();
	}
}


