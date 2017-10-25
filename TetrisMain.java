package ag3;

import java.awt.*;
import java.awt.event.*;

public class TetrisMain extends Frame {
	public static void main(String[] args) {
		new TetrisMain();
	}

	TetrisMain() {
		super("PLAYING TETRIS!!");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}
		});
		setSize(500, 400);
		add("Center", new TetrisGame());
		show();
	}
}