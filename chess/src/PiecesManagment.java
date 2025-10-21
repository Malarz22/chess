import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

import static java.lang.Math.*;

public class PiecesManagment  {
    Piece[] pieces = new Piece[64];
    public JLabel previous;
    Board board;
    boolean turn=true;

    public PiecesManagment(Board Board){
        board=Board;


    }
    //prevent discovering check
    // i need to check if after updating there is check if yes it isn't allowed


    public List<int[]> CheckCheck(int index){
        return CheckCheck(index,pieces[index].color);
    }


    public List<int[]> CheckCheck(int index, String color){
        // Najpierw wykonywany jest ruch, a dopiero potem sprawdzanie szacha - w ten sposób poprzednie położenie figury nie chroni jej przed dalszym szachem.
        // Aby tego dokonać, używamy metody UpdatePieces
        // Cofnięcie ruchu jest równoznaczne z odwróceniem indeksów w metodzie UpdatePieces
        
        List<int[]> checks = new ArrayList<>();
        int[] falseA = new int[]{0,1};
        checks.add(falseA);
        int possibleWays = 8;   
        int possibleLen = 8;
        int[][] vectors = {{0,1},{1,0},{0,-1},{-1,0},{1,1},{-1,-1},{-1,1},{1,-1}};
        //sprawdzanie szachu
        //trzeba rozważyć kilka przypadków:
        //       szach zwykły: figura po ruchu atakuje króla
        //       szach z odkrycia: figura za figurą ruszoną atakuje króla
        //       szach podwójny tego jeszcze nie ma!!!
        //       Sprawdzać czy król rusza się w szach
        for (int j = 0; j<possibleWays; j++)
        {
            int[] vector = vectors[j];
            //possibleLen jest złe
            int indexNow=index;
            for( int h = 0; h<possibleLen-1; h++) {
                int b = abs(vector[1]);
                int c = abs(vector[0]);
                //zapobieganie dzieleniu przez 0
                if(b==0) b=1;
                if(c==0) c=1;
                indexNow+=vector[0]/c*8+vector[1]/b;
                if(indexNow>63 || indexNow<0 || (indexNow%8==0 && vector[1]==-1) || (indexNow%8==7 && vector[1]==1)) {
                    //System.out.println("Koniec planszy");
                    break;
                }

                if(!pieces[indexNow].name.equals("EPlaceHolder")){
                    //System.out.println("Znaleziono przeszkode");
                    boolean isSameColor = pieces[indexNow].color.equals(color);
                    boolean isRookOrQueen = (pieces[indexNow].name.equals("Rook") || pieces[indexNow].name.equals("Queen"));
                    boolean isBishopOrQueen = pieces[indexNow].name.equals("Bishop") || pieces[indexNow].name.equals("Queen");
                    boolean isWhitePawn = pieces[indexNow].name.equals("Pawn") && pieces[indexNow].color.equals("White") && (indexNow-index==-7 || indexNow-index==-9);
                    boolean isBlackPawn = pieces[indexNow].name.equals("Pawn") && pieces[indexNow].color.equals("Black") && (indexNow-index==7 || indexNow-index==9);

                    if(!isSameColor && ((j<=3 && isRookOrQueen)||(j>3 && ( isBishopOrQueen || (isWhitePawn  || isBlackPawn))))) {
                        checks.add(new int[] {1,indexNow});
                        checks.remove(falseA);
                    }

                    break;
                }

            }
        }

        int[][] vectors2 = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] ints : vectors2) {
            int indexNow=index+ints[0]*8+ints[1];
            if(indexNow>63 || indexNow<0 || (indexNow%8==0 && ints[1]<=-1) || (indexNow%8==7 && ints[1]>=1)) continue;
            if(pieces[indexNow].name.equals("Knight") && !color.equals(pieces[indexNow].color)){
                System.out.println(indexNow);
                checks.remove(falseA);
                checks.add(new int[] {1,indexNow});
            }
        }

        return checks;
    }

    public void ShowBoard(){

        for (int i = 0; i < 8; i++) { // Loop through rows
            for (int j = 0; j < 8; j++) { // Loop through columns
                int index = i * 8 + j; // Convert 2D index to 1D
                if (pieces[index] != null) {
                    System.out.print(pieces[index].name.charAt(0) + "  ");
                } else {
                    System.out.print("0  "); // Empty spaces as '0'
                }
            }
            System.out.println(); // New line after each row
        }
        System.out.println();
    }

    public List<int[]> PreventCheck(int kingIndex, int checkingPieceIndex){
        List<int[]> possibleMoves = new ArrayList<>();
        Piece a = pieces[kingIndex];
        //can move pieces only if they prevent check or avoid check (King)
        //I must find those moves and then check if the move that player whats to do is one of them
        //how to find this moves?
        //I have index from witch is the check
        //then I have to check every square between the king and checking piece
        int kingRow = kingIndex/8;
        int kingCol = kingIndex%8;
        int possibleWays=8;
        int checkingPieceRow = checkingPieceIndex/8;
        int checkingPieceCol = checkingPieceIndex%8;
        int[] vector = {checkingPieceRow-kingRow,checkingPieceCol-kingCol};
        int len = (int)sqrt(pow(vector[0],2)+pow(vector[1],2));
        int[][] vectors = {{0,1},{1,0},{0,-1},{-1,0},{1,1},{-1,1},{1,-1},{-1,-1}};
        int tymIndex = kingIndex;
        int b = abs(vector[1]);
        int c = abs(vector[0]);
        //zapobieganie dzieleniu przez 0
        if(b==0) b=1;
        if(c==0) c=1;
        pieces[kingIndex]=new PlaceHolder();
        for(int[] i : vectors){
            int index = kingIndex+i[0]*8+i[1];
            if(index>63 || index<0 || (index%8==0 && vector[1]==-1) || (index%8==7 && vector[1]==1)) {
                continue;
            }

            if(!Objects.equals(pieces[index].color, a.color) && CheckCheck(index, a.color).getFirst()[0]==0){
                possibleMoves.add(new int[]{index,kingIndex});
            }

        }
        pieces[kingIndex]=a;
        if(pieces[checkingPieceIndex].name.equals("Knight")) {

            for (int j = 0; j < possibleWays; j++) {
                int[] vector1 = vectors[j];
                //possibleLen jest złe
                int indexNow = checkingPieceIndex;
                for (int h = 0; h < 8; h++) {
                    int d = abs(vector1[1]);
                    int e = abs(vector1[0]);
                    //zapobieganie dzieleniu przez 0
                    if (d == 0) d = 1;
                    if (e == 0) e = 1;
                    indexNow += vector1[0] / e * 8 + vector1[1] / d;
                    if (indexNow > 63 || indexNow < 0 || (indexNow % 8 == 0 && vector[1] == -1) || (indexNow % 8 == 7 && vector[1] == 1)) {
                        //System.out.println("Koniec planszy");
                        break;
                    }
                    if (!pieces[indexNow].name.equals("EPlaceHolder")) {
                        //System.out.println("Znaleziono przeszkode");
                        boolean isSameColor = pieces[kingIndex].color.equals(pieces[indexNow].color);
                        boolean isSameIndex = kingIndex == indexNow;
                        boolean isStraightDirection = j <= 3;
                        boolean isDiagonalDirection = j > 3;

                        boolean isRookOrQueen = pieces[indexNow].name.equals("Rook") || pieces[indexNow].name.equals("Queen");
                        boolean isBishopOrQueen = pieces[indexNow].name.equals("Bishop") || pieces[indexNow].name.equals("Queen");

                        boolean isWhitePawn = pieces[indexNow].name.equals("Pawn") && pieces[indexNow].color.equals("White") && (indexNow-checkingPieceIndex==-7 || indexNow-checkingPieceIndex==-9);
                        boolean isBlackPawn = pieces[indexNow].name.equals("Pawn") && pieces[indexNow].color.equals("Black") && (indexNow-checkingPieceIndex==7 || indexNow-checkingPieceIndex==9);
                        boolean isValidStraightPiece = isStraightDirection && isRookOrQueen;
                        boolean isValidDiagonalPiece = isDiagonalDirection && isBishopOrQueen;
                        if (isSameColor && !isSameIndex && (isValidStraightPiece || ( isValidDiagonalPiece || (isWhitePawn  || isBlackPawn)))){
                            possibleMoves.add(new int[]{checkingPieceIndex, indexNow});
                        }
                        break;
                    }
                }
            }
            return possibleMoves;
        }
        //for loop to check if something can move to the square between the piece and the king
        for (int i = len; i>0; i--){
            tymIndex+=vector[0]/c*8+vector[1]/b;
            for (int j = 0; j<possibleWays; j++)
            {
                int[] vector1 = vectors[j];
                //possibleLen jest złe
                int indexNow=tymIndex;
                for( int h = 0; h<8; h++) {
                    int d = abs(vector1[1]);
                    int e = abs(vector1[0]);
                    //zapobieganie dzieleniu przez 0
                    if(d==0) d=1;
                    if(e==0) e=1;
                    indexNow+=vector1[0]/e*8+vector1[1]/d;
                    if(indexNow>63 || indexNow<0 || (indexNow%8==0 && vector[1]==-1) || (indexNow%8==7 && vector[1]==1)) {
                        //System.out.println("Koniec planszy");
                        break;
                    }
                    JPanel tym =(JPanel) board.boardSquares.getComponent(indexNow);
                    if(!Objects.equals(tym.getComponent(CheckNumberOfComponents(tym)).getName(), "PlaceHolder")){
                        //System.out.println("Znaleziono przeszkode");
                        // w tym ifie jest błąd
                        boolean isSameColor = pieces[kingIndex].color.equals(pieces[indexNow].color);
                        boolean isSameIndex = kingIndex == indexNow;
                        boolean isStraightDirection = j <= 3;
                        boolean isDiagonalDirection = j > 3;

                        boolean isRookOrQueen = pieces[indexNow].name.equals("Rook") || pieces[indexNow].name.equals("Queen");
                        boolean isBishopOrQueen = pieces[indexNow].name.equals("Bishop") || pieces[indexNow].name.equals("Queen");

                        boolean isPawnAttackingDiagonally = pieces[indexNow].name.equals("Pawn") &&
                                (Math.abs(tymIndex - checkingPieceIndex) % 7 == 0 ||
                                 Math.abs(tymIndex - checkingPieceIndex) % 9 == 0);

                        boolean isValidStraightPiece = isStraightDirection && (isRookOrQueen || isPawnAttackingDiagonally);
                        boolean isValidDiagonalPiece = isDiagonalDirection && isBishopOrQueen;

                        if(isSameColor && !isSameIndex && (isValidDiagonalPiece || isValidStraightPiece)){
                            possibleMoves.add(new int[]{tymIndex,indexNow});
                        }
                        break;
                    }
                }
            }

            int[][] vectors2 = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
            for (int[] ints : vectors2) {
                int indexNow=tymIndex+ints[0]*8+ints[1];
                if(indexNow>63 || indexNow<0 || (indexNow%8==0 && ints[1]<=-1) || (indexNow%8==7 && ints[1]>=1)) continue;
                if(pieces[indexNow].name.equals("Knight") && pieces[kingIndex].color.equals(pieces[indexNow].color)){
                    possibleMoves.add(new int[]{tymIndex,indexNow});
                }
            }
        }
        //for loop to add King moves

        return possibleMoves;
    }

    public boolean IsAllowedToMove(int kingIndex, int indexTo, int indexFrom){
        Piece taken = pieces[indexTo];
        pieces[indexTo] = pieces[indexFrom];
        pieces[indexFrom] = new PlaceHolder();
        java.util.List<int[]> check = CheckCheck(kingIndex);
        pieces[indexFrom] = pieces[indexTo];
        pieces[indexTo] = taken;
        if(check.getFirst()[0]==1) {
            return false;
        }
        return true;
    }

    public JLabel Move(String name, JLabel now){
        ImageIcon piece = (ImageIcon) previous.getIcon();
        previous.setIcon(new ImageIcon("chess/pieces/placehHolder.png"));
        now.setIcon(piece);
        previous.setName("PlaceHolder");

        now.setName(name);
        turn=!turn;

        return now;
    }

    public String UpdatePieces(int index1, int index2, Piece a){
        return UpdatePieces(index1,index2,a,pieces);
    }


    public String UpdatePieces(int index1, int index2, Piece a, Piece[] pieces1){
        String move = pieces1[index1].name.substring(0,1);
        pieces1[index1] = new PlaceHolder();
        if(!pieces1[index2].name.equals("EPlaceHolder")){
            move+="x";
        }
        move += board.GetCoordinates(index1);
        move += board.GetCoordinates(index2);
        pieces1[index2] = a;
        return move;
    }

    public boolean CheckObstacle(int index ,int[] vector, int len){
        System.out.println("Start of function");
        for( int h = 0; h<len-1; h++) {
            int b = abs(vector[1]);
            int c = abs(vector[0]);
            //zapobieganie dzieleniu przez 0
            if(b==0) b=1;
            if(c==0) c=1;
            index+=vector[0]/c*8+vector[1]/b;
            //System.out.println("index: "+index);
            JPanel tym =(JPanel) board.boardSquares.getComponent(index);
            int d =0;
            if(tym.getComponentCount()>1) d=1;
            if(!Objects.equals(tym.getComponent(d).getName(), "PlaceHolder")){
                System.out.println("Znaleziono przeszkode" + tym.getComponent(d).getName() + " index " + index);
                return true;
            }

        }
        return false;
    }

    private int CheckNumberOfComponents(JPanel a){
        if(a.getComponentCount()>1)return 1;
        return 0;
    }
}
