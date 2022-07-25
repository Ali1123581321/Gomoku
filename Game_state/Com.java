package Game_state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;


import Game_state.Board.square_state;

public class Com extends Player implements Observer{
    private Board tmp_board;
    private Node root;
    private HashMap<String, Node> memory;
    boolean start_over = true;
    public Com(Board tmp_board){
        super(square_state.COM);
        this.memory = new HashMap<>();
        this.tmp_board = tmp_board;
        root = new Node(null);
    }

    @Override
    public void update(Observable o, Object arg){
        
    }
    private class Node{
        enum GAME_STATE {WON, LOSE, DRAW, UNDEFINED};
        GAME_STATE game_state;
        HashMap<String, Node> childeren; Node parent;
        double win; double visits;
        square_state origin_states[][]; square_state clone_states[][];
        List<int[]> possible_moves = new ArrayList<>();
        int first_move[] = new int[2];
        Player p;

        public Node(Node parent){
            this.parent = parent;
            origin_states = new square_state[tmp_board.size][tmp_board.size]; 
            clone_states = new square_state[tmp_board.size][tmp_board.size];
            game_state = GAME_STATE.UNDEFINED;
            childeren = new HashMap<>();
            p = new Player(square_state.P1);
            win = 0;
            visits = 0;
        }
    }

    void copy_states(Node node, square_state states[][]){
        node.clone_states = new square_state[states.length][states.length];
        node.origin_states = new square_state[states.length][states.length];
        square_state curr;
        for(int i = 0; i < tmp_board.size; i++){
            for(int n = 0; n < tmp_board.size; n++){
                curr = states[i][n];
                node.origin_states[i][n] = curr;
                node.clone_states[i][n] = curr;
                if(curr == square_state.EMPTY){
                    int arr[] = new int[2];
                    arr[0] = i;
                    arr[1] = n;
                    node.possible_moves.add(arr);
                }
            }
        }
    }
    void build_base_nodes(Node root){
        int count = 0;
        for(int i = 0; i < tmp_board.size && count < tmp_board.empty_squares; i++){
            for(int n = 0; n < tmp_board.size && count < tmp_board.empty_squares; n++){
                if(tmp_board.get_grid()[i][n] != square_state.EMPTY)
                    continue;
                count++;
                Node child = new Node(root);
                child.first_move = new int[2]; child.first_move[0] = i; child.first_move[1] = n;
                copy_states(child, root.origin_states);
                child.p.state = square_state.COM;
                child.origin_states[i][n] = square_state.COM;
                child.clone_states[i][n] = square_state.COM;
                root.childeren.put(String.valueOf(i) + String.valueOf(n), child);
                boolean isWinner = tmp_board.isWinner(child.p, child.origin_states, child.first_move);
                if(isWinner){
                    child.game_state = Node.GAME_STATE.WON;
                }else if(child.possible_moves.isEmpty()){
                    child.game_state = Node.GAME_STATE.DRAW;
                }int result = simulate(child);
                propogate(child, result);
            }
        }
    }
    Node selection(Node node, double max_score){
        if(node.game_state != Node.GAME_STATE.UNDEFINED)
            return node;
        else{
            double tmp_score;
            Node tmp_node; Node result_node = node;
            for(int i = 0; i < tmp_board.size; i++){
                for(int n = 0; n < tmp_board.size; n++){
                    if(!node.childeren.containsKey(String.valueOf(i) + String.valueOf(n)))
                        continue;
                    tmp_node = node.childeren.get(String.valueOf(i) + String.valueOf(n));
                    tmp_score = (tmp_node.win/tmp_node.visits) + (Math.sqrt(Math.log(2*tmp_node.parent.visits/tmp_node.visits)));
                    if(tmp_score > max_score){
                        result_node = tmp_node;
                        max_score = tmp_score;
                    }
                }
            }if(result_node == node)
                return node;
            else
                return(selection(result_node, max_score));
        }   
    }
    Node expand(Node parent){
        if(parent.game_state != Node.GAME_STATE.UNDEFINED)
            return parent;
        Node expansion_node = new Node(parent);
        copy_states(expansion_node, parent.origin_states);
        if(parent.p.state == square_state.COM){
            expansion_node.p = new Player(square_state.P1);
        }else{
            expansion_node.p = new Player(square_state.COM);
        }if(expansion_node.possible_moves.size() == 1){
            expansion_node.first_move = expansion_node.possible_moves.remove(0);
        }else{
            expansion_node.first_move = expansion_node.possible_moves.remove(new Random().nextInt(expansion_node.possible_moves.size()));
        }if(parent.childeren.containsKey(String.valueOf(expansion_node.first_move[0]) + String.valueOf(expansion_node.first_move[1]))){
            return (parent.childeren.get(String.valueOf(expansion_node.first_move[0]) + String.valueOf(expansion_node.first_move[1])));
        }
        expansion_node.clone_states[expansion_node.first_move[0]][expansion_node.first_move[1]] = expansion_node.p.state;
        expansion_node.origin_states[expansion_node.first_move[0]][expansion_node.first_move[1]] = expansion_node.p.state;
        boolean isWinner = tmp_board.isWinner(expansion_node.p, expansion_node.origin_states, expansion_node.first_move);
        if(isWinner && expansion_node.p.state == square_state.COM){
            expansion_node.game_state = Node.GAME_STATE.WON;
        }else if(isWinner && expansion_node.p.state == square_state.P1){
            expansion_node.game_state = Node.GAME_STATE.LOSE;
        }else if(expansion_node.possible_moves.isEmpty()){
            expansion_node.game_state = Node.GAME_STATE.DRAW;
        }parent.childeren.put((String.valueOf(expansion_node.first_move[0]) + String.valueOf(expansion_node.first_move[1])), expansion_node);
        return expansion_node;
    }
    int simulate(Node node){
        if(node.p.state == square_state.COM && node.game_state == Node.GAME_STATE.WON)
            return 1;
        else if(node.game_state == Node.GAME_STATE.LOSE)
            return -1;
        else if(node.game_state == Node.GAME_STATE.DRAW)
            return 0;
        Player p = new Player(node.p.state);
        int curr_move[];
        Random r = new Random();
        while(!node.possible_moves.isEmpty()){
            if(p.state == square_state.P1)
                p.state = square_state.COM;
            else
                p.state = square_state.P1;
            curr_move = node.possible_moves.remove(r.nextInt(node.possible_moves.size()));
            node.clone_states[curr_move[0]][curr_move[1]] = p.state;
            if(tmp_board.isWinner(p, node.clone_states, curr_move)){
                if(p.state == square_state.COM)
                    return 1;
                else{
                    return -1;
                }
            }
        }return 0;
    }
    void propogate(Node node, int result){
        while(node != null){
            node.visits++;
            if(node.p.state == square_state.COM){
                if(result == 1)
                    node.win++;
                else if(result == -1)
                    node.win--;
            }else if(node.p.state == square_state.P1){
                if(result == -1)
                    node.win++;
                else if(result == 1)
                    node.win--;
            }propogate(node.parent, result);
            return;
        }
    }
    void run_simulation(Node node){
        long start_time = System.currentTimeMillis();
        while(System.currentTimeMillis() - start_time < 1000){
            Node parent = selection(node, 0);
            Node expansion_node = expand(parent);
            int result = simulate(expansion_node);
            propogate(expansion_node, result);
        }
    }

