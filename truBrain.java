//made utilizing source code from Gridworld, collegeboard test case
//John Shueh
package info.gridworld.actor;

import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;

public class truBrain extends CritterBrain{
	private int age = 0;
	private final ArrayList<Integer> manuverLeft = new ArrayList<Integer>();
	private final ArrayList<Integer> manuverRight = new ArrayList<Integer>();
	private ArrayList<Integer> moveQueue = new ArrayList<Integer>();
	private boolean foreignSmartCritterNeighbor;
	ActorImage frontActor;
	String frontActorName;
	ActorImage leftActor;
	String leftActorName = "";
	public truBrain(){
		manuverLeft.add(new Integer(truBrain.TURNLEFT));
		manuverLeft.add(new Integer(truBrain.MOVE));
		manuverLeft.add(new Integer(truBrain.TURNRIGHT));
		manuverLeft.add(new Integer(truBrain.MOVE));
		manuverRight.add(new Integer(truBrain.TURNRIGHT));
		manuverRight.add(new Integer(truBrain.MOVE));
		manuverRight.add(new Integer(truBrain.TURNLEFT));
		manuverRight.add(new Integer(truBrain.MOVE));
	}
	public int whatToDo(Location location, int direction, Grid<ActorImage> grid, int Strength) {
		age++;
		ArrayList<Location> actorLoc = grid.getOccupiedLocations();
		ArrayList<ActorImage> neighbors = grid.getNeighbors(location);
		actorLoc.remove(location);
		foreignSmartCritterNeighbor = false;

		for(ActorImage a:neighbors){
			if(a.getClassName().equals("SmartCritter")){
				if(!a.getBrainType().equals(this)||a.getBrainType().equals(null)){
					foreignSmartCritterNeighbor = true;
				}
			}
		}
		if(otherBrainTypes(actorLoc,grid)&&foreignSmartCritterNeighbor){
			for(ActorImage a : neighbors){
				if(a.getClassName().equals("SmartCritter")){
					if(!a.getBrainType().equals(this)){
						if(a.getStrength()>Strength){
							return truBrain.MOVE;
						}
					}
				}
			}
		}		

		for(ActorImage a: neighbors){
			String name = a.getClassName();
			if(name.equals("Bug")){
				return truBrain.EAT;
			}else if(name.equals("SmartCritter")){
				int str = a.getStrength();
				if(str<Strength&&!a.getBrainType().equals(this)) {
					return truBrain.EAT;
				}
			}
		}
		ArrayList<Location> wormholes = new ArrayList<Location>();
		for(Location a: actorLoc){
			if(grid.get(a).getClassName().equals("Wormhole")){
				wormholes.add(a);
			}
		}		
		if(wormholes.size()>0&&Strength<3){
			return moveTowards(location,targetActor(location,wormholes,grid,Strength,3),direction,grid);
		}		

		if(weakerBrainTypes(actorLoc,grid,Strength)&&!foreignSmartCritterNeighbor&&Strength>2){
			return moveTowards(location,targetActor(location,actorLoc,grid,Strength,1),direction,grid);
		}		

		if(Strength>10&&neighbors.size()!=8 || age>98 || !otherBrainTypes(actorLoc,grid)&&Strength>4){
			if(breedAssist(location,direction,grid)){
				return truBrain.BREED;
			}else{
				return truBrain.TURNRIGHT;
			}
		}
		if(bugsPresent(actorLoc,grid)){
			return moveTowards(location,targetActor(location,actorLoc,grid,Strength,2),direction,grid);
		}	


		return truBrain.DONOTHING;
	}

	private boolean breedAssist(Location loc, int direction,Grid<ActorImage> grid) {
		Location breedSpot = loc.getAdjacentLocation(direction + 180);
		if (grid.isValid(breedSpot) && grid.get(breedSpot) == null) {
			return true;
		} else {
			return false;
		}
	}

	public Color color() {
		return Color.cyan;
	}

	public String name() {
		return "truBrain";
	}

