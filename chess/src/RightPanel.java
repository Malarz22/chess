import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

public class RightPanel extends JPanel {
    JButton nowaGra = new JButton("New Game");
    JLabel history = new JLabel(new ImageIcon("wood.jpg"));

    RightPanel(){
        JPanel buttonContainer = new JPanel();
        nowaGra.addActionListener(new Action() {
            @Override
            public Object getValue(String key) {
                return null;
            }

            @Override
            public void putValue(String key, Object value) {

            }

            @Override
            public void setEnabled(boolean b) {

            }

            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public void addPropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void removePropertyChangeListener(PropertyChangeListener listener) {

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked");
            }
        });
        this.setLayout(null); // Wyłączenie menedżera układu w panelu

        buttonContainer.setBounds(700+560/2,625,100,100);
        buttonContainer.add(nowaGra);

        buttonContainer.setBackground(Color.YELLOW);
        this.add(buttonContainer);
        history.setBounds(700+560/2-200/4,100,200,400);

        this.add(history);
    }

    public void ShowTurn(boolean turn){
        if (turn) {
            JLabel k = new JLabel("White turn");
            history.add(k);
        }
        JLabel k = new JLabel("White turn");
        history.add(k);
    }
}
