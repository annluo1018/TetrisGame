package ag3;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.*;

public class TetrisGui {

	TetrisGame Cv;

	public TetrisGui(TetrisGame Cv) {
		this.Cv = Cv;
	}

	static String content = "";

	public boolean MainAreaSizeChanged(int h, int w) {
		if (h == Cv.mainHeight && w == Cv.mainWidth) {
			return false;
		}
		return true;
	}

	public void changeMainAreaSize(int h, int w) {
		float mainAWidth, mainAHeight, singleSquareWidth;
		if (!MainAreaSizeChanged(h, w)) {
			return;
		}

		if (h <= 4 || w <= 4) {
			return;
		}
		float ratio = h / w;
		if (ratio > 2) {
			mainAHeight = 100F;
			mainAWidth = mainAHeight / h * w;
		} else {
			mainAWidth = 50F;
			mainAHeight = mainAWidth / w * h;
		}
		singleSquareWidth = mainAHeight / h;

		Cv.mainAHeight = mainAHeight;
		Cv.mainAWidth = mainAWidth;
		Cv.singleSquareWidth = singleSquareWidth;
		Cv.mainHeight = h;
		Cv.mainWidth = w;
		Cv.newPausePosition();
		Cv.newStart();

	}

	public void go() {
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout(6, 1));
		JPanel firstPanel = new JPanel();
		frame.add(firstPanel);
		JPanel secondPanel = new JPanel();
		frame.add(secondPanel);		
		JPanel thirdPanel = new JPanel();
		frame.add(thirdPanel);		
		JLabel headerLabel = new JLabel("New Shapes:", JLabel.CENTER);
		frame.add(headerLabel);		
		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new GridBagLayout());
		imagePanel.add(new JLabel(new ImageIcon("src/ag3/1.png")));
		imagePanel.add(new JLabel(new ImageIcon("src/ag3/2.png")));
		imagePanel.add(new JLabel(new ImageIcon("src/ag3/3.png")));
		frame.add(imagePanel);
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridBagLayout());
		frame.add(checkBoxPanel);
		JCheckBox chk1 = new JCheckBox("1      ");
		JCheckBox chk2 = new JCheckBox("2  ");
		JCheckBox chk3 = new JCheckBox("3");
		checkBoxPanel.add(chk1);
		checkBoxPanel.add(chk2);
		checkBoxPanel.add(chk3);

		chk1.setMnemonic(KeyEvent.VK_C);
		chk2.setMnemonic(KeyEvent.VK_M);
		chk3.setMnemonic(KeyEvent.VK_P);

		chk1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				Cv.shapePermitted[7] = e.getStateChange() == 1;

			}
		});
		chk2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				Cv.shapePermitted[8] = e.getStateChange() == 1;
			}

		});
		chk3.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				Cv.shapePermitted[9] = e.getStateChange() == 1;
			}

		});

		JLabel m1 = new JLabel("M:");
		JTextArea m2 = new JTextArea(String.valueOf(TetrisGame.M), 1, 2);
		firstPanel.add(m1);
		firstPanel.add(m2);

		JLabel n1 = new JLabel("N:");
		JTextArea n2 = new JTextArea(String.valueOf(TetrisGame.N), 1, 2);
		firstPanel.add(n1);
		firstPanel.add(n2);

		JLabel s1 = new JLabel("S:");
		JTextArea s2 = new JTextArea(String.valueOf(TetrisGame.S), 1, 2);
		firstPanel.add(s1);
		firstPanel.add(s2);
		
		JLabel w1 = new JLabel("Main area width:");
		JTextArea w2 = new JTextArea(String.valueOf(Cv.mainWidth), 1, 2);
		secondPanel.add(w1);
		secondPanel.add(w2);

		JLabel h1 = new JLabel("Main area height:");
		JTextArea h2 = new JTextArea(String.valueOf(Cv.mainHeight), 1, 2);
		thirdPanel.add(h1);
		thirdPanel.add(h2);

		frame.setSize(500, 400);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String m = m2.getText();
				String n = n2.getText();
				String s = s2.getText();
				String h = h2.getText();
				String w = w2.getText();

				TetrisGame.M = Integer.parseInt(m);
				TetrisGame.N = Integer.parseInt(n);
				TetrisGame.S = Float.parseFloat(s);

				Cv.openGui = false;

				if (!Cv.showPause) {
					Cv.timer.start();
					Cv.repaint();
				}

				changeMainAreaSize(Integer.parseInt(h), Integer.parseInt(w));
			}
		});

		frame.setVisible(true);
	}
}