	public CritterBrain newBrain(){
		return new truBrain();
	}
	private Location targetActor(Location currentLoc,ArrayList<Location> actors, Grid<ActorImage> grid, int str, int type) {
		ArrayList<Location> target = new ArrayList<Location>();
		if(type == 1){
			for(Location a: actors){
				if(grid.get(a).getClassName().equals("SmartCritter")){
					if(!grid.get(a).getBrainType().equals(this)){
						if(grid.get(a).getStrength()+2<str){
							target.add(a);
						}
					}
				}
			}
		}else if(type == 2){
			for(Location a: actors){
				if(grid.get(a).getClassName().equals("Bug")){
					target.add(a);
				}
			}
		}else{
			for(Location a: actors){
				if(grid.get(a).getClassName().equals("Wormhole")){
					target.add(a);
				}
			}
		}
		Location targetLoc = new Location(999,999);
		int currentX = currentLoc.getRow(), currentY = currentLoc.getCol();
		int locX, locY,targX,targY,total1,total2,newX,newY;
		for(Location loc: target){
			locX = loc.getRow();
			locY = loc.getCol();
			targX = currentX - targetLoc.getRow();
			targY = currentY - targetLoc.getCol();
			if(targX < 0){
				targX*=-1;
			}
			if(targY < 0){
				targY*=-1;
			}
			total1 = targX + targY - 1;
			newX = currentX - locX;
			newY = currentY - locY;
			if(newX < 0){
				newX*=-1;
			}
			if(newY < 0){
				newY*=-1;
			}
			total2 = newX + newY - 1;
			if(total2<total1){
				targetLoc = loc;
			}
		}
		return targetLoc;
	}

	private boolean otherBrainTypes(ArrayList<Location> locList,Grid<ActorImage> grid){
		ArrayList<Location> finalList = new ArrayList<Location>();
		for(Location a : locList){
			if(grid.get(a).getClassName().equals("SmartCritter")){
				if(!grid.get(a).getBrainType().equals("truBrain")){
					finalList.add(a);
				}
			}
		}
		return (!(finalList.size()==0));
	}

	private boolean bugsPresent(ArrayList<Location> locList,Grid<ActorImage> grid){
		ArrayList<Location> finalList = new ArrayList<Location>();
		for(Location a : locList){
			if(grid.get(a).getClassName().equals("BreedingBug")||grid.get(a).getClassName().equals("Bug")){
				finalList.add(a);
			}
		}
		return (!(finalList.size()==0));
	}

	private int moveTowards(Location currentLoc, Location targLoc, int direction,Grid<ActorImage> grid){
		int directionTo = currentLoc.getDirectionToward(targLoc);
		Location frontLoc = currentLoc.getAdjacentLocation(direction);
		if(moveQueue.size()!=0){
			int action = moveQueue.get(0);
			moveQueue.remove(0);
			if(action==truBrain.MOVE){
				if(grid.isValid(frontLoc)){
					if(grid.get(frontLoc)==null){
						return truBrain.MOVE;
					}
				}else{
					moveQueue.add(0, new Integer(truBrain.TURNRIGHT));
					moveQueue.add(0, new Integer(truBrain.TURNRIGHT));
					moveQueue.add(0, new Integer(truBrain.TURNRIGHT));
				}
			}
			return action;
		}
		if(direction>directionTo){
			return truBrain.TURNLEFT;
		}else if(direction<directionTo){
			return truBrain.TURNRIGHT;
		}else{
			if(grid.isValid(frontLoc)){
				if(grid.get(frontLoc)==null){
					return truBrain.MOVE;
				}else{
					frontActor = grid.get(frontLoc);
					frontActorName = frontActor.getClassName();
				}
			}
			if(!(frontActorName == null)){
				if(frontActorName.equals("Rock")||frontActorName.equals("Wormhole")){
					if(grid.isValid(currentLoc.getAdjacentLocation(direction-45))){
						leftActor = grid.get(currentLoc.getAdjacentLocation(direction-45));
						if(leftActor!=null){
							leftActorName = leftActor.getClassName();
						}
						if(!leftActorName.equals(null)||!leftActorName.equals("Rock")){
							moveQueue.removeAll(moveQueue);
							moveQueue.addAll(manuverLeft);
						}else{
							moveQueue.removeAll(moveQueue);
							moveQueue.addAll(manuverRight);
						}
					}
				}
			}
		}
		return truBrain.MOVE;
	}
	private boolean weakerBrainTypes(ArrayList<Location> locList,Grid<ActorImage> grid, int str){
		ArrayList<Location> finalList = new ArrayList<Location>();
		for(Location a : locList){
			if(grid.get(a).getClassName().equals("SmartCritter")){
				if(!grid.get(a).getBrainType().equals("truBrain")||grid.get(a).getBrainType().equals(null)){
					if(grid.get(a).getStrength()<str){
						finalList.add(a);
					}
				}
			}
		}
		return (!(finalList.size()==0));
	}
}
