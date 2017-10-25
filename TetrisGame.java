package ag3;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.AbstractAction;


class TetrisGame extends Canvas{
	TetrisGui gui = new TetrisGui(this);
	int Lines = 0;
	int Score = 0;
	int Level = 1;
	float FS = 1;
	int LinesOfLevel = 0;
	int DelayFactor = 300;
	public static int M = 1;
	public static int N = 1;
	public static float S = 0.1f;
	boolean showPause = false;
	boolean openGui = false;
	Color pauseColor = new Color(66, 203, 244);
	int maxX, maxY;
	float pixelWidth, pixelHeight, rWidth = 100.0F, rHeight = 100.0F, xP = -1, yP;
	float pixelSize;
	float singleSquareWidth = 5.0F;	
	float mainAWidth = 50.0F;
	float mainAHeight = 100.0F;
	float mainALeftPosition = 0F;
	float mainABottomPosition = 0F;	
	float nextShapeAWidth = 40.0F;
	float nextShapeAHeight = 30.0F;
	float nextShapeALeftPosition = 55.0F;
	float nextShapeABottomPosition = 65.0F;	
	float quitAHeight = 8.0F;
	float quitAWidth = 20.0F;
	float quitALeftPosition = 55.0F;
	float quitABottomPosition = 0F;	
	float setupAHeight = 8.0F;
	float setupAWidth = 20.0F;
	float setupALeftPosition = 55.0F;
	float setupABottomPosition = 9F;	
	float pauseAHeight = 8.0F;
	float pauseAWidth = 20.0F;
	float pauseALeftPosition = 15.0F;
	float pauseABottomPosition = 46.0F;	
	
	boolean[] shapePermitted = {true, true, true, true, true, true, true, false, false, false};
	
	TetrisShape fallingTetrisblok;   
	TetrisShape nextTetrisblok;       
	
	int mainHeight = (int)(mainAHeight / singleSquareWidth);
	int mainWidth = (int)(mainAWidth / singleSquareWidth);
	
	int fallingX;  
	int fallingY;  
	int[][] map = new int[mainHeight][mainWidth];  
	Color[][] colorMap = new Color[mainHeight][mainWidth];  
	
	Timer timer = new Timer(DelayFactor, new TimerListener());  
	int flag = 0;
	
	
	
	public void newParameters(String whatHappened) {
		if (whatHappened.equals("deleteOneRow")) {
			Lines++;
			LinesOfLevel++;
			Score += Level * M;
			if(LinesOfLevel >= N) {
				Level++;
				FS = FS * (1 + Level * S);
				timer.setDelay((int)(DelayFactor/FS));
				LinesOfLevel = 0;
			}
		} else if(whatHappened.equals("cursorInsideFallingShape")) {
			Score = Score - Level * M;
		}
		drawParameters(this.getGraphics());
	}
	
	
	public void newPausePosition() {
		pauseALeftPosition = mainAWidth / 2 - pauseAWidth / 2;
		if (pauseALeftPosition < 0) {
			pauseALeftPosition = 0;
		}
		pauseABottomPosition = mainAHeight / 2 - pauseAHeight / 2;
		if (pauseABottomPosition < 0) {
			pauseABottomPosition = 0;
		}
	}
	
	public void newStart() {
		map = new int[mainHeight][mainWidth]; 
		colorMap = new Color[mainHeight][mainWidth];
		Lines = 0;
		Score = 0;
		Level = 1;
		FS = 1;
		LinesOfLevel = 0;
		initGame();
	}
	
	public void gameOver() {
		for(int i = 0; i < mainWidth; i++) {
			if(map[mainHeight-1][i] == 1) {
				System.exit(0);
			}
		}
	}

