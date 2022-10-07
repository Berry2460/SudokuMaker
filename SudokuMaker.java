import java.io.*;
import java.util.Scanner;

public class SudokuMaker{
	public static void main(String[] args){
		Scanner scan=new Scanner(System.in);
		System.out.print("How many boards: ");
		int boards=scan.nextInt();
		System.out.print("How difficult (1-3): ");
		int diff=scan.nextInt();
		scan.close();
		if (diff > 3 || diff < 1){
			System.out.println("Invalid input, making Medium boards by default...");
			diff=2;
		}
		for (int i=0; i<boards; i++){
			int given=35;
			if (diff == 1){
				given=(int)(Math.random()*9+37);
			}
			else if (diff == 2){
				given=(int)(Math.random()*7+31);
			}
			else if (diff == 3){
				given=(int)(Math.random()*5+26);
			}
			make(given, diff, boards);
		}
	}
	public static void make(int given, int diff, int quantity){
		int[] board=new int[81];
		int dimensions=(int)Math.sqrt(board.length);
		Sudoku test=new Sudoku(board);
		//place
		for (int i=0; i<given; i++){
			int choose=(int)(Math.random()*81);
			int val=(int)(Math.random()*9+1);
			if (board[choose] == 0){
				board[choose]=val;
				test=new Sudoku(board);
				if (!test.solve()){
					board[choose]=0;
					i--;
				}
			}
			else{
				i--;
			}
		}
		try{
			String diffS="Medium";
			if (diff == 1){
				diffS="Easy";
			}
			else if (diff == 2){
				diffS="Medium";
			}
			else if (diff == 3){
				diffS="Hard";
			}
			PrintWriter write=new PrintWriter(new FileWriter(quantity+" "+diffS+" puzzles.txt", true));
			String out="";
			for (int a=0; a<=dimensions*3+3; a++){
				out+="-";
			}
			out+="\n";
			int index=0;
			for (int i=0; i<dimensions; i++){
				out+="| ";
				for (int j=0; j<dimensions; j++){
					if (board[index] != 0){
						out+=board[index]+" "; //append each point to out
					}
					else{
						out+="_ "; //append blank space if value is 0
					}
					index++;
					if (j%((int)Math.sqrt(dimensions)) == 2){
						out+="| ";
					}
					else{
						out+=" ";
					}
				}
				out=out.trim(); //gets rid of excess whitespace on the right edge
				out+="\n";
				//lines
				if (i%((int)Math.sqrt(dimensions)) == 2){
					for (int a=0; a<=dimensions*3+3; a++){
						out+="-";
					}
					out+="\n";
				}
				//bars
				else{
					out+="| ";
					for (int a=0; a<=dimensions; a++){
						if (a%((int)Math.sqrt(dimensions)) == 2){
						out+="  | ";
					}
						else{
							out+="   ";
						}
					}
					out=out.trim(); //gets rid of excess whitespace
					out+="\n";
				}
			}
			out+="\n\n\n\n\n\n\n";
			write.println(out);
			write.close();
		}
		catch(IOException e){}
	}
}

//-------------------------------- Sudoku class -------------------------------------

class Sudoku{
	private int[] board;
	private int dimensions;
	private int wrong;
	public Sudoku(int[] in){
		this.dimensions=(int)Math.sqrt(in.length);
		this.board=new int[this.dimensions*this.dimensions];
		//clone
		for (int i=0; i<this.board.length; i++){
			this.board[i]=in[i];
		}
	}

	//calls recursive solve method
	public boolean solve(){
		this.wrong=0;
		return recursiveSolve(0);
	}

	//recursively solve puzzle
	private boolean recursiveSolve(int position){
		boolean out=false;
		if (position <= -1){
			//solved
			out=true;
		}
		else if (this.board[position] != 0){
			//make correction if position isnt on a valid position
			position=this.getNextEmpty(position);
		}
		//find position solution
		if (!out && wrong <= this.board.length*this.dimensions){
			for (int i=this.board[position]+1; i<=this.dimensions; i++){
				//place value
				this.board[position]=i;
				//check if position is valid
				if (this.checkGrid(position) && this.checkRow(position) && this.checkCol(position)){
					//advance to next position
					out=this.recursiveSolve(this.getNextEmpty(position));
					if (out){
						//found final solution!
						break;
					}
				}
			}
			//backtrack
			if (!out){
				//erase value
				this.wrong++;
				this.board[position]=0;
			}
		}
		return out;
	}

	//returns next empty spot
	private int getNextEmpty(int index){
		//find empty spots
		for (int i=index; i<this.board.length; i++){ //uses an index to prevent unnecessary checks
			//check if spot is blank
			if (this.board[i] == 0){
				return i;
			}
		}
		//cannot find blank spot
		return -1;
	}

	//check 3x3 grid helper
	private boolean checkGrid(int index){
		boolean[] ok=new boolean[this.dimensions]; //store numbers found for duplicate checking
		//calculate grid dimensions for board size
		int grid=(int)Math.sqrt(this.dimensions);
		if (grid*grid != this.dimensions){ //check if board is an odd shape
			return false;
		}
		int x=index%this.dimensions;
		int y=index/this.dimensions;
		//set start and end points for grid
		int startX=x/grid;
		int startY=y/grid;
		int endY=startY*grid+grid;
		int endX=startX*grid+grid;
		//adjust grid
		if (endX > this.dimensions){
			endX=this.dimensions;
		}
		if (endY > this.dimensions){
			endY=this.dimensions;
		}
		//search within grid for identical values
		for (int i=startY*grid; i<endY; i++){
			for (int j=startX*grid; j<endX; j++){
				if (this.board[i*this.dimensions+j] > 0){
					if (ok[this.board[i*this.dimensions+j]-1]){
						return false; //duplicate found
					}else{
						ok[this.board[i*this.dimensions+j]-1]=true;
					}
				}
			}
		}
		return true;
	}

	//check row helper
	private boolean checkRow(int y){
		y=y/this.dimensions*this.dimensions;
		boolean[] ok=new boolean[this.dimensions]; //store numbers found for duplicate checking
		for (int i=0; i<this.dimensions; i++){
			if (this.board[y+i] > 0){ //go through the row of the given y position
				if (ok[this.board[y+i]-1]){
					return false; //duplicate found
				}else{
					ok[this.board[y+i]-1]=true;
				}
			}
		}
		return true;
	}

	//check column helper
	private boolean checkCol(int x){
		x%=this.dimensions;
		boolean[] ok=new boolean[this.dimensions]; //store numbers found for duplicate checking
		for (int i=0; i<this.dimensions; i++){
			if (this.board[i*this.dimensions+x] > 0){ // go through the column of the given x position
				if (ok[this.board[i*this.dimensions+x]-1]){
					return false; //duplicate found
				}else{
					ok[this.board[i*this.dimensions+x]-1]=true;
				}
			}
		}
		return true;
	}

	//print board to screen
	public String toString(){
		String out="";
		int index=0;
		for (int i=0; i<this.dimensions; i++){
			for (int j=0; j<this.dimensions; j++){
				if (this.board[index] != 0){
					out+=this.board[index]+" "; //append each point to out
				}
				else{
					out+="- "; //append blank space if value is 0
				}
				index++;
			}
			out=out.trim(); //gets rid of excess whitespace on the right edge
			out+="\n";
		}
		return out;
	}
}
