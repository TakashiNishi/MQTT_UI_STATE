

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Description  extends JFrame{
	Description(String title,String description){
		setTitle(title);

		ImageIcon icon = new ImageIcon("./img/MQTT.png");
		setIconImage(icon.getImage());

		setLayout(new BorderLayout());
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));


		JTextArea Description_print = new JTextArea(description);
		Description_print.setPreferredSize(new Dimension(200, 200));
		Description_print.setLineWrap(true);
		Description_print.setWrapStyleWord(true);
		Description_print.setEditable(false);
		p.add(Description_print);

		main.add(p);
		add("North", main);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setBounds(400, 50, 215, 200);
		setResizable(false);
	}

	public void close(){
		dispose();
	}

}
