package Game_gui;

import java.util.Observable;
import java.util.Observer;
import java.awt.*;

import javax.swing.JPanel;
import Game_state.Board.square_state;
import Game_state.Board;

public class Game_panel extends JPanel implements Observer{
    Board game_board;
    int square_size;
    int grid_size;
    
    public Game_panel(Board game_board, int grid_size, int square_size){
        this.game_board = game_board;
        this.square_size = square_size;
        this.grid_size = grid_size;
        game_board.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.paint(this.getGraphics());
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        square_state[][] grid = game_board.get_grid();
        for(int i = 0; i < grid_size; i++){
            for(int n = 0; n < grid_size; n++){
                g.drawRect(i*square_size, n*square_size, square_size, square_size);
                if(grid[i][n] == square_state.P1){
                    g.setColor(Color.RED);
                    g.drawOval(n*square_size + (square_size / 4), i*square_size + (square_size / 4), square_size/2, square_size/2);
                    g.setColor(Color.BLACK);
                }else if(grid[i][n] != square_state.EMPTY){
                    g.setColor(Color.BLUE);
                    g.drawOval(n*square_size + (square_size / 4), i*square_size + (square_size / 4), square_size/2, square_size/2);
                    g.setColor(Color.BLACK);
                }                    
            }
        }
    }
}
