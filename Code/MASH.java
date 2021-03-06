import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;

public class MASH extends AIBase
{
	private Map<GameState, ProbArray> memory;
	private ArrayList<StatePair> currentGame;
	private Random r;

	private static final int PROB_DELTA = 50;
	private static final int TREE_DEPTH = 4;

	MASH(KalahGame g, int playerID)
	{
		super(g, playerID);

		memory = new HashMap<GameState, ProbArray>();
		currentGame = new ArrayList<StatePair>();
		r = new Random();
		
	}

	public void win() {
		for(StatePair p : currentGame) {
			memory.get(p.state).updateProbability(p.choice, PROB_DELTA);
		}

		currentGame.clear(); 

	}

	public void lose() {
		for(StatePair p : currentGame) {
			memory.get(p.state).updateProbability(p.choice, -PROB_DELTA);
		}

		currentGame.clear(); 

	}

	public int makeMove()
	{
		int[] moves = game.getAllowedMoves(getPlayerID());
		GameState s = new GameState(moves, getPlayerID());
		
		Tree<KalahGame> t = new Tree<KalahGame>(null, game);
		if(!(memory.containsKey(s)))
		{
			createTree(0, t);

			ArrayList<IntPair> hvals = new ArrayList<IntPair>();
			for(int i = 0; i < t.getChildren().size(); i++) {
				hvals.add(new IntPair(i, max(t.getChildren().get(i))));
			}

			int[] probs = new int[hvals.size()];
			Collections.sort(hvals, new Comparator<IntPair>() 
			{
				public int compare(IntPair p1, IntPair p2) 
				{
					return p1.compareTo(p2);
				}
			});

			probs[hvals.get(0).i1] = hvals.get(0).i2 ^ 7;

			for(int i = 1; i < hvals.size() - 1; i++) 
			{
				probs[hvals.get(i).i1] = (hvals.get(i).i2);
			}

			memory.put(s, new ProbArray(probs));
		}

		int total = 0;
		ProbArray probs = memory.get(s);
		for(int i = 0; i < probs.getSize(); i++) 
		{
			total += probs.get(i);

		}

		int val = r.nextInt(total == 0 ? 1 : Math.abs(total));
		int accum = 0;
		int i = 0;
		for(; i < probs.getSize() ; i++)
		{
			accum += probs.get(i);
			if(val < accum) 
			{
				break;
			}
		}

		i = (i >= moves.length ? i - 1 : i);
		currentGame.add(new StatePair(s, i));
		
		return moves[i];
	}

	private void createTree(int num, Tree<KalahGame> parentGame) //depth of ~5 to keep it manageable
	{
		if(num < TREE_DEPTH) //depth
		{
			int[] moves = parentGame.getData().getAllowedMoves(parentGame.getData().getTurn());

			for(int i = 0; i < moves.length; i++)
			{
				createTree(num + 1, new Tree<KalahGame>(parentGame, parentGame.getData().getState(moves[i])));

			}

		}

	}

	private int max (Tree<KalahGame> tree)
	{
	   	if (tree.getChildren().size() == 0)
		{
			return heuristic(tree.getData());
		}

		int max = Integer.MIN_VALUE;

		for(int i = 0; i < tree.getChildren().size(); i++)
		{
			int score = min(tree.getChildren().get(i));
			if(score > max)
			{
				max = score;
			}
		}
		
		return max;
	}
	   
	private int min (Tree<KalahGame> tree)
	{
	   	if (tree.getChildren().size() == 0)
		{
			return -(heuristic(tree.getData()));
		}
		int min = Integer.MAX_VALUE;

		for(int i = 0; i < tree.getChildren().size(); i++)
		{
			int score = max(tree.getChildren().get(i));
			if(score < min)
			{
				min = score;
			}
		}
		
		return min;
	}

	private int heuristic(KalahGame g)
	{
		if(getPlayerID() == KalahGame.PLAYER_1) {
			return g.getSeeds(6) - g.getSeeds(13);

		} else {
			return g.getSeeds(13) - g.getSeeds(6);

		}

	}

private class GameState {
	private int[] availableMoves;
	private int player;

	public GameState(int[] availbleMoves, int player) {
		this.availableMoves = availableMoves;
		player = player;

	}

}

private class ProbArray {
	private int[] probs;

	public ProbArray(int[] probs) {
		this.probs = probs;

		//normalise();

	}

	public int getSize() {
		return probs.length;

	}

	public int get(int i) {
		return probs[i];

	}

	/*
	private void normalise() {
		double total = 0;
		for(double p : probs) {
			total += p;

		}

		double factor = 1.0 / total;
		for(int j = 0; j < probs.length; j++) {
			probs[j] *= factor;

		}

	}
	*/

	public void updateProbability(int i, int d) {
		probs[i] += d;

		//normalise();

	}
}

private class StatePair {
	public GameState state;
	public int choice;

	public StatePair(GameState state, int choice) {
		this.state = state;
		this.choice = choice;
	}

}

private class IntPair implements Comparable {
	public int i1, i2;

	public IntPair(int i1, int i2) {
		this.i1 = i1;
		this.i2 = i2;
	}

	public int compareTo(Object o) {
		IntPair other = (IntPair)o;
		if(i2 < other.i2) {
			return -1;

		} else if(i2 == other.i2) {
			return 0;

		} else {
			return 1;

		}
	}

}

}
