import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
public class Tetris extends JPanel implements ActionListener
{
   public static void main(String[] args) throws Exception
   {
      JFrame frame = new JFrame("Tetris");
      frame.setSize(700, 700);
      frame.setLocation(800, 0);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(new Tetris());
      frame.setVisible(true);
   }
   Timer timer = new Timer(1000, this);
   JLabel[][] board;
   Color bg = new Color(156,156,156);
   TetrisPiece current;
   int currentRow, currentCol, score = 0, highscore;
   int[][] config;
   int boardSize = 15;
   Listener l;
   boolean isFallingFinished = false, gameover = false;
   public Tetris() throws Exception
   {   
      setLayout(new BorderLayout());
      JPanel center = new JPanel();
      center.setLayout(new GridLayout(boardSize,boardSize));
      add(center, BorderLayout.CENTER); 
      JPanel bottom = new JPanel();
      bottom.setLayout(new GridLayout(1,3));
      add(bottom, BorderLayout.SOUTH);  
      board = new JLabel[boardSize][boardSize];
      config = new int[boardSize][boardSize];  
      Border border = new LineBorder(new Color(92, 92, 92), 1, true); 
      for(int r = 0; r < boardSize; r++)
         for(int c = 0; c < boardSize; c++)
         {
            board[r][c] = new JLabel();
            board[r][c].setOpaque(true);
            board[r][c].setBorder(border); 
            center.add(board[r][c]);                         
         }
      l = new Listener();
      JButton reset = new JButton("Reset");
      reset.setFocusable(false);
      reset.addActionListener( new resetHandler() );
      bottom.add(reset, BorderLayout.SOUTH);    
      newGame();
   } 
   public void actionPerformed(ActionEvent e)
   {
      if(currentRow+current.getLength() > 14)
         isFallingFinished = true;
      if(isFallingFinished) 
      {
         isFallingFinished = false;
         newPiece();
      } 
      else
      {
         if(canGoDown())
            oneLineDown();
      }
      checkLines();
   }
   public class Listener implements KeyListener
   {
      public Listener()
      {
         addKeyListener(this);
         setFocusable(true);
         setFocusTraversalKeysEnabled(false);
      }
      public void keyPressed(KeyEvent event)
      {
         if (event.getKeyCode() == KeyEvent.VK_LEFT)
            left();
         else if (event.getKeyCode() == KeyEvent.VK_DOWN)
            goDown();
         else if (event.getKeyCode() == KeyEvent.VK_RIGHT)
            right();
         else if (event.getKeyCode() == KeyEvent.VK_UP)
            rotate(); 
      }
      public void keyTyped(KeyEvent event){}
      public void keyReleased(KeyEvent event){}
   }
   public void checkLines()
   {
      boolean lineDone = true;
      int row = 0;
      for(int i = 0; i < config.length; i++)
      {
         int j = 0;
         lineDone = true;
         while(j < config[0].length && lineDone == true)
         {
            row = j;
            if(config[i][j] == 0)
               lineDone = false;
            j++;
         }
         if(lineDone)
         {
            clearLine(row); 
            lineDone = true;
         }       
      }
   }
   public void clearLine(int row)
   {
      for(int col = 0; col < boardSize; col++)
         setSquare(row, col, bg);
      for(int i = row-1; i >= 0; i--)
         for(int j = 0; j < boardSize; j++)
            setSquare(i+1, j, board[i][j].getBackground());
   }
   public boolean canGoDown()
   {
      boolean possible = true;
      int[][] thisconfig = current.getConfig();
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               config[i + currentRow][j + currentCol] = 0;
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               if((i + currentRow + 1) >= boardSize || config[i + currentRow + 1][j + currentCol] == 1 || currentRow+current.getLength() > boardSize)
               {
                  possible = false;
                  isFallingFinished = true;
               }
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               config[i + currentRow][j + currentCol] = 1;
      return possible;
   }
   public void oneLineDown()
   {
      int[][] thisconfig = current.getConfig();
      for(int i = currentRow; i < currentRow+thisconfig.length; i++)
         for(int j = currentCol; j < currentCol+thisconfig[0].length; j++)
            if(i < boardSize && j < boardSize)
               setSquare(i, j, null);
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               setSquare(i + currentRow + 1, j + currentCol, current.getColor());
      currentRow++;
   }
   public void goDown()
   {
      if(canGoDown())
         oneLineDown();
      checkLines();
   }
   public void left()
   { 
      int[][] thisconfig = current.getConfig();
      if(canGoLeft())
         if(currentCol > 0)
         {
            for(int i = 0; i < thisconfig.length; i++)
               for(int j = 0; j < thisconfig[0].length; j++)
                  //if(i < boardSize && j < boardSize)
                  setSquare(i+currentRow, j+currentCol, null);      
            for(int i = 0; i < thisconfig.length; i++)
               for(int j = 0; j < thisconfig[0].length; j++)
                  if(thisconfig[i][j] == 1)
                     setSquare(i + currentRow, j + currentCol-1, current.getColor());
            currentCol--;
         }
   }  
   public boolean canGoLeft()
   {
      boolean possible = true;
      int[][] thisconfig = current.getConfig();
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               config[i + currentRow][j + currentCol] = 0;
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               if((j + currentCol - 1) < 0 || config[i + currentRow][j + currentCol - 1] == 1)
                  possible = false;
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               config[i + currentRow][j + currentCol] = 1;
      return possible;
   }   
   public void right()
   { 
      int[][] thisconfig = current.getConfig();  
      if(canGoRight())   
         if(currentCol + current.getWidth()-1 + 1 < boardSize)
         {
            for(int i = currentRow; i < currentRow+4; i++)
               for(int j = currentCol; j < currentCol+4; j++)
                  if(i < boardSize && j < boardSize)
                     setSquare(i, j, null);      
            for(int i = 0; i < thisconfig.length; i++)
               for(int j = 0; j < thisconfig[0].length; j++)
                  if(thisconfig[i][j] == 1)
                     setSquare(i + currentRow, j + currentCol + 1, current.getColor());
            currentCol++;
         }
   }  
   public boolean canGoRight()
   {
      boolean possible = true;
      int[][] thisconfig = current.getConfig();
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               config[i + currentRow][j + currentCol] = 0;
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               if((j + currentCol + 1) >= boardSize || config[i + currentRow][j + currentCol + 1] == 1)
                  possible = false;
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if(thisconfig[i][j] == 1)
               config[i + currentRow][j + currentCol] = 1;
      return possible;
   }  
   public void rotate()
   {     
      boolean possible = true;
      int[][] possibleRotate = current.possibleRotate();
      if((currentCol + possibleRotate[0].length-1) >= boardSize)
         possible = false;
      if(canRotate() && possible == true)
      {
         current.rotateLeft();
         int[][] oldConfig = current.getOldConfig();
         for(int i = currentRow; i < currentRow+oldConfig.length; i++)
            for(int j = currentCol; j < currentCol+oldConfig[0].length; j++)
               setSquare(i, j, null);
         int[][] thisconfig = current.getConfig();
         for(int i = 0; i < thisconfig.length; i++)
            for(int j = 0; j < thisconfig[0].length; j++)
               if(thisconfig[i][j] == 1)
                  setSquare(i+currentRow, j+currentCol, current.getColor());
      }
   } 
   public boolean canRotate()
   {
      int[][] thisconfig = current.possibleRotate();
      int[][] currentconfig = current.getConfig();
      boolean possible = true;
      for(int i = 0; i < currentconfig.length; i++)
         for(int j = 0; j < currentconfig[0].length; j++)
            if(currentconfig[i][j] == 1)
               if((i + currentRow) < boardSize && (j + currentCol) < boardSize && (j + currentCol) >= 0)
                  config[i + currentRow][j + currentCol] = 0;
      for(int i = 0; i < thisconfig.length; i++)
         for(int j = 0; j < thisconfig[0].length; j++)
            if((i + currentRow) >= boardSize || (j + currentCol) >= boardSize || config[i + currentRow][j + currentCol] == 1)
               possible = false;
      for(int i = 0; i < currentconfig.length; i++)
         for(int j = 0; j < currentconfig[0].length; j++)
            if(currentconfig[i][j] == 1)
               if((i + currentRow) < boardSize && (j + currentCol) < boardSize && (j + currentCol) >= 0)
                  config[i + currentRow][j + currentCol] = 1;
      return possible;
   }    
   private class resetHandler implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         newGame();
         setFocusable(true);
      }
   }     
   public void newGame()
   {
      for(int r = 0; r < boardSize; r++)
         for(int c = 0; c < boardSize; c++)
         {
            config[r][c] = 0;
            board[r][c].setBackground(bg);
         }
      newPiece();
   } 
   public void newPiece()
   {
      int kind = (int)(Math.random() * 7);
      TetrisPiece t = new TetrisPiece(kind); 
      current = t;
      int[][] config = current.getConfig();
      for(int i = 0; i < config.length; i++)
         for(int j = 0; j < config[0].length; j++)
            if(config[i][j] == 1)
               setSquare(i, j+(boardSize/2)-1, current.getColor());  
      currentRow = 0;
      currentCol = boardSize/2 -1;            
      timer.start();
   } 
   public void setSquare(int r, int c, Color color)
   {
      if(color == null || color == bg)
      {
         board[r][c].setBackground(bg);
         config[r][c] = 0;
      }
      else
      {
         board[r][c].setBackground(color);
         config[r][c] = 1;
      }
   }
}
class TetrisPiece
{
   private Color color;
   private int rotate = 0, length, width, shape;
   private int[][] config, oldConfig;
   private final static int bits[][][][] = {
   	{
   		{//0
            {1},
   		   {1},
   		   {1},
   		   {1},
         },
         {
            {1, 1, 1, 1},
         },
         {
            {1},
   		   {1},
   		   {1},
   		   {1},
         },
         {
            {1, 1, 1, 1},
         },
   	},
   	{
         {//1
   		   {1, 1},
   		   {1, 0},
   		   {1, 0},
         },
         {
            {1, 1, 1},
   		   {0, 0, 1},
   		},
         {         
            {0, 1},
   		   {0, 1},
   		   {1, 1},
         },
         {
            {1, 0, 0},
   		   {1, 1, 1},
   		},
   	},
   	{//2
         {
   		   {1, 0},
   		   {1, 0},
   		   {1, 1},
         },
         {
            {1, 1, 1},
   		   {1, 0, 0},
         },
         {
            {1, 1},
   		   {0, 1},
   		   {0, 1},
   		},
         {         
            {0, 0, 1},
   		   {1, 1, 1},
   		},
   	},
   	{//3
         {
   		   {1, 0},
   		   {1, 1},
   		   {0, 1},
   		},
         {         
            {0, 1, 1},
   		   {1, 1, 0},
   		},
   	},
   	{//4
         {
   		   {1, 1, 0},
   		   {0, 1, 1},
   		},
         {         
            {0, 1},
   		   {1, 1},
   		   {1, 0},
   		},
   	},
   	{ //5 
         {
   		   {1, 0},
   		   {1, 1},
   		   {1, 0},
         },
         {
            {1, 1, 1},
   		   {0, 1, 0},
         },
         {
            {0, 1},
   		   {1, 1},
   		   {0, 1},
   		},
         {        
            {0, 1, 0},
   		   {1, 1, 1},
         },
   	},
   	{
         {//6
   		   {1, 1},
   		   {1, 1},
         }
   	},
   };
   private final static int bitsDimensions[][][] = {
   {{4,1}, {1,4}, {4,1}, {1,4},},
   {{3,2}, {2,3}, {3,2}, {2,3},},
   {{3,2}, {2,3}, {3,2}, {2,3},},
   {{3,2}, {2,3}, {3,2}, {2,3},},
   {{2,3}, {3,2}, {2,3}, {3,2},},
   {{3,2}, {2,3}, {3,2}, {2,3},},
   {{2,2}, {2,2}, {2,2}, {2,2},},
   };
   public TetrisPiece(int i)
   {
      shape = i;
      switch(i)
      {
         case 0: color = new Color(38, 224, 237);
            length = 4;
            width = 1;
            break;
         case 1: color = new Color(237, 128, 38);
            length = 3;
            width = 2;
            break;
         case 2: color = new Color(36, 43, 255);
            length = 3;
            width = 2;
            break;
         case 3: color = new Color(18, 255, 34);
            length = 3;
            width = 2;
            break;
         case 4: color = new Color(255, 18, 230);
            length = 2;
            width = 3;
            break;
         case 5: color = new Color(94, 0, 181);
            length = 3;
            width = 2;
            break;
         case 6: color = new Color(255, 255, 0);
            length = 2;
            width = 2;
            break;
      }
      readBits(i, length, width);
   
   }
   public void readBits(int numArray, int length, int width)
   {
      config = new int[length][width];
      for(int row = 0; row < length; row++)
         for(int col = 0; col < width; col++)
            config[row][col] = bits[numArray][0][row][col]; 
      oldConfig = config;
   }
   public int getLength()
   {
      return length;
   }
   public int getWidth()
   {
      return width;
   }
   public Color getColor()
   {
      return color;
   }
   public int[][] getConfig()
   {
      return config;
   }
   public int[][] getOldConfig()
   {
      return oldConfig;
   }
   public int[][] possibleRotate()
   {
      int numOptions = bits[shape].length;
      return bits[shape][(rotate+1)%numOptions];
   }
   public int[][] rotateLeft()
   {
      rotate++;
      oldConfig = config;
      int numOptions = bits[shape].length;
      config = bits[shape][rotate%numOptions];
      length = bitsDimensions[shape][rotate%numOptions][0];
      width = bitsDimensions[shape][rotate%numOptions][1];
      return config;
   }
}
