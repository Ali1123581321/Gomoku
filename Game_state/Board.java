package Game_state;

import java.util.Observable;

public class Board extends Observable{
    int size;
    public enum square_state {EMPTY, P1, P2, COM};
	int empty_squares;
    private square_state[][] grid;
    private int last_move[] = new int[2];
    private int inRow;

    public Board(int size, int inRow){
        this.inRow = inRow;
        this.size = size;
		empty_squares = size*size;
        grid = new square_state[size][size];
        create_grid(grid);
		this.setChanged();
		this.notifyObservers();
    }
    private void create_grid(square_state grid[][]){
        for(int i = 0; i < grid.length; i++){
            for(int n = 0; n < grid.length; n++){
                grid[i][n] = square_state.EMPTY;
            }
        }this.setChanged();
		this.notifyObservers();
    }
    public void clear_grid(){
		empty_squares = size*size;
        create_grid(this.grid);
    }
	public square_state[][] get_grid(){
		return grid;
	}
	public int[] get_last_move(){
		return this.last_move;
	}

    boolean move(int x, int y, Player p){
        if(grid[y][x] == square_state.EMPTY){
            last_move[0] = y;
            last_move[1] = x;
            grid[y][x] = p.state;
			empty_squares--;
			this.setChanged();
			this.notifyObservers();
            return true;
        }return false;
    }	
    private boolean check_row(Player curr_player, square_state[][] grid, int[] last_move){
		square_state row[] = grid[last_move[0]];
		int col = last_move[1];
		int l_count = 0, r_count = 0, m_count = 0;
		boolean l = true, r = true;
		for(int n = 1; n < inRow && (l || r); n++){
			if(l && col - n >= 0 && row[col - n] == curr_player.state){
				l_count++;
				m_count++;
			}else l = false;
			if(r && col + n < size && row[col + n] == curr_player.state){
				r_count++;
				m_count++;
			}
		}return (m_count == inRow - 1 || l_count == inRow - 1 || r_count == inRow - 1);
	}

	private boolean check_col(Player curr_player, square_state[][] grid, int[] last_move){
		int row = last_move[0];
		int col = last_move[1];
		int up_count = 0; int down_count = 0; int m_count = 0;
		boolean up = true; boolean down = true;
		for(int n = 1; n < inRow && (up || down); n++){
			if(down && row + n < size && grid[row + n][col] == curr_player.state){
				down_count++;
				m_count++;
			}else{
				down = false;
			}if(up && row - n >= 0 && grid[row - n][col] == curr_player.state){
				up_count++;
				m_count++;
			}else
				up = false;
		}return (up_count == inRow - 1 || down_count == inRow - 1 || m_count == inRow - 1);
	}

	private boolean check_diag(Player curr_player, square_state[][] grid, int[] last_move){
		int row = last_move[0];
		int col = last_move[1];
		int lu_diag_count = 0; int ru_diag_count = 0; int ml_diag_count = 0; int mr_diag_count = 0;
		int rd_diag_count = 0, ld_diag_count = 0;
		boolean lu_diag = true, ru_diag = true, ld_diag = true, rd_diag = true;
		for(int n = 1; n < inRow; n++){
			if(rd_diag && row + n < size && col + n < size && grid[row + n][col + n] == curr_player.state){
				rd_diag_count++;
				mr_diag_count++;
			}else{
				rd_diag = false;
			}if(ru_diag && row - n >= 0 && col + n < size && grid[row - n][col + n] == curr_player.state){
				ru_diag_count++;
				ml_diag_count++;
			}else{
				ru_diag = false;
			}if(ld_diag && row + n < size && col - n >= 0 && grid[row + n][col - n] == curr_player.state){
				ld_diag_count++;
				ml_diag_count++;
			}else{
				ld_diag = false;
			}if(lu_diag && row - n >= 0 && col - n >= 0 && grid[row - n][col - n] == curr_player.state){
				lu_diag_count++;
				mr_diag_count++;
			}else{
				lu_diag = false;
			}
		}return(ml_diag_count == inRow - 1 || mr_diag_count == inRow - 1 || lu_diag_count == inRow - 1 || 
				ru_diag_count == inRow - 1 || rd_diag_count == inRow - 1 || ld_diag_count == inRow - 1);
	}
	

	public boolean isWinner(Player player, square_state[][] grid, int[] last_move){
		return(check_row(player, grid, last_move) || check_col(player, grid, last_move) || check_diag(player, grid, last_move));
	}
}