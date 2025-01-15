import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class DragAndDropChess {
    private static JLabel draggedPiece = null; // Przesuwany pionek
    private static Point initialPosition = null; // Początkowa pozycja etykiety

    public static void main(String[] args) {
        JFrame frame = new JFrame("Przeciąganie między polami");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new GridLayout(8, 8)); // Szachownica: 8x8

        ArrayList<JLabel> squares = new ArrayList<>();

        // Tworzenie pól szachownicy
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JLabel square = new JLabel();
                square.setOpaque(true);
                square.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY);
                square.setHorizontalAlignment(SwingConstants.CENTER);
                square.setVerticalAlignment(SwingConstants.CENTER);

                // Dodaj pionki tylko na wybranych polach
                if (row == 0 && col == 4) { // Na przykład król
                    ImageIcon pieceIcon = new ImageIcon("chess/pieces/KingW.png");
                    square.setIcon(scaleImageIcon(pieceIcon, 80, 80));
                }

                squares.add(square);
                frame.add(square);

                // Dodaj słuchacza myszy dla przeciągania
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (square.getIcon() != null) {
                            draggedPiece = square;
                            initialPosition = square.getLocation(); // Zapamiętaj startową pozycję
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (draggedPiece != null) {
                            JLabel targetSquare = getSquareAt(e.getPoint(), squares, frame);

                            if (targetSquare != null && targetSquare.getIcon() == null) {
                                // Przesunięcie na nowe pole
                                targetSquare.setIcon(draggedPiece.getIcon());
                                draggedPiece.setIcon(null); // Usunięcie ikony z oryginalnego pola
                            }

                            draggedPiece = null; // Zresetowanie przeciągania
                        }
                    }
                });
            }
        }

        frame.setVisible(true);
    }

    // Pomocnicza metoda do skalowania ikon
    private static ImageIcon scaleImageIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    // Znajduje pole, na które wskazuje mysz
    private static JLabel getSquareAt(Point point, ArrayList<JLabel> squares, JFrame frame) {
        for (JLabel square : squares) {
            Rectangle bounds = square.getBounds();
            Point location = SwingUtilities.convertPoint(frame.getContentPane(), point, square);
            if (bounds.contains(location)) {
                return square;
            }
        }
        return null; // Jeśli nie znaleziono pola
    }
}
