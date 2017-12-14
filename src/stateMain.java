

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class stateMain extends JFrame implements ActionListener {

		// UIのPubSub設定のテキストエリア
		JComboBox<String> pubsub_combo;
		JTextField topic_field;
		JTextField name;
		JButton config;
		String state;
		int num;
		Topic topic;

		static stateMain frame;//UIに関するクラス
		static mqtt_ui frame_mqtt;
		stateMain(int i){
			state = "state"+i;
			topic = new Topic();
			topic.setPubsub("pub");
			topic.setQoS(0);
			topic.setTopic("hoge!");
			topic.setValue("value");
		}


		// メインUIを描写する関数
		void review(int num,mqtt_ui frame_mqtt) {
			this.num=num;
			this.frame_mqtt= frame_mqtt;
			setVisible(true);//UIを可視化
			setResizable(false);//UIのサイズ変更不可

			//UIの初期設定
			setBounds(30, 30, 700, 100);//UIのサイズ指定
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//画面の×ボタン押したときに綴じるように設定
			setTitle("Main"+String.valueOf(num));//UIのタイトル指定
			ImageIcon icon = new ImageIcon("./img/MQTT.png");//UIのアイコンURL指定
			setIconImage(icon.getImage());//アイコンをUIに張り付ける



			//以下、UIの描写に関する内容。一行空きはUIにおける改行部分
			setLayout(new BorderLayout());

			JPanel main = new JPanel();
			main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));



			p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel name_print = new JLabel("name of state");
			name_print.setPreferredSize(new Dimension(220, 20));
			JLabel pubsub_print = new JLabel("Pub/Sub");
			pubsub_print.setPreferredSize(new Dimension(145, 20));
			JLabel Topic_print = new JLabel("Topic");
			Topic_print.setPreferredSize(new Dimension(135, 20));
			p.add(name_print);
			p.add(pubsub_print);
			p.add(Topic_print);
			main.add(p);


			p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));

			name= new JTextField(state, 20);
			p.add(name);

			pubsub_combo=new JComboBox<String>();
			pubsub_combo.addItem("Pub");
			pubsub_combo.addItem("Sub");
			p.add(pubsub_combo);


			topic_field= new JTextField("", 20);
			p.add(topic_field);

			JButton config = new JButton("Config");
			config.addActionListener(this);
			p.add(config);

			JButton finish = new JButton("Finish");
			finish.addActionListener(this);
			p.add(finish);

			main.add(p);



			add("North", main);

			topic=new Topic();

		}

		// ボタンを押したときに呼ばれる関数
		public void actionPerformed(ActionEvent e) {
			String cn = e.getActionCommand();//コマンド名の取得
			topic.setTopic(topic_field.getText());
			if(cn.equals("Config")){
				String ps = (String) pubsub_combo.getSelectedItem();
				if(ps.equals("Pub")){
					new PubConfig("PubConfig", topic,num);//PubConfigのUIクラスを呼び出す
				}else{
					new SubConfig("SubConfig", topic,num);//PubConfigのUIクラスを呼び出す
				}
			}else if(cn.equals("Finish")){


				frame_mqtt.mainLabel.get(num).setText(name.getText());;
				Component c = (Component) e.getSource();
				Window w = SwingUtilities.getWindowAncestor(c);
				w.dispose();
			}


		}
	}
