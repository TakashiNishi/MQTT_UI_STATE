

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class PubConfig extends JFrame implements ActionListener {
	static PubConfig frame;
	JComboBox<String> PIf_Combo = new JComboBox<String>();
	JComboBox<String> Pub_Combo = new JComboBox<String>();
	PIf pif;
	Pub pub;
	Topic topic;
	int select;

	@Override
	public void dispose() {
		super.dispose();
		if (pif != null) {
			pif.dispose();
		}
		if (pub != null) {
			pub.dispose();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PIf_Combo.removeActionListener(this);
		Pub_Combo.removeActionListener(this);
		String cmdName = e.getActionCommand();

		if (cmdName.equals("OK")) {
			topic.makeup("pub");
			Component c = (Component) e.getSource();
			Window w = SwingUtilities.getWindowAncestor(c);
			w.dispose();
		} else if (cmdName.equals("PIf_Combo")) {
			String temp = (String) PIf_Combo.getSelectedItem();
			if (!temp.isEmpty()) {
				pif = new PIf(topic, temp,select);
			}
		} else if (cmdName.equals("Pub_Combo")) {
			String temp = (String) Pub_Combo.getSelectedItem();
			if (!temp.isEmpty()) {
				pub = new Pub(topic, temp,select);
			}
		}

		PIf_Combo.addActionListener(this);
		Pub_Combo.addActionListener(this);
	}

	PubConfig(String title, Topic topic,int select) {
		// insファイルからPD1属性ファイルを取得する
		File file = new File("./\\ins");
		File files[] = file.listFiles();
		topic.removeLogAtt("SIf");
		topic.removeLogAtt("Sub");

		// 取得した一覧を表示する
		PIf_Combo.addItem("");
		Pub_Combo.addItem("");
		for (int i = 0; i < files.length; i++) {
			String filename = files[i].getName();
			if (filename.substring(0, 3).equals("PIf")) {
				PIf_Combo.addItem(filename.substring(4, filename.length() - 4));
			} else if (filename.substring(0, 3).equals("Pub")) {
				Pub_Combo.addItem(filename.substring(4, filename.length() - 4));
			}
		}

		this.topic = topic;
		ImageIcon icon = new ImageIcon("./img/MQTT.png");
		setIconImage(icon.getImage());
		JButton config_change = new JButton("OK");

		config_change.addActionListener(this);
		PIf_Combo.addActionListener(this);
		Pub_Combo.addActionListener(this);

		PIf_Combo.setActionCommand("PIf_Combo");
		Pub_Combo.setActionCommand("Pub_Combo");
		PIf_Combo.setPreferredSize(new Dimension(100, 20));
		Pub_Combo.setPreferredSize(new Dimension(100, 20));

		setLayout(new BorderLayout());
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel PIf_print = new JLabel("If");

		PIf_print.setPreferredSize(new Dimension(100, 20));
		p.add(PIf_print);
		p.add(PIf_Combo);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel Pub_print = new JLabel("Publisher");
		Pub_print.setPreferredSize(new Dimension(100, 20));
		p.add(Pub_print);
		p.add(Pub_Combo);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.add(config_change);
		main.add(p);
		add("North", main);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setBounds(40, 40, 250, 200);
		setResizable(false);
	}

}
