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
        RowsX = (float) height/8;
        ColY = (float) width/8;
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

    private String GetColumn(int col){
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
            //System.out.println("added");
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
            boolean added = false;
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
                added = true;
            } else if (column < 16) {
                Piece a = new Pawn("White",GetColumn(column%8));
                manager.pieces[column] = a;
                piece = a.pack;

                piece.setName("Pawn");
                square.add(piece);
                added = true;
            } else if (column >= 48 && column < 56) {
                Piece a = new Pawn("Black",GetColumn(column%8));;
                manager.pieces[column] = a;
                piece = a.pack;
                piece.setName("Pawn");

                square.add(piece);
                added = true;
            } else if (column >= 56) {
                Piece a = createPiece(piecesNames[column-56], "Black");
                manager.pieces[column] = a;
                piece = a.pack;
                piece.setName(piecesNames[column - 56]);
                square.add(PromotionMenu("White"),0);
                square.add(piece);
                added = true;
            } else {
                piece = new JLabel(new ImageIcon("chess/pieces/placehHolder.png"));
                manager.pieces[column]= new PlaceHolder();
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

    private Piece createPiece(String name, String color) {
        switch (name) {
            case "King": return new King(color);
            case "Queen": return new Queen(color);
            case "Bishop": return new Bishop(color);
            case "Knight": return new Knight(color);
            case "Rook": return new Rook(color);
            default: return null;
        }
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
                    String move="";
                    if (manager.turn) {
                        if (Objects.equals(tymczas.color, "White")) {
                            if(manager.CheckCheck(whiteKingPos)) System.out.println("Szach na białych");
                            switch (manager.previous.getName()) {
                                case "King":
                                    int c;
                                    if(vector[1]>0) {
                                        c = 2;
                                    }
                                    else c = -1;
                                    int rookIndex=indexPrevious+vector[1]+c;
                                    //System.out.format("King %b, %b, %b, %b, %b " , tymczas.firstMove , abs(vector[1])==2, manager.pieces[rookIndex].name=="Rook",  manager.pieces[ rookIndex].firstMove, !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious));

                                    if(tymczas.move==1 && abs(vector[1])==2 && manager.pieces[rookIndex].name=="Rook" && manager.pieces[rookIndex].move == 1 && !manager.CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious)){
                                        now = manager.Move("King", now);
                                        manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        System.out.println("roszada");
                                        //System.out.println("Panel components");
                                        for (int i=0;i<((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponentCount();i++){
                                            System.out.println(((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(i));
                                        }
                                        manager.previous=(JLabel) ((JPanel) boardSquares.getComponent(rookIndex)).getComponent(1);
                                        now = (JLabel) ((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(1);
                                        now = manager.Move("Rook", now);
                                        manager.UpdatePieces(rookIndex,indexNow-vector[1]/abs(vector[1]), manager.pieces[rookIndex]);
                                        manager.turn=!manager.turn;
                                        move+="roszada";
                                        break;
                                    }
                                    if(len<2 && (Diagonal(vector) || Strait(vector)) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                        now = manager.Move("King", now);
                                        move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    }
                                    break;
                                case "Queen":
                                    if (Diagonal(vector) || Strait(vector)) {
                                        if (!manager.CheckObstacle(indexPrevious, vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Queen", now);
                                            move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Bishop":
                                    if (Diagonal(vector)) {
                                        if (!manager.CheckObstacle(indexPrevious, vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Bishop", now);
                                            move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Knight":
                                    int[][] vectors = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
                                    for (int[] ints : vectors) {
                                        if (vector[0] == ints[0] && vector[1] == ints[1] && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Knight", now);
                                            move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Rook":
                                    if (Strait(vector)) {
                                        if (!manager.CheckObstacle(indexPrevious, vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Rook", now);
                                            move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Pawn":
                                    if(previousRow==4 && nowRow==5 && abs(nowColumn-previousColumn)==1){
                                        int pawnIndex = indexNow-8;
                                        int tymRow = indexNow / 8;
                                        int tymCol = indexNow % 8;
                                        vector = new int[]{tymRow-previousRow,tymCol-previousColumn};
                                        Pawn taken = (Pawn) manager.pieces[pawnIndex];
                                        String rightMove = String.format("%s%d%s%d",GetColumn(tymCol),tymRow+2,GetColumn(tymCol),tymRow);
                                        if(taken.startingColumn == GetColumn(tymCol) && taken.move == 2 && right.movesHistory.getLast().equals(rightMove)) {
                                            now = manager.Move("Pawn", now);
                                            manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                            manager.pieces[pawnIndex] = new PlaceHolder();
                                            JLabel tym = (JLabel) boardSquares.getComponent(pawnIndex).getComponentAt(1, 1);
                                            tym = manager.Move("PlaceHolder", tym);
                                            move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                            manager.turn = !manager.turn;
                                        }
                                    }
                                    if(nowRow==7 && previousRow == 6){
                                        JPopupMenu menuNow = (JPopupMenu) nowSquare.getComponent(0);
                                        menuNow.setPopupSize(100,400);
                                        menuNow.show(nowSquare,0,0);
                                    }
                                    //System.out.println("Vector: " + vector[0] + " " + vector[1]);System.out.println("Jestem: " + manager.pieces[indexNow].color + " drugi " + manager.pieces[indexPrevious].color + " tymczas: " + tymczas.color + " now " + nowPiece.color);
                                    if (vector[0] != 0 && vector[1] == 0 && (len==1 || (tymczas.move == 1 && len==2)) && !manager.CheckObstacle(indexPrevious, vector, len+1)) {
                                        now = manager.Move("Pawn", now);
                                        move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        tymczas.move +=1;
                                    } else if (Diagonal(vector) && len < 2 && !Objects.equals(nowPiece.name, "PlaceHolder") && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                        if (manager.CheckObstacle(indexPrevious, vector, 2)) {
                                            now = manager.Move("Pawn", now);
                                            tymczas.move +=1;
                                        }
                                        move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                    }
                                    break;
                                default:
                            }
                            right.ShowTurn(manager.turn);
                            right.AddToHistory(move);
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

                                    if(tymczas.move == 1 && abs(vector[1])==2 && manager.pieces[rookIndex].name=="Rook" && manager.pieces[rookIndex].move == 1 && !manager.CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious)){
                                        now = manager.Move("King", now);
                                        manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        System.out.println("roszada");
                                        //System.out.println("Panel components");
                                        for (int i=0;i<((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponentCount();i++){
                                            System.out.println(((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(i));
                                        }
                                        manager.previous=(JLabel) ((JPanel) boardSquares.getComponent(rookIndex)).getComponent(1);
                                        now = (JLabel) ((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(1);
                                        now = manager.Move("Rook", now);
                                        manager.UpdatePieces(rookIndex,indexNow-vector[1]/abs(vector[1]), manager.pieces[rookIndex]);
                                        manager.turn=!manager.turn;
                                        move+="roszada";
                                        break;
                                    }
                                    if(len<2 && (Diagonal(vector) || Strait(vector)) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                        now = manager.Move("King", now);
                                        move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    }
                                    break;
                                case "Queen":
                                    if(Diagonal(vector) || Strait(vector)){
                                        if(!manager.CheckObstacle(indexPrevious,vector,   len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                            now = manager.Move("Queen",now);
                                            move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Bishop":
                                    if(Diagonal(vector)){
                                        if(!manager.CheckObstacle(indexPrevious,vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Bishop",now);
                                            move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Knight":
                                    int[][] vectors = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}};
                                    for (int[] ints : vectors) {
                                        if (vector[0] == ints[0] && vector[1] == ints[1] && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Knight",now);
                                            move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Rook":
                                    if(Strait(vector)){
                                        if(!manager.CheckObstacle(indexPrevious,vector,   len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                            now = manager.Move("Rook",now);
                                            move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Pawn":
                                    if(previousRow==3 && nowRow==2 && abs(nowColumn-previousColumn)==1){
                                        System.out.println("Jestem");
                                        int pawnIndex = indexNow+8;
                                        int tymRow = indexNow / 8;
                                        int tymCol = indexNow % 8;
                                        vector = new int[]{tymRow-previousRow,tymCol-previousColumn};
                                        Pawn taken = (Pawn) manager.pieces[pawnIndex];
                                        String rightMove = String.format("%s%d%s%d",GetColumn(tymCol),tymRow,GetColumn(tymCol),tymRow+2);
                                        System.out.println(taken.startingColumn == GetColumn(tymCol));
                                        System.out.println(taken.move == 2);
                                        System.out.println(right.movesHistory.getLast());
                                        System.out.println(rightMove);
                                        if(taken.startingColumn == GetColumn(tymCol) && taken.move == 2 && right.movesHistory.getLast().equals(rightMove)) {
                                            now = manager.Move("Pawn", now);
                                            manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                            manager.pieces[pawnIndex] = new PlaceHolder();
                                            JLabel tym = (JLabel) boardSquares.getComponent(pawnIndex).getComponentAt(1, 1);
                                            tym = manager.Move("PlaceHolder", tym);
                                            move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                            manager.turn = !manager.turn;
                                        }
                                    }
                                    if(nowRow==0 && previousRow == 1){
                                        JPopupMenu menuNow = (JPopupMenu) nowSquare.getComponent(0);
                                        menuNow.setPopupSize(100,400);
                                        menuNow.show(nowSquare,0,0);
                                    }
                                    //System.out.println("Vector: " + vector[0] + " " + vector[1]);System.out.println("Jestem: " + manager.pieces[indexNow].color + " drugi " + manager.pieces[indexPrevious].color + " tymczas: " + tymczas.color + " now " + nowPiece.color);
                                    if(vector[0]!=0&&vector[1]==0 && (len==1 || (tymczas.move == 1 && len==2)) && !manager.CheckObstacle(indexPrevious,vector,len+1)){
                                        now = manager.Move("Pawn",now);
                                        move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                        tymczas.move += 1;
                                    }
                                    else if(Diagonal(vector) && len<2 && !Objects.equals(nowPiece.name, "PlaceHolder") && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                        if(manager.CheckObstacle(indexPrevious,vector,2)){
                                            now = manager.Move("Pawn",now);
                                            tymczas.move +=1;
                                        }
                                        move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    }
                                    break;
                                default:
                            }
                            right.ShowTurn(manager.turn);
                            right.AddToHistory(move);
                        }

                    }


                    //System.out.println("manager.pieces [indexNow] "+ manager.pieces[indexNow]);
                    //System.out.println("manager.pieces [indexPrevious] " + manager.pieces[indexPrevious]);



                }
            manager.ShowBoard();
            manager.previous = now;


            }
        };

}
