public class Pawn extends  Piece{
    public String startingColumn;

    public Pawn(String color, String StartingColumn) {
        super("Pawn", color, 1, "chess/pieces/Pawn" + color.charAt(0) + ".png");
        this.startingColumn = StartingColumn;
    }
    @Override
    public void Move() {

    }
}
