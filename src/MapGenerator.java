import java.awt.Point;
import java.util.Random;

public class MapGenerator {

	static int numSystems = 20;
	static int MapSize = 20;
	static double minDistOrigin = 3;
	static double maxDistOrigin = 10;
	static double minDistSystem = 2.5;
	static double maxDistSystem = 5;
	static double maxDistLink = 4.5;
	static Point Origin = new Point(MapSize/2,MapSize/2);
	
	//Check for complete routing tables
	static boolean CompleteTables = false;
	
	//Array of Systems
	static Systems[] mySystems = new Systems[numSystems];
	
	//Routing Tables
	static SystemLinks[][] RoutingTables = new SystemLinks[numSystems][numSystems];
	
	private static void BuildMap() {
		int numTry;
		int numLoops = 1000;
		System.out.println("Generating Map...");
		for (int i = 0; i < numSystems; i++) {
			numTry = 0;
			mainLoop:
			while (numTry < numLoops) {
				Point TempSystem = new Point(GenerateSystem());
				if (ValidSystem(TempSystem, i)) {
					//Commit system to array
					mySystems[i] = new Systems(0, "New System", TempSystem, 0, 0, 0, 0);
					break mainLoop;
				}
				numTry++;
			}
			if (numTry == numLoops) {
				System.out.println("Map generation failed.");
				System.exit(0);
			}
		}
	}
	
	private static void DrawMap() {
		boolean EmptyPoint;
		System.out.println("Drawing Map...");
		for (int y = 0; y < MapSize; y++) {
			for (int x = 0; x < MapSize; x++) {
				EmptyPoint = true;
				Point pCheck = new Point(x,y);
				for (int z = 0; z < numSystems; z++){
					if (mySystems[z].Coords.equals(pCheck)) {
						System.out.print(String.format("%02d ", z));
						EmptyPoint = false;
					}
				}
				if (Origin.equals(pCheck)) {
					System.out.print(" . ");
				} else if (EmptyPoint) {
					System.out.print("   ");
				}
			}
			System.out.println();
		}
	}
	
	private static void BuildRoutingTables() {
		System.out.println("Generating Routing Tables...");
		//Start by building local tables for all systems
		for (int i = 0; i < numSystems; i++) {
			CalcLocalTable(i);
		}
		//Cycle through all systems until tables are complete
		while (!CompleteTables) {
			CompleteTables = true;
			for (int x = 0; x < numSystems; x++) {
				for (int y = 0; y < numSystems; y++) {
					//If !empty & neighbor
					if (RoutingTables[x][y] != null && RoutingTables[x][y].DestSystemID == RoutingTables[x][y].Interface) CollectNeighborTables(x, y);
					//If !link fail check
					if (RoutingTables[x][y] == null) {
						CompleteTables = false;
					}
				}
			}
		}
		CleanLocalTables();
	}
	
	private static void PrintRoutingTables() {
		System.out.println("Output Routing Tables...");
		for (int x = 0; x < numSystems; x++) {
			for (int y = 0; y < numSystems; y++) {
				SystemLinks curLink = RoutingTables[x][y];
				if (curLink != null) {
					System.out.println("OrigSystemID: " + curLink.OrigSystemID + " DestSystemID: " + curLink.DestSystemID + " Distance: " + curLink.TotalDistance + " Interface: " + curLink.Interface);
				}
			}
		}
	}
	
	private static double CalcDistance(Point A, Point B) {
		double Distance = (Math.sqrt((Math.pow(A.x - B.x, 2)) + (Math.pow(A.y - B.y, 2))));
		return Distance;
	}
	
	private static Point GenerateSystem() {
		Random generator = new Random();
		int x = generator.nextInt(MapSize);
		int y = generator.nextInt(MapSize);
		
		Point GeneratedSystem = new Point(x,y);
		
		return GeneratedSystem;
	}
	
	private static boolean ValidSystem(Point Sys, int Count) {
		//Calculate Distance To Origin
		double DistToOrigin = CalcDistance(Sys, Origin);
		//Check min distance to origin
		if (DistToOrigin < minDistOrigin) return false;
		//Check max distance to origin
		if (DistToOrigin > maxDistOrigin) return false;
		if (Count == 0) return true;
		//Check min distance to all systems
		for (int i = 0; i < Count; i++) {
			double DistToSystem = CalcDistance(Sys, mySystems[i].Coords);
			if (DistToSystem < minDistSystem) return false;
		}
		//Check max distance to any system
		for (int i = 0; i < Count; i++) {
			double DistToSystem = CalcDistance(Sys, mySystems[i].Coords);
			if (DistToSystem < maxDistSystem) return true;
		}
		return false;
	}
	
	private static void CalcLocalTable(int Sys) {
		double LinkDist;
		
		for (int i = 0; i < numSystems; i++) {
			if (Sys != i) {
				LinkDist = CalcDistance(mySystems[Sys].Coords, mySystems[i].Coords);
				if (LinkDist < maxDistLink) {
					//Add to SystemLinks
					RoutingTables[Sys][i] = new SystemLinks(Sys, i, LinkDist, i);
				}
			}
		}
	}
	
	private static void CleanLocalTables() {
		for (int i = 0; i < numSystems; i++) {
			RoutingTables[i][i] = null;
		}
	}
	
	private static void CollectNeighborTables(int Sys, int Neigh) {
		double SysDist = CalcDistance(mySystems[Sys].Coords, mySystems[Neigh].Coords);
		mainLoop:
		for (int i = 0; i < numSystems; i++) {
			//Check for null neighbor route
			if (RoutingTables[Neigh][i] == null) break mainLoop;
			if (RoutingTables[Sys][i] == null && RoutingTables[Neigh][i] != null) {
				//Empty route -> Set new route via neighbor
				double TempDist = SysDist + RoutingTables[Neigh][i].TotalDistance;
				RoutingTables[Sys][i] = new SystemLinks(Sys, i, TempDist, Neigh);
			} else if ((RoutingTables[Neigh][i].TotalDistance + SysDist) < RoutingTables[Sys][i].TotalDistance) {
				//Shorter route -> Set new TotalDistance and Interface
				RoutingTables[Sys][i].TotalDistance = (SysDist + RoutingTables[Neigh][i].TotalDistance);
				RoutingTables[Sys][i].Interface = Neigh;
			}
		}
	}
	

	public static void main(String[] args) {
		//Generate map
		BuildMap();
		
		//DrawMap
		DrawMap();
		
		//Build Routing Tables
		BuildRoutingTables();
		
		
		//Output Routing Tables
		PrintRoutingTables();
	}
}
