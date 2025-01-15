import javax.swing.*;
import java.awt.*;

public class Main{

    public static void main(String[] args) {
        Piece[] white;
        Piece[] black;
        Board plansza = new Board();
        JFrame frame = new JFrame();


        frame.setTitle("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1360,768);
        //frame.add(order);
        RightPanel boczny = new RightPanel();
        boczny.setBackground(Color.BLACK);
        JPanel wszystko = new JPanel();
        JPanel planszaPanel = plansza.boardSquares;

//        JPanel a = (JPanel) planszaPanel.getComponentAt(new Point(256,456));
//        System.out.println(a);
        planszaPanel.setBounds(0,0,700,700);
        plansza.SetRowsCols(planszaPanel.getHeight(),planszaPanel.getWidth());
        boczny.setBounds(700,0, 560,700);
        frame.add(planszaPanel);
        frame.add(boczny);
        //frame.add(wszystko);
        frame.setVisible(true);
    }

}