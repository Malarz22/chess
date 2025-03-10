import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RightPanel extends JPanel {
    JButton nowaGra = new JButton("New Game");
    JPanel historyPanel = new JPanel();

    BoxLayout boxLayout = new BoxLayout(historyPanel, BoxLayout.Y_AXIS);
    JLabel turnLabel = new JLabel();
    List<String> movesHistory = new ArrayList<String>();
    RightPanel(Board b, JPanel boardPanel){
        historyPanel.setLayout(boxLayout);
        JPanel buttonContainer = new JPanel();
        nowaGra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked");
                b.SetUpBoard();
                boardPanel.updateUI();
            }
        });
        this.setLayout(null); // Wyłączenie menedżera układu w panelu

        buttonContainer.setBounds(700+560/2,625,100,100);
        buttonContainer.add(nowaGra);

        buttonContainer.setBackground(Color.YELLOW);
        this.add(buttonContainer);
        historyPanel.setBounds(700+560/2-200/4,100,200,400);
        historyPanel.setOpaque(true);
        historyPanel.setBackground(Color.WHITE);
        JLabel text = new JLabel("HISTORIA");
        text.setAlignmentX(0.5f);
        historyPanel.add(text);
        turnLabel.setAlignmentX(0.5f);
        historyPanel.add(turnLabel);
        this.add(historyPanel);
    }

    public void ShowTurn(boolean turn){
        if (turn) {
            turnLabel.setText("White Turn");
            return;
        }
        turnLabel.setText("Black Turn");
    }

    public void ShowWinner(String color){
        JLabel a =new JLabel("Winner is " + color);
        a.setAlignmentX(0.5f);
        historyPanel.add(a);
    }

    public void AddToHistory(String move){
        movesHistory.add(move);
        if(move.length()==0) return;
        String beutifullMove = move.substring(0,2) + "         " + move.substring(move.length()-2);
        JLabel a =new JLabel(beutifullMove);
        a.setAlignmentX(0.5f);
        historyPanel.add(a);
    }
}
