
public class SystemLinks {

	public int ID;
	public int OrigSystemID;
	public int DestSystemID;
	public double TotalDistance;
	public int Interface;
	
	public SystemLinks(int OS, int DS, double TD, int If) {
		ID = 0;
		OrigSystemID = OS;
		DestSystemID = DS;
		TotalDistance = TD;
		Interface = If;
	}
}
