import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.abs;

public class MyMouseAdapter extends MouseAdapter {
    PiecesManagment manager;
    Board mainBoard;
    RightPanel right;
    public MyMouseAdapter(PiecesManagment manager,Board mainBoard,RightPanel rightPanel){
        this.manager=manager;
        this.mainBoard = mainBoard;
        this.right = rightPanel;
    };
    @Override
    public void mousePressed(MouseEvent e) {
        JLabel now = (JLabel) e.getSource();
        JPanel nowSquare = (JPanel) now.getParent();
        nowSquare.setBackground(Color.BLACK);
        if(!now.equals(manager.previous) && manager.previous!=null && !mainBoard.TheEnd){

            JPanel previousSquare = (JPanel) manager.previous.getParent();
            int indexNow = mainBoard.boardSquares.getComponentZOrder(nowSquare); // Indeks w siatce
            //System.out.println("indexNow now: " + indexNow);
            int nowRow = indexNow / 8; // Wiersz w planszy 8x8
            int nowColumn = indexNow % 8;
            int indexPrevious = mainBoard.boardSquares.getComponentZOrder(previousSquare);
            //System.out.println("indexPrevious previous: " + indexPrevious);
            int previousRow =  indexPrevious / 8; // Wiersz w planszy 8x8
            int previousColumn= indexPrevious % 8;
            mainBoard.setColor(previousColumn,previousRow+1,previousSquare);
            int[] vector = {nowRow-previousRow,nowColumn-previousColumn};
            int len = mainBoard.GetIndex(nowRow,nowColumn,previousRow,previousColumn);
            //System.out.println(previous.getName());

            java.util.List<int[]> allowedMoves = new ArrayList<>();
            int[] moveList = {indexNow, indexPrevious};

            Piece tymczas = manager.pieces[indexPrevious];
            Piece nowPiece = manager.pieces[indexNow];
            String move="";
            if (manager.turn) {
                if (Objects.equals(tymczas.color, "White")) {
                    java.util.List<int[]> check = manager.CheckCheck(mainBoard.whiteKingPos);
                    if(check.size()==2){
                        System.out.println("Szach podwójny na białych");
                        java.util.List<int[]> nowa1 = manager.PreventCheck(mainBoard.whiteKingPos,check.getFirst()[1]);
                        java.util.List<int[]> nowa2 = manager.PreventCheck(mainBoard.whiteKingPos, check.get(1)[1]);
                        for(int[] i :nowa1){
                            for (int[] j:nowa2){
                                if(Arrays.equals(i,j)){
                                    allowedMoves.add(i);
                                }
                            }
                        }
                    }
                    else{
                        if(check.getFirst()[0]==1){
                            java.util.List<int[]> nowa = manager.PreventCheck(mainBoard.whiteKingPos,check.getFirst()[1]);
                            for(int[] ints : nowa){
                                for(int j : ints){
                                    System.out.println(j);
                                }
                                allowedMoves.add(ints);
                                System.out.println("Koniec ruchu");
                            }
                        }
                    }
                    boolean warunek = false;
                    for(int[] i:allowedMoves){
                        if(Arrays.equals(i, moveList)) warunek=true;
                    }
                    if(allowedMoves.isEmpty()&&check.getFirst()[0]==1){
                        //implementacja końca gry
                        right.ShowWinner("Black");
                        mainBoard.TheEnd = true;
                    }
                    if(allowedMoves.isEmpty() ||warunek && !mainBoard.TheEnd) {
                        switch (manager.previous.getName()) {
                            case "King":
                                int c;
                                if(vector[1]>0) {
                                    c = 2;
                                }
                                else c = -1;
                                int rookIndex=indexPrevious+vector[1]+c;
                                //System.out.format("King %b, %b, %b, %b, %b " , tymczas.firstMove , abs(vector[1])==2, manager.pieces[rookIndex].name=="Rook",  manager.pieces[ rookIndex].firstMove, !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious));

                                if(tymczas.move==1 && abs(vector[1])==2 && manager.pieces[rookIndex].name.equals("Rook") && manager.pieces[rookIndex].move == 1 && !manager.CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious)){
                                    int afterMoveRoomIndex =indexNow-vector[1]/abs(vector[1]);
                                    now = manager.Move("King", now);
                                    manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    System.out.println("roszada");
                                    //System.out.println("Panel components");
                                    for (int i=0;i<((JPanel) mainBoard.boardSquares.getComponent(afterMoveRoomIndex)).getComponentCount();i++){
                                        System.out.println(((JPanel) mainBoard.boardSquares.getComponent(afterMoveRoomIndex)).getComponent(i));
                                    }
                                    manager.previous=(JLabel) ((JPanel) mainBoard.boardSquares.getComponent(rookIndex)).getComponent(1);
                                    now = (JLabel) ((JPanel) mainBoard.boardSquares.getComponent(afterMoveRoomIndex)).getComponent(1);
                                    now = manager.Move("Rook", now);
                                    manager.UpdatePieces(rookIndex,afterMoveRoomIndex, manager.pieces[rookIndex]);
                                    manager.turn=!manager.turn;
                                    move+="roszada";
                                    mainBoard.whiteKingPos=indexNow;
                                    break;
                                }
                                if(len<2 && (mainBoard.Diagonal(vector) || mainBoard.Strait(vector)) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color ) && manager.CheckCheck(indexNow,"White").getFirst()[0]==0){
                                    now = manager.Move("King", now);
                                    move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    mainBoard.whiteKingPos=indexNow;
                                }
                                break;
                            case "Queen":
                                if (mainBoard.Diagonal(vector) || mainBoard.Strait(vector)) {
                                    if (!manager.CheckObstacle(indexPrevious, vector, len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                        now = manager.Move("Queen", now);
                                        move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                    }
                                }
                                break;
                            case "Bishop":
                                if (mainBoard.Diagonal(vector)) {
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
                                if (mainBoard.Strait(vector)) {
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
                                    String rightMove = String.format("%s%d%s%d",mainBoard.GetColumn(tymCol),tymRow+2,mainBoard.GetColumn(tymCol),tymRow);
                                    if(taken.startingColumn.equals(mainBoard.GetColumn(tymCol)) && taken.move == 2 && right.movesHistory.getLast().equals(rightMove)) {
                                        now = manager.Move("Pawn", now);
                                        manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        manager.pieces[pawnIndex] = new PlaceHolder();
                                        JLabel tym = (JLabel) mainBoard.boardSquares.getComponent(pawnIndex).getComponentAt(1, 1);
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
                                } else if (mainBoard.Diagonal(vector) && len < 2 && !Objects.equals(nowPiece.name, "EPlaceHolder") && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)) {
                                    if (manager.CheckObstacle(indexPrevious, vector, 2)) {
                                        now = manager.Move("Pawn", now);
                                        tymczas.move +=1;
                                    }
                                    move += manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                }
                                break;
                            default:
                        }
                    }
                    right.ShowTurn(manager.turn);
                    right.AddToHistory(move);
                }

            }
            else  {
                if(Objects.equals(tymczas.color, "Black")){
                    java.util.List<int[]> check = manager.CheckCheck(mainBoard.blackKingPos);
                    if(check.size()>1){
                        System.out.println("Szach podwójny na białych");
                        java.util.List<int[]> nowa1 = manager.PreventCheck(mainBoard.blackKingPos,check.getFirst()[1]);
                        java.util.List<int[]> nowa2 = manager.PreventCheck(mainBoard.blackKingPos, check.get(1)[1]);
                        for(int[] i :nowa1){
                            for(int[] j : nowa2){
                                if(Arrays.equals(i, j))  allowedMoves.add(i);
                            }
                        }
                    }
                    else{
                        if(check.getFirst()[0]==1){
                            java.util.List<int[]> nowa = manager.PreventCheck(mainBoard.blackKingPos,check.getFirst()[1]);
                            for(int[] ints : nowa){
                                for(int j : ints){
                                    System.out.println(j);
                                }
                                allowedMoves.add(ints);
                                System.out.println("Koniec ruchu");
                            }
                        }
                    }
                    boolean warunek = false;
                    for(int[] i:allowedMoves){
                        if (Arrays.equals(i, moveList)) {
                            warunek = true;
                            break;
                        }
                    }
                    if(allowedMoves.isEmpty()&&check.getFirst()[0]==1){
                        //implementacja końca gry
                        right.ShowWinner("White");
                        mainBoard.TheEnd = true;
                    }
                    if(allowedMoves.isEmpty() || warunek && !mainBoard.TheEnd) {
                        switch (manager.previous.getName()){
                            case "King":
                                int c;
                                if(vector[1]>0) {
                                    c = 2;
                                }
                                else c = -1;
                                int rookIndex=indexPrevious+vector[1]+c;
                                //System.out.format("King %b, %b, %b, %b, %b " , tymczas.firstMove , abs(vector[1])==2, manager.pieces[rookIndex].name=="Rook",  manager.pieces[ rookIndex].firstMove, !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious));

                                if(tymczas.move == 1 && abs(vector[1])==2 && manager.pieces[rookIndex].name.equals("Rook") && manager.pieces[rookIndex].move == 1 && !manager.CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious)){
                                    int afterMoveRoomIndex =indexNow-vector[1]/abs(vector[1]);
                                    boolean throughChecks = false;
                                    for (int i=0;i<2;i++){
                                        List<int[]> temp = manager.CheckCheck(indexNow-(vector[1]/abs(vector[1])*i),"Black");
                                        if(temp.getFirst()[0]==1) throughChecks=true;
                                    }
                                    if(!throughChecks)
                                    {
                                        now = manager.Move("King", now);
                                        manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        System.out.println("roszada");
                                        //System.out.println("Panel components");
                                        for (int i = 0; i < ((JPanel) mainBoard.boardSquares.getComponent(afterMoveRoomIndex)).getComponentCount(); i++) {
                                            System.out.println(((JPanel) mainBoard.boardSquares.getComponent(afterMoveRoomIndex)).getComponent(i));
                                        }
                                        manager.previous = (JLabel) ((JPanel) mainBoard.boardSquares.getComponent(rookIndex)).getComponent(1);
                                        now = (JLabel) ((JPanel) mainBoard.boardSquares.getComponent(afterMoveRoomIndex)).getComponent(1);
                                        now = manager.Move("Rook", now);
                                        manager.UpdatePieces(rookIndex, afterMoveRoomIndex, manager.pieces[rookIndex]);
                                        manager.turn = !manager.turn;
                                        move += "roszada";
                                        mainBoard.blackKingPos = indexNow;
                                        break;
                                    }
                                }
                                if(len<2 && (mainBoard.Diagonal(vector) || mainBoard.Strait(vector)) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color) && manager.CheckCheck(indexNow,"Black").getFirst()[0]==0){
                                    now = manager.Move("King", now);
                                    move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    mainBoard.blackKingPos=indexNow;

                                }
                                break;
                            case "Queen":
                                if(mainBoard.Diagonal(vector) || mainBoard.Strait(vector)){
                                    if(!manager.CheckObstacle(indexPrevious,vector,   len) && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                        now = manager.Move("Queen",now);
                                        move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                    }
                                }
                                break;
                            case "Bishop":
                                if(mainBoard.Diagonal(vector)){
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
                                if(mainBoard.Strait(vector)){
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
                                    String rightMove = String.format("%s%d%s%d",mainBoard.GetColumn(tymCol),tymRow,mainBoard.GetColumn(tymCol),tymRow+2);
                                    System.out.println(taken.startingColumn.equals(mainBoard.GetColumn(tymCol)));
                                    System.out.println(taken.move == 2);
                                    System.out.println(right.movesHistory.getLast());
                                    System.out.println(rightMove);
                                    if(taken.startingColumn.equals(mainBoard.GetColumn(tymCol)) && taken.move == 2 && right.movesHistory.getLast().equals(rightMove)) {
                                        now = manager.Move("Pawn", now);
                                        manager.UpdatePieces(indexPrevious, indexNow, tymczas);
                                        manager.pieces[pawnIndex] = new PlaceHolder();
                                        JLabel tym = (JLabel) mainBoard.boardSquares.getComponent(pawnIndex).getComponentAt(1, 1);
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
                                else if(mainBoard.Diagonal(vector) && len<2 && !Objects.equals(nowPiece.name, "PlaceHolder") && !Objects.equals(manager.pieces[indexNow].color, tymczas.color)){
                                    if(manager.CheckObstacle(indexPrevious,vector,2)){
                                        now = manager.Move("Pawn",now);
                                        tymczas.move +=1;
                                    }
                                    move += manager.UpdatePieces(indexPrevious,indexNow,tymczas);
                                }
                                break;
                            default:
                        }
                    }
                    right.ShowTurn(manager.turn);
                    right.AddToHistory(move);
                }

            }


            //System.out.println("manager.pieces [indexNow] "+ manager.pieces[indexNow]);
            //System.out.println("manager.pieces [indexPrevious] " + manager.pieces[indexPrevious]);



        }
        //manager.ShowBoard();

        manager.previous = now;


    }
}