	TetrisGame() {
		timer.setDelay((int)(DelayFactor/FS));
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (openGui) {
					return;
				}
				float x = fx(evt.getX());
				float y = fy(evt.getY());
				if (x >= quitALeftPosition && x <= quitALeftPosition + quitAWidth &&
						y >= quitABottomPosition && y <= quitABottomPosition + quitAHeight) {
					System.exit(0);
				}
				if (x >= setupALeftPosition && x <= setupALeftPosition + setupAWidth &&
						y >= setupABottomPosition && y <= setupABottomPosition + setupAHeight) {
					gui.go();
					openGui = true;
					showPause = true;
					timer.stop();
					repaint(); 
					
				} else if(SwingUtilities.isRightMouseButton(evt) && !showPause && !openGui) {
					right();
				} else if(SwingUtilities.isLeftMouseButton(evt) && !showPause && !openGui) {
					left();
				}
			}
		});
		
		addMouseWheelListener(new MouseAdapter(){
			 public void mouseWheelMoved(MouseWheelEvent e) {
				 int notches = e.getWheelRotation();
			     
			     if(notches > 0 && !showPause && !openGui) {
			    	 turnCounterClockwise();
			     }
			     if(notches < 0 && !showPause && !openGui) {
			    	 turnClockwise();
			     }
			 }
		});
		
		addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (openGui) {
					return;
				}
				float x = fx(e.getX());
				float y = fy(e.getY());
				if(x >= mainALeftPosition && x <= mainALeftPosition + mainAWidth &&
						y >= mainABottomPosition && y <= mainABottomPosition + mainAHeight) {
					
					if(showPause == false) {
						showPause = true;
						timer.stop();
						repaint();          
					} else {               		
						if(CursorInside(x, y)) {
							nextBlock();
							newParameters("cursorInsideFallingShape");
							repaint();
						}
					}
				} 
				
				else {
					if(showPause == true) {
						showPause = false;
						timer.start();
						repaint();
					}
				}
			}
		});
		
		initGame();
	}
	
	public boolean CursorInside(float x, float y) {
		for (int a = 0; a < 4; a++) {
			for (int b = 0; b < 4; b++) {
				if(fallingTetrisblok.shapes[fallingTetrisblok.theType][fallingTetrisblok.turnState][a * 4 + b] == 0) {
					continue;
				}
				if (x >= (fallingX + b) * singleSquareWidth && x <= (fallingX + b + 1) * singleSquareWidth && 
						y >= (fallingY + a) * singleSquareWidth && y <= (fallingY + a + 1) * singleSquareWidth) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	void drawAllRect(Graphics g) {
		drawRectImp(g, iX(mainALeftPosition), iY(mainABottomPosition), iXLength(mainAWidth), iYLength(mainAHeight));
		drawRectImp(g, iX(nextShapeALeftPosition), iY(nextShapeABottomPosition), iXLength(nextShapeAWidth), iYLength(nextShapeAHeight));
		drawRectImp(g, iX(quitALeftPosition), iY(quitABottomPosition), iXLength(quitAWidth), iYLength(quitAHeight));
		drawRectImp(g, iX(setupALeftPosition), iY(setupABottomPosition), iXLength(setupAWidth), iYLength(setupAHeight));
		if(showPause) {
			Color c = pauseColor;
			g.setColor(c);
			drawRectImp(g, iX(pauseALeftPosition), iY(pauseABottomPosition), iXLength(pauseAWidth), iYLength(pauseAHeight));
		}
	}
	
	void drawfallingTetrisblock(Graphics g) {
		drawTetrisblock(g, fallingX * singleSquareWidth, fallingY * singleSquareWidth, fallingTetrisblok);
	}
	
	
	void drawNextTetrisblock(Graphics g) {
		drawTetrisblock(g, 68, 75, nextTetrisblok);
	}
	
	
	void drawTetrisblock(Graphics g, float x, float y, TetrisShape tetrisblok) {
		float xP, yP;
		Color color = tetrisblok.blokColors[tetrisblok.theType];
		for(int i = 0; i <= 15; i++) {
				if (tetrisblok.shapes[tetrisblok.theType][tetrisblok.turnState][i] == 1) {
					int v = i / 4;
					int h = i % 4;
					xP = x + h * singleSquareWidth;
				    yP = y + v * singleSquareWidth;
					drawSingleSquare(g, xP, yP, color);
				}
		}
	}
	
	
	void drawSingleSquare(Graphics g, float x, float y, Color color) {
		g.setColor(color);
		fillRectImp(g, iX(x), iY(y), iXLength(singleSquareWidth), iYLength(singleSquareWidth));
		
		g.setColor(Color.black);
		drawRectImp(g, iX(x), iY(y), iXLength(singleSquareWidth), iYLength(singleSquareWidth));
	}
	
	void drawRectImp(Graphics g, int leftP, int bottomP, int width, int height) {
		g.drawRect(leftP, bottomP - height, width, height);
	}
	
	void fillRectImp(Graphics g, int leftP, int bottomP, int width, int height) {
		g.fillRect(leftP, bottomP - height, width, height);
	}

	void initgr() {
		Dimension d = getSize();
		maxX = d.width - 1;
		maxY = d.height - 1;
		
		pixelWidth = rWidth / maxX;
		pixelHeight = rHeight / maxY;
		
		pixelSize = Math.max(pixelWidth, pixelHeight);
		pixelWidth = pixelSize;
		pixelHeight = pixelSize;
	}
	
	void initGame() {
		newblock();
		timer.start();
	}
	
	int iXLength(float l) {
		return iX(l);
	}
	
	int iYLength(float l) {
		return Math.round(l / pixelHeight);
	}

	int iX(float x) {
		return Math.round(x / pixelWidth);
	}

	int iY(float y) {
		return maxY - Math.round(y / pixelHeight);
	}

	float fx(int x) {
		return x * pixelWidth;
	}

	float fy(int y) {
		return (maxY - y) * pixelHeight;
	}
	
	public void drawAllStrings(Graphics g) {
		
		int font = Math.round(1 / pixelSize * 5);
		Font f = new Font("TimesRoman", Font.BOLD, font);
		g.setFont(f);
		
		int x = iX(1.5F + pauseALeftPosition);        
		int y = iY(2.0F + pauseABottomPosition);        
		g.setColor(pauseColor);
		if(showPause) {
		    g.drawString("PAUSE", x, y);
		}
		
		g.setColor(Color.BLACK);
		x = iX(59.5F);
		y = iY(2.0F);
		g.drawString("QUIT", x, y);
	
		x = iX(59.5F);
		y = iY(11.0F);
		g.drawString("SET", x, y);
		
		
		drawParameters(g);
	}
	
	public void drawParameters(Graphics g) {
		g.setColor(Color.black);
		int x = iX(quitALeftPosition);
		int y = iY(55.0F);
		g.drawString("Level:      " + Level, x, y);
		
		y = iY(45.0F);
		g.drawString("Lines:      " + Lines, x, y);
		
		y = iY(35.0F);
		g.drawString("Score:      " + Score, x, y);
	}
	
	public void drawMap(Graphics g) {
		for(int i = 0; i < mainHeight; i++) {
			for(int j = 0; j < mainWidth; j++) {
				if(map[i][j] == 1) {
				    float x = j * singleSquareWidth;
				    float y = i * singleSquareWidth;
				    drawSingleSquare(g, x, y, colorMap[i][j]); 
				}
			}
		}
	}

	public void paint(Graphics g) {
		initgr();
		drawAllRect(g);
		drawfallingTetrisblock(g);
		drawNextTetrisblock(g);
		drawMap(g);
		drawAllStrings(g);
	}
	
	public void delline() {
		int c = 0;
		for (int b = 0; b < mainHeight; b++) {
			for (int a = 0; a < mainWidth; a++) {
				if (map[b][a] == 1) {

					c = c + 1;
					if (c == mainWidth) {
				
						for (int d = b; d < mainHeight - 1; d++) {
							for (int e = 0; e < mainWidth; e++) {
								map[d][e] = map[d+1][e];
								colorMap[d][e] = colorMap[d+1][e];
							}
						}
						newParameters("deleteOneRow");
					}
				}
			}
			c = 0;
		}
	}
	
	public void turnClockwise() {
		int tempturnState = fallingTetrisblok.turnState;
		fallingTetrisblok.turnState = (fallingTetrisblok.turnState + 3) % 4;
		int blow = blow(fallingX, fallingY, fallingTetrisblok.theType, fallingTetrisblok.turnState);
		if (blow == 0) {
			fallingTetrisblok.turnState = tempturnState;
		} else {
			repaint();
		}
	}
	
	
	public void left() {
		if (blow(fallingX - 1, fallingY, fallingTetrisblok.theType, fallingTetrisblok.turnState) == 1) {
			fallingX--;
		}
		repaint();
	}


	public void right() {
		if (blow(fallingX + 1, fallingY, fallingTetrisblok.theType, fallingTetrisblok.turnState) == 1) {
			fallingX++;
		}
		repaint();
	}
	
	
		public void turnCounterClockwise() {
			int tempturnState = fallingTetrisblok.turnState;
			fallingTetrisblok.turnState = (fallingTetrisblok.turnState + 1) % 4;
			int blow = blow(fallingX, fallingY, fallingTetrisblok.theType, fallingTetrisblok.turnState);
			if (blow == 0) {
				fallingTetrisblok.turnState = tempturnState;
			} else {
				repaint();
			}
		}
	
	
	

	
	public void newblock() {
		int r = (int)(Math.random() * 7);
		fallingTetrisblok = new TetrisShape(r, 0);
		fallingX = (int)(mainWidth / 2 - 2);
		fallingY = mainHeight - 2;
		getDifferentNextBlock();
		
	}
	
	public void getDifferentNextBlock() {
		int r = (int)(Math.random() * 10);
		while(r == fallingTetrisblok.theType || !shapePermitted[r]) {
			r = (int)(Math.random() * 10);
		}
		nextTetrisblok = new TetrisShape(r, 0);
	}
	
	public void nextBlock() {
		fallingTetrisblok = nextTetrisblok;
		fallingX = (int)(mainWidth / 2 - 2);
		fallingY = mainHeight - 2;
		getDifferentNextBlock();
	}
	
	
	public int blow(int x, int y, int theType, int turnState) {
		for (int a = 0; a < 4; a++) {
			for (int b = 0; b < 4; b++) {
				if(fallingTetrisblok.shapes[theType][turnState][a * 4 + b] == 0) {
					continue;
				}
				if (( y + a < 0 || y + a >= mainHeight || x + b >= mainWidth || x + b < 0|| (map[y + a ][x + b] == 1))) {
					return 0;
				}
			}
		}
		return 1;
	}
	
	
	public void add(int x, int y) {
		for (int a = 0; a < 4; a++) {
			for (int b = 0; b < 4; b++) {
				if(y + a < 0 || y + a >= mainHeight || x + b < 0 || x + b >= mainWidth) {
					continue;
				}
				
				if (map[y + a ][x + b] == 0) {
					map[y + a ][x + b] = fallingTetrisblok.shapes[fallingTetrisblok.theType][fallingTetrisblok.turnState][ 4 * a + b];
					colorMap[y + a][x + b] = fallingTetrisblok.blokColors[fallingTetrisblok.theType];
				}
			}
		}
	}
	
	class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			repaint();
			if (blow( fallingX, fallingY - 1, fallingTetrisblok.theType, fallingTetrisblok.turnState) == 1) {
				fallingY = fallingY - 1;
				delline();
			}
			if (blow( fallingX, fallingY - 1 , fallingTetrisblok.theType, fallingTetrisblok.turnState) == 0) {

				if (flag == 1) {
					add(fallingX, fallingY);
					delline();
					gameOver();
					nextBlock();
					flag = 0;
				}
				flag = 1;
			}
			;
		}
	}
	

}