import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
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

    int pointsWhite = 0;
    int pointsBlack = 0;
    int whiteKingPos=3;
    int blackKingPos=59;
    Board(){
        int row = 0;
        manager = new PiecesManagment(this);
        for (int column = 0; column < boardSize; column++) {
            boolean added = false;
            if (column % 8 == 0) row += 1;
            JPanel square = new JPanel();
            square.setName(String.valueOf(column));
            square.setLayout(new GridBagLayout()); // Center components in the square

            JLabel piece;

            if (column < 8) {
                Piece a = new Piece(piecesNames[column], "White");
                manager.pieces[column] = a;
                piece = a.pack;
                piece.setName(piecesNames[column]);
                square.add(PromotionMenu("Black"),0);
                square.add(piece);
                added = true;
            } else if (column < 16) {
                Piece a = new Piece("Pawn", "White");
                manager.pieces[column] = a;
                piece = a.pack;
                
                piece.setName("Pawn");
                square.add(piece);
                added = true;
            } else if (column >= 48 && column < 56) {
                Piece a = new Piece("Pawn", "Black");
                manager.pieces[column] = a;
                piece = a.pack;
                piece.setName("Pawn");
                
                square.add(piece);
                added = true;
            } else if (column >= 56) {
                Piece a = new Piece(piecesNames[column - 56], "Black");
                manager.pieces[column] = a;
                piece = a.pack;
                piece.setName(piecesNames[column - 56]);
                square.add(PromotionMenu("White"),0);
                square.add(piece);
                added = true;
            } else {
                piece = new JLabel(new ImageIcon("chess/pieces/placehHolder.png"));
                manager.pieces[column]= new Piece();
                square.add(piece);
                piece.setName("PlaceHolder");
            }

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
            piece.addMouseListener(myMouse);

            boardSquares.add(square,column);


        }
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
        RowsX = (float) height/8;
        ColY = (float) width/8;
        //System.out.format("Wysokość każdego pola: %d Długość każdego pola: %d %n",(int)RowsX,(int)ColY);
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
            Piece m = new Piece(piecesNames[i], color);
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
//                    JPopupMenu menu = (JPopupMenu) nowy.getParent();
//                    JPanel square = (JPanel) menu.getParent();
//                    JLabel piece = (JLabel) square.getComponent(1);
//                    piece.setName(c.getName());
//                    piece.setIcon(c.getIcon());

                }
            });
            //System.out.println("added");
            menu.add(nowy);
            i+=-1;
        }
        return menu;
    }

    MouseAdapter myMouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                initialClick = e.getPoint();
                JLabel now = (JLabel) e.getSource();
                if(!now.equals(manager.previous) && manager.previous!=null){
                    JPanel nowSquare = (JPanel) now.getParent();
                    JPanel previousSquare = (JPanel) manager.previous.getParent();
                    int indexNow = boardSquares.getComponentZOrder(nowSquare); // Indeks w siatce
                    //System.out.println("indexNow now: " + indexNow);
                    int nowRow = indexNow / 8; // Wiersz w planszy 8x8
                    int nowColumn = indexNow % 8;
                    int indexPrevious = boardSquares.getComponentZOrder(previousSquare);
                    //System.out.println("indexPrevious previous: " + indexPrevious);
                    int previousRow =  indexPrevious / 8; // Wiersz w planszy 8x8
                    int previousColumn= indexPrevious % 8;
                    int[] vector = {nowRow-previousRow,nowColumn-previousColumn};
                    int len = GetIndex(nowRow,nowColumn,previousRow,previousColumn);
                    //System.out.println(previous.getName());

                    Piece tymczas = manager.pieces[indexPrevious];
                    Piece nowPiece = manager.pieces[indexNow];
                    if (manager.turn) {
                        if (Objects.equals(tymczas.color, "White")) {
                            if(manager.CheckCheck(whiteKingPos)) System.out.println("Szach na białych");
                            switch (manager.previous.getName()) {
                                case "King":
                                    int c;
                                    //System.out.println("vector" + vector[0] + vector[1]);
                                    //znalezienie rookindex
                                    if(vector[1]>0) {
                                        c = 2;
                                    }
                                    else c = -1;
                                    int rookIndex=indexPrevious+vector[1]+c;
                                    //System.out.format("King %b, %b, %b, %b, %b " , tymczas.firstMove , abs(vector[1])==2, manager.pieces[rookIndex].name=="Rook",  manager.pieces[ rookIndex].firstMove, !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious));

                                    if(tymczas.firstMove && abs(vector[1])==2 && Objects.equals(manager.pieces[rookIndex].name, "Rook") && manager.pieces[rookIndex].firstMove && !manager.CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},abs(rookIndex-indexPrevious)-1)){
                                        now = manager.Move("King", now);
                                        System.out.println("roszada");
                                        //System.out.println("Panel components");
                                        for (int i=0;i<((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponentCount();i++){
                                            System.out.println(((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(i));
                                        }
                                        manager.previous=(JLabel) ((JPanel) boardSquares.getComponent(rookIndex)).getComponent(1);
                                        now = (JLabel) ((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(1);
                                        now = manager.Move("Rook", now);
                                        manager.turn=!manager.turn;
                                        break;
                                    }
                                    if (len < 2 && (Diagonal(vector) || Strait(vector)) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                        now = manager.Move("King", now);
                                        manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                    }
                                    break;
                                case "Queen":
                                    if (Diagonal(vector) || Strait(vector)) {
                                        if (!manager.CheckObstacle(indexPrevious, vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Queen", now);
                                            manager.UpdatePieces(indexPrevious, indexNow, tymczas);

                                        }
                                    }
                                    break;
                                case "Bishop":
                                    if (Diagonal(vector)) {
                                        if (!manager.CheckObstacle(indexPrevious, vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Bishop", now);
                                            manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Knight":
                                    int[][] vectors = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
                                    for (int[] ints : vectors) {
                                        if (vector[0] == ints[0] && vector[1] == ints[1] && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Knight", now);
                                            manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Rook":
                                    if (Strait(vector)) {
                                        if (!manager.CheckObstacle(indexPrevious, vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Rook", now);
                                            manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Pawn":
                                    if(nowRow==7){
                                        JPopupMenu menuNow = (JPopupMenu) nowSquare.getComponent(0);
                                        menuNow.setPopupSize(100,400);
                                        menuNow.show(nowSquare,0,0);
                                    }
                                    //System.out.println("Vector: " + vector[0] + " " + vector[1]);System.out.println("Jestem: " + manager.pieces[indexNow].color + " drugi " + manager.pieces[indexPrevious].color + " tymczas: " + tymczas.color + " now " + nowPiece.color);
                                    if (vector[0] != 0 && vector[1] == 0 && (len==1 || (tymczas.firstMove && len==2)) && !manager.CheckObstacle(indexPrevious, vector, len+1)) {
                                        now = manager.Move("Pawn", now);
                                        manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        tymczas.firstMove=false;
                                    } else if (Diagonal(vector) && len < 2 && !Objects.equals(nowPiece.name, "PlaceHolder") && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                        if (manager.CheckObstacle(indexPrevious, vector, 2)) {
                                            now = manager.Move("Pawn", now);
                                            tymczas.firstMove=false;
                                        }
                                        manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                    }
                                    break;
                                default:
                            }
                        }

                    }
                    else  {
                        if(Objects.equals(tymczas.color, "Black")){
                            if(manager.CheckCheck(blackKingPos)) System.out.println("Szach na czarnych");
                            switch (manager.previous.getName()){
                                case "King":
                                    int c;
                                    if(vector[1]>0) {
                                        c = 2;
                                    }
                                    else c = -1;
                                    int rookIndex=indexPrevious+vector[1]+c;
                                    //System.out.format("King %b, %b, %b, %b, %b " , tymczas.firstMove , abs(vector[1])==2, manager.pieces[rookIndex].name=="Rook",  manager.pieces[ rookIndex].firstMove, !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious));

                                    if(tymczas.firstMove && abs(vector[1])==2 && manager.pieces[rookIndex].name=="Rook" && manager.pieces[rookIndex].firstMove && !manager.CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious)){
                                        now = manager.Move("King", now);
                                        System.out.println("roszada");
                                        //System.out.println("Panel components");
                                        for (int i=0;i<((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponentCount();i++){
                                            System.out.println(((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(i));
                                        }
                                        manager.previous=(JLabel) ((JPanel) boardSquares.getComponent(rookIndex)).getComponent(1);
                                        now = (JLabel) ((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(1);
                                        now = manager.Move("Rook", now);
                                        manager.turn=!manager.turn;
                                        break;
                                    }
                                    if(len<2 && (Diagonal(vector) || Strait(vector)) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                        now = manager.Move("King", now);
                                        manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    }
                                    break;
                                case "Queen":
                                    if(Diagonal(vector) || Strait(vector)){
                                        if(!manager.CheckObstacle(indexPrevious,vector,   len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                            now = manager.Move("Queen",now);
                                            manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Bishop":
                                    if(Diagonal(vector)){
                                        if(!manager.CheckObstacle(indexPrevious,vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Bishop",now);
                                            manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Knight":
                                    int[][] vectors = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}};
                                    for (int[] ints : vectors) {
                                        if (vector[0] == ints[0] && vector[1] == ints[1] && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Knight",now);
                                            manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Rook":
                                    if(Strait(vector)){
                                        if(!manager.CheckObstacle(indexPrevious,vector,   len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Rook",now);
                                            manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Pawn":
                                    if(nowRow==0){
                                        JPopupMenu menuNow = (JPopupMenu) nowSquare.getComponent(0);
                                        menuNow.setPopupSize(100,400);
                                        menuNow.show(nowSquare,0,0);
                                    }
                                    //System.out.println("Vector: " + vector[0] + " " + vector[1]);System.out.println("Jestem: " + manager.pieces[indexNow].color + " drugi " + manager.pieces[indexPrevious].color + " tymczas: " + tymczas.color + " now " + nowPiece.color);
                                    if(vector[0]!=0&&vector[1]==0 && (len==1 || (tymczas.firstMove && len==2)) && !manager.CheckObstacle(indexPrevious,vector,len+1)){
                                        now = manager.Move("Pawn",now);
                                        manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        tymczas.firstMove=false;
                                    }
                                    else if(Diagonal(vector) && len<2 && !Objects.equals(nowPiece.name, "PlaceHolder") && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                        if(manager.CheckObstacle(indexPrevious,vector,2)){
                                            now = manager.Move("Pawn",now);
                                            tymczas.firstMove=false;
                                        }
                                        manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    }
                                    break;
                                default:
                            }
                        }
                    }
                    right.ShowTurn(manager.turn);


                    //System.out.println("manager.pieces [indexNow] "+ manager.pieces[indexNow]);
                    //System.out.println("manager.pieces [indexPrevious] " + manager.pieces[indexPrevious]);



                }

            manager.previous = now;

            }
        };
}
