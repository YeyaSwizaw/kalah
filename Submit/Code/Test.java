import java.util.Scanner;

public class Test {
	private static void pit(KalahGame game, AIBase firstPlayer, AIBase secondPlayer)
	{
		int player1Seeds = 0;
		int player2Seeds = 0;
		
		int player1Wins = 0;
		int player2Wins = 0;
		int draws = 0;
		
		for (int i = 0; i < 1000; i++) {
			System.out.print("Game " + (i + 1) + " -> ");
			
			game.reset((i % 2) == 0 ? KalahGame.PLAYER_1 : KalahGame.PLAYER_2);
			
			int result = game.playGame(firstPlayer, secondPlayer);
			
			if (result == 0) {
				System.out.print("Both players drew");
			} else if (result == 1) {
				System.out.print("Player 1 won");
			} else if (result == 2) {
				System.out.print("Player 2 won");
			}
			
			System.out.println("\t\tPlayer 1 got " + game.getSeeds(6) + " seed(s), Player 2 got " + game.getSeeds(13) + " seed(s)");
			
			player1Seeds += game.getSeeds(6);
			player2Seeds += game.getSeeds(13);
			
			switch (result) {
				case 0: draws++;	break;
				case 1: player1Wins++; firstPlayer.win(); secondPlayer.lose();	break;
				case 2: player2Wins++; firstPlayer.lose(); secondPlayer.win();	break;
			}
			
			//System.out.println(game);
		}
		
		System.out.println("\n---Results---");
		
		System.out.println("Total seeds Player 1 acquired: " + player1Seeds);
		System.out.println("Total seeds Player 2 acquired: " + player2Seeds);
		
		System.out.println("Player 1 won " + player1Wins + " game(s)");
		System.out.println("Player 2 won " + player2Wins + " game(s)");
		System.out.println("Both players drew " + draws + " time(s)");
	}

	public static void main(String[] args) 
	{
		KalahGame game = new KalahGame(KalahGame.PLAYER_1);
		
		AIBase firstPlayer = new ROCK(game, KalahGame.PLAYER_1, -1);
		AIBase secondPlayer = new MASH(game, KalahGame.PLAYER_2);
		
		pit(game, firstPlayer, secondPlayer);
	}
}
