

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class SubConfig extends JFrame implements ActionListener {
	static SubConfig frame;
	JComboBox<String> SIf_Combo = new JComboBox<String>();
	JComboBox<String> Sub_Combo = new JComboBox<String>();
	SIf sif;
	Sub sub;
	Topic topic;
	int select;

	@Override
	public void dispose() {
		super.dispose();
		if (sif != null) {
			sif.dispose();
		}
		if (sub != null) {
			sub.dispose();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		SIf_Combo.removeActionListener(this);
		Sub_Combo.removeActionListener(this);
		String cmdName = e.getActionCommand();

		if (cmdName.equals("OK")) {
			if(((String)SIf_Combo.getSelectedItem()).isEmpty()||((String)Sub_Combo.getSelectedItem()).isEmpty()){
				JOptionPane.showMessageDialog(this, "SIfとSubを選択してください。");
			}else{
				topic.removeLogAtt("sub");
				topic.makeup("sub");
				topic.addLog("  while (!client.subscribe(\""+topic.getTopic()+"\")) {", "setup2", "sub");
				topic.addLog("}", "setup2", "sub");

				topic.addLog("  if(state=="+select+") {", "pub", "sub");
				topic.addLog("  while (!client.subscribe(\""+topic.getTopic()+"\")) {", "pub", "sub");
				topic.addLog("	}", "pub", "sub");
				topic.addLog("}", "pub", "sub");

				Component c = (Component) e.getSource();
				Window w = SwingUtilities.getWindowAncestor(c);
				w.dispose();
			}
		} else if (cmdName.equals("SIf_Combo")) {
			String temp = (String)SIf_Combo.getSelectedItem();
			if(!temp.isEmpty()){
				sif=new SIf(topic, temp,select);
			}
		} else if (cmdName.equals("Sub_Combo")) {
			String temp = (String)Sub_Combo.getSelectedItem();
			if(!temp.isEmpty()){
				sub=new Sub(topic, temp,select);
			}
		}

		SIf_Combo.addActionListener(this);
		Sub_Combo.addActionListener(this);
	}

	SubConfig(String title, Topic topic,int select) {
		this.select = select;
		// insファイルからPD1属性ファイルを取得する
		File file = new File("./\\ins");
		File files[] = file.listFiles();
		topic.removeLogAtt("PIf");
		topic.removeLogAtt("Pub");

		// 取得した一覧を表示する
		SIf_Combo.addItem("");
		Sub_Combo.addItem("");
		for (int i = 0; i < files.length; i++) {
			String filename = files[i].getName();
			if (filename.substring(0, 3).equals("SIf")) {
				SIf_Combo.addItem(filename.substring(4, filename.length() - 4));
			}else if(filename.substring(0, 3).equals("Sub")) {
				Sub_Combo.addItem(filename.substring(4, filename.length() - 4));
			}
		}

		this.topic = topic;
		ImageIcon icon = new ImageIcon("./img/MQTT.png");
		setIconImage(icon.getImage());
		JButton config_change = new JButton("OK");

		config_change.addActionListener(this);
		SIf_Combo.addActionListener(this);
		Sub_Combo.addActionListener(this);

		SIf_Combo.setActionCommand("SIf_Combo");
		Sub_Combo.setActionCommand("Sub_Combo");
		SIf_Combo.setPreferredSize(new Dimension(100, 20));
		Sub_Combo.setPreferredSize(new Dimension(100, 20));

		setLayout(new BorderLayout());
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));


		JLabel SIf_print = new JLabel("If");

		SIf_print.setPreferredSize(new Dimension(100, 20));
		p.add(SIf_print);
		p.add(SIf_Combo);
		main.add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel Sub_print = new JLabel("Subscriber");
		Sub_print.setPreferredSize(new Dimension(100, 20));
		p.add(Sub_print);
		p.add(Sub_Combo);
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
