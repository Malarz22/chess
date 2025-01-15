import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.Vector;

import static java.lang.Math.*;

public class Board {
    //constants
    final static String[] piecesNames={"Rook", "Knight", "Bishop", "King", "Queen", "Bishop", "Knight", "Rook"};
    final static int boardSize =64;
    RightPanel right;
    JPanel boardSquares = new JPanel(new GridLayout(8,8));
    Point initialClick;
    float RowsX = (float) this.boardSquares.getHeight() /8;
    float  ColY = (float) this.boardSquares.getWidth()/8;
    JLabel previous;
    Piece[] pieces = new Piece[64];
    boolean turn=true;
    int pointsWhite = 0;
    int pointsBlack = 0;
    int whiteKingPos=3;
    int blackKingPos=58;
    Board(){
        int row = 0;
        for (int column = 0; column < boardSize; column++) {
            boolean added = false;
            if (column % 8 == 0) row += 1;
            JPanel square = new JPanel();
            square.setName(String.valueOf(column));
            square.setLayout(new GridBagLayout()); // Center components in the square

            JLabel piece;

            if (column < 8) {
                Piece a = new Piece(piecesNames[column], "White");
                pieces[column] = a;
                piece = a.pack;
                piece.setName(piecesNames[column]);
                square.add(PromotionMenu("Black"),0);
                square.add(piece);
                added = true;
            } else if (column < 16) {
                Piece a = new Piece("Pawn", "White");
                pieces[column] = a;
                piece = a.pack;
                
                piece.setName("Pawn");
                square.add(piece);
                added = true;
            } else if (column >= 48 && column < 56) {
                Piece a = new Piece("Pawn", "Black");
                pieces[column] = a;
                piece = a.pack;
                piece.setName("Pawn");
                
                square.add(piece);
                added = true;
            } else if (column >= 56) {
                Piece a = new Piece(piecesNames[column - 56], "Black");
                pieces[column] = a;
                piece = a.pack;
                piece.setName(piecesNames[column - 56]);
                square.add(PromotionMenu("White"),0);
                square.add(piece);
                added = true;
            } else {
                piece = new JLabel(new ImageIcon("chess/pieces/placehHolder.png"));
                pieces[column]= new Piece();
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

    private boolean CheckCheck(int index){
        int possibleWays =8;
        int possibleLen = 8;
        int[][] vectors = {{0,1},{1,0},{0,-1},{-1,0},{1,1},{-1,-1},{-1,1},{1,-1}};
        //sprawdzanie szachu
        //trzeba rozważyć kilka przypadków:
        //       szach zwykłky: figura po ruchu atakuje króla
        //       szach z odkrycia: figura za figurą ruszoną atakuje króla
        //       szach podwójny
        //       Sprawdząć czy król rusza się w szach
        //
        for (int j = 0; j<possibleWays; j++)
        {
            int[] vector = vectors[j];
            for( int h = 0; h<possibleLen-1; h++) {
                int b = abs(vector[1]);
                int c = abs(vector[0]);
                //zapobieganie dzieleniu przez 0
                if(b==0) b=1;
                if(c==0) c=1;
                index+=vector[0]/c*8+vector[1]/b;
                //System.out.println("index: "+index);
                JPanel tym =(JPanel) boardSquares.getComponent(index);
                if(!Objects.equals(tym.getComponent(CheckNumberOfComponents(tym)).getName(), "PlaceHolder")){
                    System.out.println("Znaleziono przeszkode");
                    break;
                }
                if(!Objects.equals(tym.getComponent(CheckNumberOfComponents(tym)).getName(), "King")){
                    System.out.println("Szach");
                    return true;
                }

            }
        }

        int[][] vectors2 = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] ints : vectors2) {
            if(index+ints[0]+ints[1]*8>63) continue;
            int ind = index+ints[0]+ints[1]*8;
            if(pieces[ind].name=="Knight" && pieces[index].color!=pieces[ind].color){
                return true;
            }
        }
        return false;
    }

    private void UpdatePieces(int index1, int index2, Piece a){
        pieces[index1] = new Piece();
        pieces[index2] = a;
    }

    private JLabel Move(String name, JLabel now){
        ImageIcon piece = (ImageIcon) previous.getIcon();
        previous.setIcon(new ImageIcon("chess/pieces/placehHolder.png"));
        now.setIcon(piece);
        previous.setName("PlaceHolder");
        now.setName(name);
        turn=!turn;
        return now;
    }

    private boolean CheckObstacle(int index ,int[] vector, int len){
        System.out.println("Start of function");
        for( int h = 0; h<len-1; h++) {
            int b = abs(vector[1]);
            int c = abs(vector[0]);
            //zapobieganie dzieleniu przez 0
            if(b==0) b=1;
            if(c==0) c=1;
            index+=vector[0]/c*8+vector[1]/b;
            //System.out.println("index: "+index);
            JPanel tym =(JPanel) boardSquares.getComponent(index);
            int d =0;
            if(tym.getComponentCount()>1) d=1;
            if(!Objects.equals(tym.getComponent(d).getName(), "PlaceHolder")){
                System.out.println("Znaleziono przeszkode" + tym.getComponent(d).getName() + " index " + index);
                return true;
            }

        }
        return false;
    }

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

    private int CheckNumberOfComponents(JPanel a){
        if(a.getComponentCount()>1)return 1;
        return 0;
    }

    public void getRightPanel(RightPanel a){
        right=a;
    }

    public JPopupMenu PromotionMenu(String color){
        JPopupMenu menu = new JPopupMenu("Promotion");
        int i=piecesNames.length-1;
        int d=piecesNames.length/2;
        while (i>=d) {
            System.out.println("i: " + i);
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
                    previous.setName(c.getName());
                    previous.setIcon(c.getIcon());
                    turn=!turn;
                    Move(previous.getName(),previous);
//                    JPopupMenu menu = (JPopupMenu) nowy.getParent();
//                    JPanel square = (JPanel) menu.getParent();
//                    JLabel piece = (JLabel) square.getComponent(1);
//                    piece.setName(c.getName());
//                    piece.setIcon(c.getIcon());

                }
            });
            System.out.println("added");
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
                if(!now.equals(previous) && previous!=null){
                    JPanel nowSquare = (JPanel) now.getParent();
                    JPanel previousSquare = (JPanel) previous.getParent();
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

                    Piece tymczas = pieces[indexPrevious];
                    Piece nowPiece = pieces[indexNow];
                    if (turn) {
                        if (Objects.equals(tymczas.color, "White")) {
                            if(CheckCheck(whiteKingPos)) System.out.println("Szach na białych");
                            switch (previous.getName()) {
                                case "King":
                                    int c;
                                    System.out.println("vector" + vector[0] + vector[1]);
                                    //znalezienie rookindex
                                    if(vector[1]>0) {
                                        c = 2;
                                    }
                                    else c = -1;
                                    int rookIndex=indexPrevious+vector[1]+c;
                                    //System.out.format("King %b, %b, %b, %b, %b " , tymczas.firstMove , abs(vector[1])==2, pieces[rookIndex].name=="Rook",  pieces[ rookIndex].firstMove, !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious));

                                    if(tymczas.firstMove && abs(vector[1])==2 && Objects.equals(pieces[rookIndex].name, "Rook") && pieces[rookIndex].firstMove && !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},abs(rookIndex-indexPrevious)-1)){
                                        now = Move("King", now);
                                        System.out.println("roszada");
                                        System.out.println("Panel components");
                                        for (int i=0;i<((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponentCount();i++){
                                            System.out.println(((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(i));
                                        }
                                        previous=(JLabel) ((JPanel) boardSquares.getComponent(rookIndex)).getComponent(1);
                                        now = (JLabel) ((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(1);
                                        now = Move("Rook", now);
                                        break;
                                    }
                                    if (len < 2 && (Diagonal(vector) || Strait(vector)) && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                        now = Move("King", now);
                                        UpdatePieces(indexPrevious, indexNow, tymczas);
                                    }
                                    break;
                                case "Queen":
                                    if (Diagonal(vector) || Strait(vector)) {
                                        if (!CheckObstacle(indexPrevious, vector, len) && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                            now = Move("Queen", now);
                                            UpdatePieces(indexPrevious, indexNow, tymczas);

                                        }
                                    }
                                    break;
                                case "Bishop":
                                    if (Diagonal(vector)) {
                                        if (!CheckObstacle(indexPrevious, vector, len) && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                            now = Move("Bishop", now);
                                            UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Knight":
                                    int[][] vectors = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
                                    for (int[] ints : vectors) {
                                        if (vector[0] == ints[0] && vector[1] == ints[1] && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                            now = Move("Knight", now);
                                            UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Rook":
                                    if (Strait(vector)) {
                                        if (!CheckObstacle(indexPrevious, vector, len) && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                            now = Move("Rook", now);
                                            UpdatePieces(indexPrevious, indexNow, tymczas);
                                        }
                                    }
                                    break;
                                case "Pawn":
                                    if(nowRow==7){
                                        JPopupMenu menuNow = (JPopupMenu) nowSquare.getComponent(0);
                                        menuNow.setPopupSize(100,400);
                                        menuNow.show(nowSquare,0,0);
                                    }
                                    //System.out.println("Vector: " + vector[0] + " " + vector[1]);System.out.println("Jestem: " + pieces[indexNow].color + " drugi " + pieces[indexPrevious].color + " tymczas: " + tymczas.color + " now " + nowPiece.color);
                                    if (vector[0] != 0 && vector[1] == 0 && (len==1 || (tymczas.firstMove && len==2)) && !CheckObstacle(indexPrevious, vector, len+1)) {
                                        now = Move("Pawn", now);
                                        UpdatePieces(indexPrevious, indexNow, tymczas);
                                        tymczas.firstMove=false;
                                    } else if (Diagonal(vector) && len < 2 && !Objects.equals(nowPiece.name, "PlaceHolder") && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                        if (CheckObstacle(indexPrevious, vector, 2)) {
                                            now = Move("Pawn", now);
                                            tymczas.firstMove=false;
                                        }
                                        UpdatePieces(indexPrevious, indexNow, tymczas);
                                    }
                                    break;
                                default:
                            }
                        }

                    }
                    else  {
                        if(Objects.equals(tymczas.color, "Black")){
                            if(CheckCheck(blackKingPos)) System.out.println("Szach na czarnych");
                            switch (previous.getName()){
                                case "King":
                                    int c;
                                    if(vector[1]>0) {
                                        c = 2;
                                    }
                                    else c = -1;
                                    int rookIndex=indexPrevious+vector[1]+c;
                                    //System.out.format("King %b, %b, %b, %b, %b " , tymczas.firstMove , abs(vector[1])==2, pieces[rookIndex].name=="Rook",  pieces[ rookIndex].firstMove, !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious));

                                    if(tymczas.firstMove && abs(vector[1])==2 && pieces[rookIndex].name=="Rook" && pieces[rookIndex].firstMove && !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious)){
                                        now = Move("King", now);
                                        System.out.println("roszada");
                                        previous=(JLabel) ((JPanel) boardSquares.getComponent(rookIndex)).getComponent(0);
                                        now = (JLabel) ((JPanel) boardSquares.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(0);
                                        now = Move("Rook", now);
                                    }
                                    if(len<2 && (Diagonal(vector) || Strait(vector)) && !Objects.equals(pieces[indexNow].color, tymczas.color)){
                                        now = Move("King", now);
                                        UpdatePieces(indexPrevious,indexNow,tymczas);
                                    }
                                    break;
                                case "Queen":
                                    if(Diagonal(vector) || Strait(vector)){
                                        if(!CheckObstacle(indexPrevious,vector,   len) && !Objects.equals(pieces[indexNow].color, tymczas.color)){
                                            now = Move("Queen",now);
                                            UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Bishop":
                                    if(Diagonal(vector)){
                                        if(!CheckObstacle(indexPrevious,vector, len) && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                            now = Move("Bishop",now);
                                            UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Knight":
                                    int[][] vectors = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{2,-1},{-2,1},{-2,-1}};
                                    for (int[] ints : vectors) {
                                        if (vector[0] == ints[0] && vector[1] == ints[1] && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                            now = Move("Knight",now);
                                            UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Rook":
                                    if(Strait(vector)){
                                        if(!CheckObstacle(indexPrevious,vector,   len) && !Objects.equals(pieces[indexNow].color, tymczas.color)) {
                                            now = Move("Rook",now);
                                            UpdatePieces(indexPrevious,indexNow,tymczas);
                                        }
                                    }
                                    break;
                                case "Pawn":
                                    if(nowRow==0){
                                        JPopupMenu menuNow = (JPopupMenu) nowSquare.getComponent(0);
                                        menuNow.setPopupSize(100,400);
                                        menuNow.show(nowSquare,0,0);
                                    }
                                    //System.out.println("Vector: " + vector[0] + " " + vector[1]);System.out.println("Jestem: " + pieces[indexNow].color + " drugi " + pieces[indexPrevious].color + " tymczas: " + tymczas.color + " now " + nowPiece.color);
                                    if(vector[0]!=0&&vector[1]==0 && (len==1 || (tymczas.firstMove && len==2)) && !CheckObstacle(indexPrevious,vector,len+1)){
                                        now = Move("Pawn",now);
                                        UpdatePieces(indexPrevious,indexNow,tymczas);
                                        tymczas.firstMove=false;
                                    }
                                    else if(Diagonal(vector) && len<2 && !Objects.equals(nowPiece.name, "PlaceHolder") && !Objects.equals(pieces[indexNow].color, tymczas.color)){
                                        if(CheckObstacle(indexPrevious,vector,2)){
                                            now = Move("Pawn",now);
                                            tymczas.firstMove=false;
                                        }
                                        UpdatePieces(indexPrevious,indexNow,tymczas);
                                    }
                                    break;
                                default:
                            }
                        }
                    }


                    //System.out.println("pieces [indexNow] "+ pieces[indexNow]);
                    //System.out.println("pieces [indexPrevious] " + pieces[indexPrevious]);



                }

            previous = now;

            }
        };
}
