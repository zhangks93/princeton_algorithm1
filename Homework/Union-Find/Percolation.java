 import edu.princeton.cs.algs4.WeightedQuickUnionUF;
 
public class Percolation {
	
	private boolean[] op; // true=open while false=blocked
	private int side; // number of rows or columns
	private int numOp; // number of open sites
	private WeightedQuickUnionUF uf;
	private WeightedQuickUnionUF ufTop;
 
	public Percolation(int n) {
		
		if(n <= 0) throw new IllegalArgumentException("Input should be positif!\n");
		
		this.side = n;
		this.op = new boolean[n*n+2]; // with 2 virtual sites
		this.uf = new WeightedQuickUnionUF(n*n+2); 
		this.ufTop = new WeightedQuickUnionUF(n*n+1); // with only the upper virtual site
		
		for(int i=1; i<n*n+1; i++) op[i] = false;
		op[0] = op[n*n+1] = true;
		this.numOp = 0;
		
	}
	
	// both ROW and COL should be integer within 1~n
	private void checkBounds(int row, int col){
		if(row < 1 || row > this.side || col < 1 || col > this.side){
			throw new IllegalArgumentException("Index out of bounds!\n");
		}
	}
	
	// get position of sites in 3 arrays: op, uf.parent & uf.size
	private int getPosition(int row, int col){
		return (row - 1) * this.side + col; 
	}
	
	private void union(int aPos, int bPos, WeightedQuickUnionUF wq){
		if(!wq.connected(aPos, bPos)){
			wq.union(aPos, bPos);
		}
	}
	
	private boolean isOpen(int pos){
		return op[pos];
	}
	
	public void open(int row, int col) {
		
		checkBounds(row, col);	
		if(isOpen(row, col)) return;
		
		int pos = getPosition(row, col);
		op[pos] = true;
		numOp++;
		
		// positions of adjacent sites
		int rowPrev = pos - side, rowNext = pos + side,
				colPrev = pos - 1, colNext = pos + 1;
		
		// try connect the adjacent open sites
		if(row == 1){
			union(0, pos, uf);
			union(0, pos, ufTop);
		}else if(isOpen(rowPrev)){
			union(rowPrev, pos, uf);
			union(rowPrev, pos, ufTop);
		}
				
		if(row == side){
			union(side * side + 1, pos, uf);
		}else if(isOpen(rowNext)){
			union(rowNext, pos, uf);
			union(rowNext, pos, ufTop);
		}
		
		if(col != 1 && isOpen(colPrev)) {
			union(colPrev, pos, uf);
			union(colPrev, pos, ufTop);
		}
		
		if(col != side && isOpen(colNext)) {
			union(colNext, pos, uf);
			union(colNext, pos, ufTop);
		}
	}
	
	public boolean isOpen(int row, int col) {
		checkBounds(row, col);
		return isOpen(getPosition(row, col));
					
	}
	
	/**
	 * check for backwash with predetermined sites that have multiple percolating paths
	 * in this case ufTop should be used instead of uf
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isFull(int row, int col) {
		checkBounds(row, col);
		//return uf.connected(0, getPosition(row, col)); -> didn't pass the test! 
		return ufTop.connected(0, getPosition(row, col));
			
	}
	
	// should pass the timing check
	public int numberOfOpenSites(){
		return this.numOp;
	}
	
	public boolean percolates(){
		return uf.connected(0, side * side + 1);
	}
 
}
