package Game_state;
import java.util.Observable;

import Game_state.Board.square_state;

public class Game_state extends Observable{
    public enum States {NO_GAME_STARTED, PVP, PVC, GAME_FINISHED, END_GAME};
    private Player p1;
    private Player p2;
    private Com com;
    private States state;
    private Board board;
    public String message;
    private Player turn;
    private int DEFAULT_INROW = 5;
    private int com_win = 0; private int p1_win = 0; private int p2_win = 0;
    public String com_wins = "com: " + com_win; public String p1_wins = "p1: "+ p1_win; public String p2_wins = "p2: " + p2_win;

    public Game_state(int size){
        this.board = new Board(size, DEFAULT_INROW);
        this.state = States.NO_GAME_STARTED;
        this.setChanged();
        this.notifyObservers();
    }

    public Board get_board(){
        return this.board;
    }

    public void change_state(States new_state){
        if(new_state == States.END_GAME){
            this.state = States.GAME_FINISHED;
            this.board.clear_grid();
            this.setChanged();
            this.notifyObservers();
        }else if(state != States.NO_GAME_STARTED && state != States.GAME_FINISHED){
            this.message = "There is already a game going!";
            this.setChanged();
            this.notifyObservers();
            return;
        }else if(new_state == States.PVC){
            board.clear_grid();
            this.state = new_state;
            this.p1 = new Player(square_state.P1);
            this.com = new Com(this.board);
            this.addObserver(this.com);
            p2_win = 0;
            turn = p1;
            this.message = "a game has started";
            this.setChanged();
            this.notifyObservers();
        }else if(new_state == States.PVP){
            board.clear_grid();
            this.state = new_state;
            this.p1 = new Player(square_state.P1);
            this.p2 = new Player(square_state.P2);
            this.message = "a game has started";
            com_win = 0;
            turn = p1;
            this.setChanged();
            this.notifyObservers();
        }
    }
    public void move(int x, int y){
        if(this.state == States.NO_GAME_STARTED || this.state == States.GAME_FINISHED){
            this.message = "start a new game";
        }else if(this.state == States.PVC){
            if(this.turn == this.com){
                move_and_reverse_turns(x, y, this.p1);
                this.com.my_turn = false;
            }else{
                this.com.my_turn = true;
                move_and_reverse_turns(x, y, this.com);
            }
        }else{
            if(this.turn == p1)
                move_and_reverse_turns(x, y, this.p2);
            else if(this.turn == p2){
                move_and_reverse_turns(x, y, this.p1);  
            }
        }this.setChanged();
        this.notifyObservers();
    }
    
    private void move_and_reverse_turns(int x, int y, Player next){
        if(this.turn == com){
            this.com.find_best_move();
            this.message = "com has played";
        }else if(this.board.move(x, y, turn)){
            this.message = "valid move";
        }else{
            this.message = "not_valid move";
            return;
        }if(this.board.isWinner(turn, this.board.get_grid(), this.board.get_last_move())){
            if(turn.state == square_state.COM){
                this.message = "com has won, the game is over";
                com_win++;
                com_wins = "com: " + com_win;
            }else if (turn.state == square_state.P1){
                this.message = "p1 has won, the game is over";
                p1_win++;
                p1_wins = "p1: " + p1_win;
            }else{
                this.message = "p2 has won, the game is over";
                p2_win++;
                p2_wins = "p2: " + p2_win;
            }
            this.state = States.GAME_FINISHED;
            return;
        }if(this.board.empty_squares == 0){
            this.state = States.GAME_FINISHED;
            this.message = "DRAW";
            return;
        }
        turn = next;
    }
}