    void init_node(){
        Node tmp_node = new Node(null);
        tmp_node.origin_states = tmp_board.get_grid();
        tmp_node.clone_states = tmp_board.get_grid();
        build_base_nodes(tmp_node);
        memory.put(String.valueOf(tmp_board.get_last_move()[0]) + String.valueOf(tmp_board.get_last_move()[1]), tmp_node);
        root = tmp_node;
    }

    void find_best_move(){
        if(memory.isEmpty()){
            init_node();
            start_over = false;
        }else if(start_over){
            if(memory.containsKey(String.valueOf(tmp_board.get_last_move()[0]) + String.valueOf(tmp_board.get_last_move()[1])))
                this.root = memory.get(String.valueOf(tmp_board.get_last_move()[0]) + String.valueOf(tmp_board.get_last_move()[1]));
            else{
                init_node();
            }
            start_over = false;
        }else{
            root = root.childeren.get(String.valueOf(tmp_board.get_last_move()[0]) + String.valueOf(tmp_board.get_last_move()[1]));
        }
        run_simulation(root);
        double tmp_score; double max_score = -1;
        Node tmp_node; Node result_node = root;
        for(int i = 0; i < tmp_board.size; i++){
            for(int n = 0; n < tmp_board.size; n++){
                if(!root.childeren.containsKey(String.valueOf(i) + String.valueOf(n)))
                    continue;
                tmp_node = root.childeren.get(String.valueOf(i) + String.valueOf(n));
                if(tmp_node.visits == 0)
                    continue;
                else{
                    tmp_score = tmp_node.win/tmp_node.visits;
                    if(tmp_score > max_score){
                        result_node = tmp_node;
                        max_score = tmp_score;
                    }
                }
            }
        }root = result_node;
        tmp_board.move(result_node.first_move[1], result_node.first_move[0], result_node.p);
    }
} 