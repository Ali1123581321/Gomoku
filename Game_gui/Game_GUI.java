package Game_gui;

import java.util.Observable;
import java.util.Observer;
import java.awt.*;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import Game_state.*;
import Game_state.Game_state.States;

public class Game_GUI extends JPanel implements Observer{
    Game_state game_state;
    private JFrame frame;
    private Game_panel panel;
    private final int square_size = 20;
    private int DEFAULT_SIZE = 3;
    private MouseInputListener mouse_handler;
    private JButton pvp_button;
    private JButton pvc_button;
    private JButton end_game_button;
    private JLabel message_label;
    private SpringLayout layout;
    private JLabel com_count_label;
    private JLabel p1_count_label;
    private JLabel p2_count_label;


    public Game_GUI(){
        this.game_state = new Game_state(DEFAULT_SIZE);
        this.game_state.addObserver(this);
        pvp_button = new JButton("pvp");
        pvc_button = new JButton("pvc");
        end_game_button = new JButton("end game");
        message_label = new JLabel("Welcome to gomoku!");
        game_init();
        p1_count_init();
    }
    private void game_init(){
        layout = new SpringLayout();
        frame = new JFrame("Gomoku");
        frame.setLayout(layout);
        frame.setSize(new Dimension(2*DEFAULT_SIZE*square_size, 2*DEFAULT_SIZE*square_size));
        panel = new Game_panel(this.game_state.get_board(), DEFAULT_SIZE, square_size);
        panel.setPreferredSize(new Dimension(DEFAULT_SIZE*square_size, DEFAULT_SIZE*square_size));
        frame.add(panel);
        layout.putConstraint(SpringLayout.NORTH, panel, 8, SpringLayout.NORTH, frame.getContentPane());
        layout.putConstraint(SpringLayout.WEST, panel, 8, SpringLayout.WEST, frame.getContentPane());
        panel.setBackground(Color.WHITE);
        frame.getContentPane().add(pvp_button); frame.getContentPane().add(pvc_button); frame.getContentPane().add(end_game_button);
        layout.putConstraint(SpringLayout.WEST, pvc_button, 8, SpringLayout.WEST, frame.getContentPane());
        layout.putConstraint(SpringLayout.NORTH, pvc_button, 5, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.NORTH, pvp_button, 5, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.NORTH, end_game_button, 5, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.WEST, pvp_button, 5, SpringLayout.EAST, pvc_button);
        layout.putConstraint(SpringLayout.WEST, end_game_button, 5, SpringLayout.EAST, pvp_button);
        frame.getContentPane().add(message_label);
        layout.putConstraint(SpringLayout.WEST, message_label, 8, SpringLayout.WEST, frame.getContentPane());
        layout.putConstraint(SpringLayout.NORTH, message_label, 5, SpringLayout.SOUTH, pvp_button);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        create_mouse_adapter();
        panel.addMouseListener(mouse_handler);
        add_action_listeners();
    }
    private int[] get_board_location(int x, int y){
        int arr[] = new int[2];
        arr[0] = x / square_size;
        arr[1] = y / square_size;
        return arr;
    }
    private void create_mouse_adapter(){
        mouse_handler = new MouseInputAdapter(){
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e){
                System.out.println("mouse clicked");
                int x = e.getX();
                int y = e.getY();
                int arr[] = get_board_location(x, y);
                System.out.println("the x is " + arr[0] + " the y is " + arr[1]);
                game_state.move(arr[0], arr[1]);
            }
        };
    }
    void p1_count_init(){
        this.p1_count_label = new JLabel(game_state.p1_wins);
        frame.getContentPane().add(p1_count_label);
        layout.putConstraint(SpringLayout.WEST, p1_count_label, 8, SpringLayout.WEST, frame.getContentPane());
        layout.putConstraint(SpringLayout.NORTH, p1_count_label, 5, SpringLayout.SOUTH, message_label);
    }
    void com_count_init(){
        this.com_count_label = new JLabel(game_state.com_wins);
        frame.getContentPane().add(com_count_label);
        layout.putConstraint(SpringLayout.WEST, com_count_label, 5, SpringLayout.EAST, p1_count_label);
        layout.putConstraint(SpringLayout.NORTH, com_count_label, 5, SpringLayout.SOUTH, message_label);
    }
    void p2_count_init(){
        this.p2_count_label = new JLabel(game_state.p2_wins);
        layout.putConstraint(SpringLayout.WEST, p2_count_label, 5, SpringLayout.EAST, p1_count_label);
        layout.putConstraint(SpringLayout.NORTH, p2_count_label, 5, SpringLayout.SOUTH, message_label);
    }
    
    @Override
    public void update(Observable o, Object arg){
        message_label.setText(game_state.message);
        p1_count_label.setText(game_state.p1_wins);
        if(p2_count_label != null)
            p2_count_label.setText(game_state.p2_wins);
        else if(com_count_label != null)
            com_count_label.setText(game_state.com_wins);
    }
    private void add_action_listeners(){
        AbstractAction pvp_action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game_state.change_state(States.PVP);
                p2_count_init();
                com_count_label = null;
            }
        };pvp_button.addActionListener(pvp_action);
        AbstractAction pvc_action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game_state.change_state(States.PVC);
                p2_count_label = null;
                com_count_init();
            }            
        };pvc_button.addActionListener(pvc_action);
        AbstractAction end_action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                game_state.change_state(States.END_GAME);
            }
        };end_game_button.addActionListener(end_action);
    }
}
