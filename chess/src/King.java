import javax.swing.*;
import java.util.Objects;

import static java.lang.Math.*;

public class King extends  Piece{
    public King(String color) {
        super("King", color, 100, "chess/pieces/King" + color.charAt(0) + ".png");
    }


    public void Move(){

    };

    /*
    @Override
    public String Move(int[] vector, PiecesManagment manager, JLabel now, JPanel Board) {
        JPanel nowSquare = (JPanel) now.getParent();
        JPanel previousSquare = (JPanel) manager.previous.getParent();
        double len = sqrt(pow(vector[0],2)+pow(vector[1],2));
        int indexNow = Board.getComponentZOrder(nowSquare); // Indeks w siatce
        int indexPrevious = Board.getComponentZOrder(previousSquare);
        int c;
        //System.out.println("vector" + vector[0] + vector[1]);
        //znalezienie rookindex
        if(vector[1]>0) {
            c = 2;
        }
        else c = -1;
        int rookIndex=indexPrevious+vector[1]+c;
        //System.out.format("King %b, %b, %b, %b, %b " , tymczas.firstMove , abs(vector[1])==2, manager.pieces[rookIndex].name=="Rook",  manager.pieces[ rookIndex].firstMove, !CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},rookIndex-indexPrevious));

        if(this.firstMove && abs(vector[1])==2 && Objects.equals(manager.pieces[rookIndex].name, "Rook") && manager.pieces[rookIndex].firstMove && !manager.CheckObstacle(indexPrevious,new int[]{vector[0],rookIndex-indexPrevious},abs(rookIndex-indexPrevious)-1)){
            now = manager.Move("King", now);
            System.out.println("roszada");
            //System.out.println("Panel components");
            for (int i=0;i<((JPanel) Board.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponentCount();i++){
                System.out.println(((JPanel) Board.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(i));
            }
            manager.previous=(JLabel) ((JPanel) Board.getComponent(rookIndex)).getComponent(1);
            now = (JLabel) ((JPanel) Board.getComponent(indexNow-vector[1]/abs(vector[1]))).getComponent(1);
            now = manager.Move("Rook", now);
            manager.turn=!manager.turn;
            move += "roszada";
        }
        if (len < 2 && ((abs(vector[0])==abs(vector[1]) || (vector[0]*vector[1]==0)) && !Objects.equals(manager.pieces[indexNow].color, this.color)) {
            now = manager.Move("King", now);
            move += manager.UpdatePieces(indexPrevious, indexNow, this);
        }
        return  move;
    }
    */

}
