import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static java.lang.Math.abs;

public class PiecesManagment  {
    Piece[] pieces = new Piece[64];
    public JLabel previous;
    Board board;
    boolean turn=true;

    public PiecesManagment(Board Board){
        board=Board;


    }

    public boolean CheckCheck(int index){
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
            //possibleLen jest złe
            int indexNow=index;
            List checked = new List();
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
                ////System.out.println("index: "+index);
                JPanel tym =(JPanel) board.boardSquares.getComponent(indexNow);
                if(!Objects.equals(tym.getComponent(CheckNumberOfComponents(tym)).getName(), "PlaceHolder")){
                    //System.out.println("Znaleziono przeszkode");
                    if(pieces[indexNow].color!=pieces[index].color) return true;
                    break;
                }
                checked.add(String.valueOf(indexNow));
            }
        }

        int[][] vectors2 = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
        for (int[] ints : vectors2) {
            int indexNow=index+ints[0]*8+ints[1];
            if(indexNow>63 || indexNow<0 || (indexNow%8==0 && ints[1]<=-1) || (indexNow%8==7 && ints[1]>=1)) continue;
            if(pieces[indexNow].name=="Knight" && pieces[index].color!=pieces[indexNow].color){
                System.out.println(indexNow);
                return true;
            }
        }
        return false;
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

    public void UpdatePieces(int index1, int index2, Piece a){
        pieces[index1] = new Piece();
        pieces[index2] = a;
    }


    public void UpdatePossibilities(){
        
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
