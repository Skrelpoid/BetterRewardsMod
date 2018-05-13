package skrelpoid.betterrewards;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.screens.stats.RunData;

public class RunHistory {

	public static final Logger logger = LogManager.getLogger(RunHistory.class.getName());

	private Gson gson;
	private HashMap<String, ArrayList<RunData>> runsByCharacter;

	public RunHistory() {
		runsByCharacter = new HashMap<String, ArrayList<RunData>>();
		gson = new Gson();
	}

	// Partially copied from RunHistoryScreen.refreshData()
	public void refreshData() {
		logger.info("Refreshing run data");
		FileHandle[] subfolders = Gdx.files.local("runs" + File.separator).list();
		runsByCharacter.clear();

		for (FileHandle subFolder : subfolders) {
			for (FileHandle file : subFolder.list()) {
				try {
					RunData data = gson.fromJson(file.readString(), RunData.class);
					if ((data != null) && (data.timestamp == null)) {

						data.timestamp = file.nameWithoutExtension();

						String exampleDaysSinceUnixStr = "17586";
						boolean assumeDaysSinceUnix = data.timestamp.length() == exampleDaysSinceUnixStr.length();
						if (assumeDaysSinceUnix) {
							try {
								long secondsInDay = 86400L;
								long days = Long.parseLong(data.timestamp);
								data.timestamp = Long.toString(days * secondsInDay);
							} catch (NumberFormatException ex) {
								logger.info(
										"Run file " + file.path() + " name is could not be parsed into a Timestamp.");
								data = null;
							}
						}
					}

					if (data != null) {
						try {
							AbstractPlayer.PlayerClass.valueOf(data.character_chosen);
							if (!runsByCharacter.containsKey(data.character_chosen)) {
								runsByCharacter.put(data.character_chosen, new ArrayList<RunData>());
							}
							runsByCharacter.get(data.character_chosen).add(data);
						} catch (IllegalArgumentException ex) {
							logger.info("Run file " + file.path() + " does not use a real character: "
									+ data.character_chosen);
						}
					}
				} catch (JsonSyntaxException ex) {
					logger.info("Failed to load RunData from JSON file: " + file.path());
				}
			}
		}

		for (Entry<String, ArrayList<RunData>> entry : runsByCharacter.entrySet()) {
			entry.getValue().sort(RunData.orderByTimestampDesc);
			logger.info(entry.getKey() + ": " + entry.getValue().size() + " runs found");
		}
	}

	public RunData getLastRunByCharacter(String characterString) {
		ArrayList<RunData> runs = runsByCharacter.get(characterString);
		if (runs == null || runs.isEmpty()) {
			return null;
		} else {
			return runs.get(0);
		}
	}
}
