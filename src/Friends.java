// Helene Martin, CSE 143
// Prompts the user for two names and prints how distant of friends they are if they are connected.

import java.util.*;
import java.io.*;

public class Friends {
	public static void main(String[] args) throws FileNotFoundException {
		Map<String, Set<String>> friends = buildFriendMap("friends.dot");

		Scanner console = new Scanner(System.in);
		System.out.print("First person? ");
		String name1 = console.next();

		System.out.print("Second person? ");
		String name2 = console.next();
		System.out.println(friends);

		printLevelsBetween(name1, name2, friends);
	}

	// Builds a map of friendships based on a file with the specified name.
	// pre: a file named filename exists and is in valid Graphviz format
	public static Map<String, Set<String>> buildFriendMap(String filename)
			throws FileNotFoundException {
		Scanner input = new Scanner(new File(filename));

		Map<String, Set<String>> friends = new TreeMap<String, Set<String>>();

		while (input.hasNextLine()) {
			String line = input.nextLine();
			if (line.contains("--")) {
				Scanner lineData = new Scanner(line);
				String name1 = lineData.next();
				lineData.next(); // skip "--"
				String name2 = lineData.next();

				// friendships are symmetrical
				addTo(friends, name1, name2);
				addTo(friends, name2, name1);
			}
		}
		return friends;
	}

	// prints the friends at each level separating friend1 and friend2 in the
	// specified friendship graph.
	// pre: friends contains friend1
	public static void printLevelsBetween(String friend1, String friend2,
			Map<String, Set<String>> friends) {
		Set<String> currentFriends = new TreeSet<String>();
		int level = 0;
		currentFriends.add(friend1);

		Set<String> visited = new HashSet<String>();
		while (!currentFriends.contains(friend2) && !currentFriends.isEmpty()) {
			level++;
			// build the next level in a temporary set
			Set<String> nextLevel = new TreeSet<String>();
			for (String friend : currentFriends) {
				nextLevel.addAll(friends.get(friend));
			}

			visited.addAll(currentFriends);
			// a friend can't be in multiple levels
			nextLevel.removeAll(visited);

			currentFriends = nextLevel;
			System.out.println("\tDistance " + level + ": " + currentFriends);
		}

		if (!currentFriends.isEmpty()) {
			System.out.println(friend2 + " is " + level + " away from "
					+ friend1);
		} else {
			System.out.println(friend2 + " is not connected to " + friend1);
		}
	}

	// Adds name2 to name1's set of friends in the friends map.
	// pre: friends not null
	public static void addTo(Map<String, Set<String>> friends, String name1,
			String name2) {
		if (!friends.containsKey(name1)) {
			friends.put(name1, new TreeSet<String>());
		}
		Set<String> name1Friends = friends.get(name1);
		name1Friends.add(name2);
	}
}