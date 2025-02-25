import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;
import static java.lang.Math.*;

public class Board {
    //constants

    final static String[] piecesNames={"Rook", "Knight", "Bishop", "King", "Queen", "Bishop", "Knight", "Rook"};
    final static int boardSize =64;
    PiecesManagment manager;
    RightPanel right;
    JPanel boardSquares = new JPanel(new GridLayout(8,8));
    Point initialClick;
    float RowsX = (float) this.boardSquares.getHeight() /8;
    float  ColY = (float) this.boardSquares.getWidth()/8;
    boolean TheEnd=false;

    int pointsWhite = 0;
    int pointsBlack = 0;
    int whiteKingPos=3;
    int blackKingPos=59;
    Board(){
       SetUpBoard();
    }

/*
    private void CheckBorder(int indexNow, int[] vector){
        //muszę sprawdzać tutaj czy jego index wychodzi poza granice szachownicy
        //Jak to zrobić?
        //w przypadku skrajnych wierszy jest proste bo jeśli aktualny index jest <0 albo >63
        //gorzej jest w przypadku kolumn
        //w przypadku kolumn muszę sprawdzać czy aktualny index nie dzieli się przez 8

           1 2 3 4 5 6 7 8
         1 _ _ _ _ _ _ _ _
         2 x _ _ _ _ _ _ _
         3 _ _ _ _ _ _ _ _
         4 _ _ _ _ _ _ _ _
         5 _ _ _ _ _ _ _ _
         6 _ _ _ _ _ _ _ _
         7 _ _ _ _ _ _ _ _
         8 _ _ _ _ _ _ _ _
           0  1  2  3  4  5  6  7
           8  9  10 11 12 13 14 15
           16 17 18 19 20 21 22 23
           24 25 26 27 28 29 30 31
           32 33 34 35 36 37 38 39
           40 41 42 43 44 45 46 47
           48 49 50 51 52 53 54 55
           56 57 58 59 60 61 62 63
         Jeśli jest w pierwszej kolumnie czyli dzieli się przez 8 muszę sprawdzić

    }
*/
    public int GetIndex(int row1,int col1, int row2, int col2){
        if(abs(row1-row2)==abs(col1-col2)){
            return abs(row1-row2);
        }
        if(abs(row1-row2)!=0){
            return abs(row1-row2);
        }
        else return abs(col1-col2);
    }

    public boolean Diagonal(int[] vector){
        return (abs(vector[0])==abs(vector[1]));
    }

    public boolean Strait(int[] vector){
        return (vector[0]*vector[1]==0);
    }

    public void SetRowsCols(float height, float width){
        RowsX = height /8;
        ColY = width /8;
        //System.out.format("Wysokość każdego pola: %d Długość każdego pola: %d %n",(int)RowsX,(int)ColY);
    }

    public String GetCoordinates(int index){
        String coordinates = "";
        int col = index%8;
        int row = index/8+1;
        coordinates+=GetColumn(col);
        coordinates+=Integer.toString(row);
        return coordinates;
    }

    protected String GetColumn(int col){
        return switch (col) {
            case 0 -> "a";
            case 1 -> "b";
            case 2 -> "c";
            case 3 -> "d";
            case 4 -> "e";
            case 5 -> "f";
            case 6 -> "g";
            case 7 -> "h";
            default -> "";
        };
    }

    public void getRightPanel(RightPanel a){
        right=a;
    }

    public JPopupMenu PromotionMenu(String color){
        JPopupMenu menu = new JPopupMenu("Promotion");
        int i=piecesNames.length-1;
        int d=piecesNames.length/2;
        while (i>=d) {
            //System.out.println("i: " + i);
            JMenuItem nowy = new JMenuItem();
            Piece m = createPiece(piecesNames[i], color);
            JLabel p = new JLabel();
            p.setIcon(m.look);
            p.setName(m.name);
            nowy.add(p, 0);
            nowy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JLabel c = (JLabel) ((JMenuItem) e.getSource()).getComponent(0);
                    manager.previous.setName(c.getName());
                    manager.previous.setIcon(c.getIcon());
                    manager.turn=!manager.turn;
                    manager.Move(manager.previous.getName(),manager.previous);
                }
            });
            //System.out.println("");
            menu.add(nowy);
            i+=-1;
        }
        return menu;
    }

    public void SetUpBoard(){
        int row = 0;
        manager = new PiecesManagment(this);
        boardSquares = new JPanel(new GridLayout(8,8));
        for (int column = 0; column < boardSize; column++) {
            if (column % 8 == 0) row += 1;
            JPanel square = new JPanel();
            square.setName(String.valueOf(column));
            square.setLayout(new GridBagLayout()); // Center components in the square

            JLabel piece;

            if (column < 8) {
                Piece a = createPiece(piecesNames[column], "White");
                manager.pieces[column] = a;
                piece = a.pack;
                piece.setName(piecesNames[column]);
                square.add(PromotionMenu("Black"),0);
                square.add(piece);
            } else if (column < 16) {
                Piece a = new Pawn("White",GetColumn(column%8));
                manager.pieces[column] = a;
                piece = a.pack;

                piece.setName("Pawn");
                square.add(piece);
            } else if (column >= 48 && column < 56) {
                Piece a = new Pawn("Black",GetColumn(column%8));
                manager.pieces[column] = a;
                piece = a.pack;
                piece.setName("Pawn");

                square.add(piece);
            } else if (column >= 56) {
                Piece a = createPiece(piecesNames[column-56], "Black");
                manager.pieces[column] = a;
                piece = a.pack;
                piece.setName(piecesNames[column - 56]);
                square.add(PromotionMenu("White"),0);
                square.add(piece);
            } else {
                piece = new JLabel(new ImageIcon("chess/pieces/placehHolder.png"));
                manager.pieces[column]= new PlaceHolder();
                square.add(piece);
                piece.setName("PlaceHolder");
            }
            setColor(column,row,square);

            piece.addMouseListener(myMouse);

            boardSquares.add(square,column);


        }

    }

    public void setColor(int column, int row, JPanel square){
        if (column % 2 == 0) {
            if (row % 2 == 0)
                square.setBackground(new Color(139, 69, 19));
            else
                square.setBackground(new Color(245, 222, 179));
        }
        else {
            if (row % 2 == 0)
                square.setBackground(new Color(245, 222, 179));
            else
                square.setBackground(new Color(139, 69, 19));
        }
    }

    private Piece createPiece(String name, String color) {
        return switch (name) {
            case "King" -> new King(color);
            case "Queen" -> new Queen(color);
            case "Bishop" -> new Bishop(color);
            case "Knight" -> new Knight(color);
            case "Rook" -> new Rook(color);
            default -> null;
        };
    }

    MouseAdapter myMouse = new MyMouseAdapter(manager, this,right);
}
