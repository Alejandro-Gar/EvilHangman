/**
 * Kelly Keniston, CSE 143, Section BC, Andrew Repp
 * Manages a game of "Evil Hangman", where the program delays in choosing an actual
 * word until it has to.
 */

import java.util.*;

public class HangmanManager {
	private SortedSet<String> wordsLeft;
	private SortedSet<Character> guessedLetters;
	private int guessCount;
	private String wordPattern;

	/**
	 * Initializes the state of the game, taking all words of given length from the 
	 * dictionary of words passed in and setting the maximum guess number.
	 * @param dictionary: dictionary of words passed in
	 * @param length: length of word to be guessed
	 * @param max: maximum number of guesses the user can make in the game
	 * @exception: throws IllegalArgumentException if the passed length is less than 1
	 * or the max guess number is less than 0.
	 */
	public HangmanManager(List<String> dictionary, int length, int max) {
		if (length < 1 || max < 0) {
			throw new IllegalArgumentException();
		}
		wordsLeft = new TreeSet<String>();
		for (String word : dictionary) {
			if (word.length() == length) {
				wordsLeft.add(word);
			}
		}
		wordPattern = "";
		for (int i = 0; i < length; i++ ){
			wordPattern += "- ";
		}
		wordPattern.trim();
		guessedLetters = new TreeSet<Character>();
		guessCount = max;
	}
	
	/**
	 * Returns the manager's current set of words.
	 * @return: the set of words being considered by HangmanManager
	 */
	public Set<String> words() {
		return wordsLeft;
	}
	
	/**
	 * Returns how many guesses the player has left.
	 * @return: amount of guesses the player has left
	 */
	public int guessesLeft() {
		return guessCount - guessedLetters.size();
	}
	
	/**
	 * Returns the current set of letters that the user has guessed.
	 * @return: set of letters guessed by the user
	 */
	public SortedSet<Character> guesses() {
		return guessedLetters;
	}
	
	/**
	 * Returns the current pattern of the word, considering the guesses that haven't been made.
	 * @return: pattern with guesses that were made
	 */
	public String pattern() {
		return wordPattern;
	}

	/**
	 * Records the next guess made by the user and decides which set of words to use next.
	 * Returns the number of occurrences of the guessed letter, and updates the number 
	 * of guesses left.
	 * @param guess: guess made by the user
	 * @return: number of occurrences of guessed letter in the pattern
	 */
	public int record(char guess) {
		Map<String, SortedSet<String>> evilGuess = new TreeMap<String, SortedSet<String>>();
		createWordFamilies(evilGuess, guess);	
		return chooseWordFamily(evilGuess, guess);
	}
	
	/**
	 * Returns the string of the pattern of the current guessed letter in the word
	 * passed in.
	 * @param guess: letter guessed by the user
	 * @param word: word being considered from the set
	 * @return: pattern of the word with the guessed letter
	 */
	private String getString (char guess, String word) {
		String letterPosition = "";
		char[] letters = word.toCharArray();
		for (int i = 0; i < word.length(); i++) {
			if (letters[i] == guess) {
				letterPosition = letterPosition + guess + " ";
			} else {
				letterPosition += "- ";
			}
		}
		String currentWordPattern = letterPosition.trim();
		return currentWordPattern;
	}
	
	/**
	 * Merges the two patterns passed in, replacing any dashes in the pattern
	 * by letters that are concurrent in the other current pattern passed in, and
	 * decreases the number of guesses if the pattern was changed, leaves it if not.
	 * @param pattern: pattern to be updated
	 * @param currentPattern: pattern passed in by the word chosen
	 * @return: new pattern that combines both patterns passed in
	 */
	private String mergePatterns(String pattern, String currentPattern) {
		boolean changed = false;
		for (int i = 0; i < pattern.length() - 1; i += 2) {
			if (pattern.charAt(i) != currentPattern.charAt(i) && 
					!currentPattern.substring(i, i + 1).equals("-")) {
				pattern = pattern.substring(0, i) + currentPattern.charAt(i) + pattern.substring(i + 1);
				changed = true;
			}
		}
		if (changed) {
			guessCount++;
		}
		return pattern;
	}
	
	/**
	 * Creates all of the families of words with the same String pattern of letters considering
	 * the letter that was guessed.
	 * @param evilGuess: maps from the pattern of the word to all of the words that have the 
	 * given pattern
	 * @param guess: letter guess made by the user
	 */
	private void createWordFamilies(Map<String, SortedSet<String>> evilGuess, char guess) {
		for (String word : wordsLeft) {
			String letterPosition = getString(guess, word);		
			SortedSet<String> wordValue = new TreeSet<String>();	
			if (evilGuess.containsKey(letterPosition)) {
				wordValue = evilGuess.get(letterPosition);
			}
			wordValue.add(word);
			evilGuess.put(letterPosition, wordValue);
		}	
	}
	
	/**
	 * Chooses the word family set to continue the game with, choosing the family with the
	 * most words in it.
	 * Returns the amount of occurrences of the letter in the word family chosen and updates
	 * the pattern.
	 * @param evilGuess: maps from the pattern of the word to all of the words that have the 
	 * given pattern
	 * @param guess: letter guess made by the user
	 * @return: number of occurrences of the letter in the word family
	 */
	private int chooseWordFamily(Map<String, SortedSet<String>> evilGuess, char guess) {
		int maxWordCount = 0;
		String correctWordPattern = "";
		for (String currentWordPattern : evilGuess.keySet()) {
			if (evilGuess.get(currentWordPattern).size() > maxWordCount) {
				maxWordCount = evilGuess.get(currentWordPattern).size();
				correctWordPattern = currentWordPattern;
				wordsLeft = evilGuess.get(currentWordPattern);
			}
		}
		int letterCount = 0;
		for (int i = 0; i < correctWordPattern.length(); i++) {
			char current = correctWordPattern.charAt(i);
			if (current == guess) {
				letterCount++;				
			}
		}
		wordPattern = mergePatterns(wordPattern, correctWordPattern);
		guessedLetters.add(guess);
		return letterCount;
	}
}
