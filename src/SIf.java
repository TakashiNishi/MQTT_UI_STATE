

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

public class SIf extends JFrame implements ActionListener {
	// insファイルのうちから探し出し，解析する
	//Pubクラスと内容がほぼ同じ
	ArrayList<JTextField> SIfInput = new ArrayList<JTextField>();
	ArrayList<JLabel> SIfLabel = new ArrayList<JLabel>();
	ArrayList<String> SIfLog = new ArrayList<String>();
	ArrayList<String> SIfPos = new ArrayList<String>();
	ArrayList<String> SIfVal = new ArrayList<String>();
	ArrayList<String> SIfIns = new ArrayList<String>();
	Description description;
	int row = 0;
	int log = 0;
	Topic topic;
	int select;
	SIf(Topic topic, String SIf,int select) {
		this.topic = topic;
		ImageIcon icon = new ImageIcon("./img/MQTT.png");
		setIconImage(icon.getImage());
		setLayout(new BorderLayout());
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.select = select;

		try {
			File file = new File("./ins/SIf_" + SIf + ".txt");
			FileReader filereader = new FileReader(file);
			int ch;
			int m = 0;
			int JL = 0;
			int JTF = 0;

			String temp = "";
			String command = "";
			while ((ch = filereader.read()) != -1) {
				if (ch == 10) {
					if (command.equals("Input")) {
						SIfLabel.get(JL).setPreferredSize(new Dimension(100, 20));
						SIfInput.get(JTF).setPreferredSize(new Dimension(100, 20));
						p.add(SIfLabel.get(JL));
						p.add(SIfInput.get(JTF));
						main.add(p);
						p = new JPanel();
						p.setLayout(new FlowLayout(FlowLayout.LEFT));
						JL = JL + 1;
						JTF = JTF + 1;
						row = row + 1;
					}
					m = 0;
				} else if (ch == 58) {
					if (m == 0) {
						command = temp;
					}

					if (command.equals("Input")) {
						if (m == 0) {
							SIfInput.add(new JTextField());
						} else if (m == 1) {
							SIfLabel.add(new JLabel(temp));
							SIfVal.add(temp);
						}

					}
					if (command.equals("Log")) {
						if (m == 1) {
							SIfLog.add(temp);
							log = log + 1;
						} else if (m == 2) {
							SIfPos.add(temp);
						} else if (m==3){
							SIfIns.add(temp);
						}
					}
					if (command.equals("Description")) {
						if (m == 1) {
							description = new Description(SIf,temp);
						}
					}

					temp = "";

					m = m + 1;
				} else {
					if (ch != 13) {
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
	@Override
	public void dispose() {
	    super.dispose();
	    description.dispose();
	}
	public void actionPerformed(ActionEvent e) {
		topic.removeLogAtt("SIf");
		topic.removeLogAtt("PIf");
		topic.addLog("	if(state=="+select+"){", "sub", "SIf", "start");
		for (int i = 0; i < log; i++) {
			String temp = SIfLog.get(i);
			ArrayList<String> cut = new ArrayList<String>();

			boolean toggle = false;
			while (temp.indexOf("\\") != -1) {

				if (toggle) {
					if (temp.substring(0, temp.indexOf("\\")).equals("topic")) {
						cut.add(topic.getTopic());
					} else if(temp.substring(0, temp.indexOf("\\")).equals("num")){
						cut.add(String.valueOf(select));
					} else {
						for (int k = 0; k < SIfVal.size(); k++) {

							if (SIfVal.get(k).equals(temp.substring(0, temp.indexOf("\\")))) {
								cut.add(SIfInput.get(k).getText());
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
			if(SIfIns.get(i).isEmpty()){
				topic.addLog("	"+code, SIfPos.get(i), "SIf");
			}else{
				topic.addLog("	"+code, SIfPos.get(i), "SIf",SIfIns.get(i));
			}
		}
		topic.addLog("		state++;", "sub", "SIf", "end");
		topic.addLog("	}", "sub", "SIf", "end");
		Component c = (Component) e.getSource();
		Window w = SwingUtilities.getWindowAncestor(c);
		w.dispose();
		description.close();

	}
}
