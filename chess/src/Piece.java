import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;


public class Piece {
    ImageIcon look = new ImageIcon();
    JLabel pack = new JLabel();
    String color;
    String name;
    int points;
    boolean firstMove = true;
    //boolean firstMove=true;
    // Visualization of the possibilities array:
    //int[][] possibilities = new int[8][8];
    //  0  0  0  0  2  0  0  0
    //  0  0  1  0  1  0  Y  0
    //  0  1  0  0  2  1  0  0
    //  0  0  0  x  0  2  0  2
    //  0  1  0  0  0  1  0  0
    //  0  0  1  0  1  0  0  0
    //  0  0  0  0  0  0  0  0
    //  0  0  0  0  0  0  0  0
    // Kon - musi mieć a=2 i b = 1. i wtedy mamy 8 możliwości w najlebższym momencie zastanowić się nad rozwiązaniem a = 2 i a potem a-1
    // Pion - a = 2 sprawdzać czy ma bicie po skosie - 1,1 od pozycji
    // Wieża - a=8
    // Goniec - a = 8 zmiana 1,1
    // Król a = 1
    // Królowa a = 8
    // przy każdym oczywiście sprawdzamy czy nie stoi na przeszkodzie pionek

    Piece(){
        color="";
        name="";
        points=0;
    }

    Piece(String Name,String Color){
        color = Color;
        name=Name;
        //Wybieranie koloru
        switch (Name){
            case "King":
                points = 100;
                if(Objects.equals(color, "White")) {
                    look = new ImageIcon("chess/pieces/KingW.png");

                }
                else if(Objects.equals(color, "Black")) {
                    System.out.print("Król czarna");
                    look = new ImageIcon("chess/pieces/KingB.png");
                }
                else
                    throw new RuntimeException("Błąd inicjalizacji Króla");
                break;
            case "Queen":
                points = 8;
                if(Objects.equals(color, "White"))
                    look = new ImageIcon("chess/pieces/QueenW.png");
                else if(Objects.equals(color, "Black"))
                    look = new ImageIcon("chess/pieces/QueenB.png");
                else
                    throw new RuntimeException("Błąd inicjalizacji Królowej");
                break;
            case "Bishop":
                points = 3;
                if(Objects.equals(color, "White"))
                    look = new ImageIcon("chess/pieces/BishopW.png");
                else if(Objects.equals(color, "Black"))
                    look = new ImageIcon("chess/pieces/bishopBT.png");
                else
                    throw new RuntimeException("Błąd inicjalizacji gońca");
                break;
            case "Knight":
                points = 3;
                if(Objects.equals(color, "White"))
                    look = new ImageIcon("chess/pieces/KnightW.png");
                else if(Objects.equals(color, "Black"))
                    look = new ImageIcon("chess/pieces/KnightB.png");
                else
                    throw new RuntimeException("Błąd inicjalizacji skoczka");
                break;
            case "Rook":
                points = 5;
                if(Objects.equals(color, "White"))
                    look = new ImageIcon("chess/pieces/RookW.png");
                else if(Objects.equals(color, "Black"))
                    look = new ImageIcon("chess/pieces/RookB.png");
                else
                    throw new RuntimeException("Błąd inicjalizacji wieży");
                break;
            default:
                points = 1;
                if(Objects.equals(color, "White"))
                    look = new ImageIcon("chess/pieces/PawnW.png");
                else if(Objects.equals(color, "Black"))
                    look = new ImageIcon("chess/pieces/PawnB.png");
                else
                    throw new RuntimeException("Błąd inicjalizacji pionka");
        }
        pack.setHorizontalAlignment(SwingConstants.CENTER);
        pack.setVerticalAlignment(SwingConstants.CENTER);

        pack.setIcon(look);
    }
    @Override
    public String toString(){
        return "To jest figura: " + name + " jest koloru: " + color + " jest warta punktów: " + points;
    }


}
