import java.awt.Point;


public class Systems {
	
	public int ID;
	public String Name;
	public Point Coords = new Point(0,0);
	public int OwnerID;
	public int FleetsPresent;
	public int NumPlanets;
	public int Production;
	public boolean ChangedOwner;
	
	public Systems(int I, String N, Point C, int O, int F, int nP, int P) {
		ID = I;
		Name = N;
		Coords = C;
		OwnerID = O;
		FleetsPresent = F;
		NumPlanets = nP;
		Production = P;
		ChangedOwner = false;
	}
}
