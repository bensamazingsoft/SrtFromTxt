package srtfromtxt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Txt2Srt {

	/**
	 * set by user the max lenght of subs lines
	 */
	private static int charMax;

	public static int getCharMax() {
		return ((Integer) charMax);
	}

	public static void setCharMax(int charMax) {
		Txt2Srt.charMax = charMax;
	}

	/**
	 * txt file provided by user
	 */
	private static File file2Convert;

	public static File getFile2Convert() {
		return file2Convert;
	}

	public static void setFile2Convert(File file2Convert) {
		Txt2Srt.file2Convert = file2Convert;
	}

	/**
	 * destination path provided by user
	 */
	private static File destination;

	public static File getDestination() {
		return destination;
	}

	public static void setDestination(File destination) {
		Txt2Srt.destination = destination;
	}

	private static String inputText;

	public static String getInputText() {
		return inputText;
	}

	public static void setInputText(String inputText) {
		Txt2Srt.inputText = inputText;
	}

	public Txt2Srt() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * String the text out of the file
	 * 
	 * @param inFile
	 * @return
	 * @throws IOException
	 */
	public static String file2String(File inFile) throws IOException {

		String result;

		BufferedReader br = new BufferedReader(new FileReader(inFile));

		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				if (line.length() != 0) {
					sb.append(line);
					sb.append(" ");
				}
				line = br.readLine();
			}
			result = sb.toString();
		} finally {
			br.close();
		}
		return result;
	}

	/**
	 * return an arrayList of the index matches for the regex in a String
	 * 
	 * @param regex
	 * @param inString
	 * @return indexTab
	 */
	public static ArrayList<Integer> getRegIndex(String inString, String regex) {

		ArrayList<Integer> indexTab = new ArrayList<Integer>();
		Pattern phrasesEndPatt = Pattern.compile(regex);
		Matcher phrasesEndMatcher = phrasesEndPatt.matcher(inString);
		while (phrasesEndMatcher.find()) {
			indexTab.add(phrasesEndMatcher.start());
			// System.out.println(phrasesEndMatcher.group());
		}
		// System.out.println(inString);
		// System.out.println(indexTab.toString());

		return indexTab;
	}

	/**
	 * insert 'tag' in a string at indexes provided
	 * 
	 * @see Txt2Srt#getRegIndex(String, String)
	 * @param inString
	 * @param indexTab
	 * @param tag
	 * @return tagedString
	 */
	public static String addTags(String inString, ArrayList<Integer> indexTab, String tag) {

		int startId = 0;
		String tagedString = "";

		// must do to get the last sentence taged
		indexTab.add(inString.length() - 1);

		for (int endId : indexTab) {

			// +1 is to get to the " "
			tagedString += inString.substring(startId, endId + 1).trim() + tag;
			startId = endId + 1;
		}
		return tagedString;
	}

	/**
	 * add <BREAK> tags in 'phrases' (end with <END> tag) using algorithm based
	 * on user input maxChar integer
	 * 
	 * @param inString
	 * @return
	 */
	public static String addBreakTags(String inString) {

		String tagedStr = "";
		String[] strTab = inString.split("<END>");
		Pattern ponctuPatt = Pattern.compile("(,)|(;)|(:)");

		for (String phrase : strTab) {
			if (phrase.length() > getCharMax()) {
				ArrayList<Integer> idArr = new ArrayList<Integer>();
				int chunkCount = 0;
				int chunkSize = 0;
				int size = phrase.length();
				int delta = 0;
				Matcher matcher = ponctuPatt.matcher(phrase);
				while (size % getCharMax() != 0) {
					size++;
				}
				chunkCount = (size / getCharMax());
				chunkSize = phrase.length() / chunkCount;
				delta = (int) (chunkSize / 3);
				int[] id2Search = new int[chunkCount - 1];
				for (int i = 0; i < chunkCount - 1; i++) {
					id2Search[i] = (chunkSize - 1) * (i + 1);
				}
				for (int id : id2Search) {
					matcher.region(id - delta, id + delta);

					if (matcher.find()) {
						idArr.add(matcher.start());
					} else {
						idArr.add(phrase.indexOf(" ", id - delta));

					}

					matcher.reset();
				}
				phrase = addTags(phrase, idArr, "<BREAK>");
			}
			phrase += "<END>";
			tagedStr += phrase;
		}

		return tagedStr.trim();

	}

	/**
	 * add <END> tags at the end of inString sentences
	 * 
	 * @param inString
	 * @return
	 * @throws IOException
	 */
	public static String addEndTags(String inString) throws IOException {

		ArrayList<Integer> indexArr = getRegIndex(file2String(getFile2Convert()), "(\\.)(\\s)([A-Z0-9])");

		String stre = addTags(file2String(getFile2Convert()), indexArr, "<END>");

		return stre;
	}

	/**
	 * retrieve user input and creates the srt file
	 * 
	 * @param preview
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	public static void doIt(boolean preview) throws IOException {
		
		
		String outString = makeOutStr();

			
			if (!preview) {
				File srtFile = new File(
						getDestination() + "/" + getFile2Convert().getName().replaceFirst(".txt", "") + ".srt");
				if (srtFile.exists()) {
					int rep = new JOptionPane().showConfirmDialog(null, "File already exist, Overwrite ?", "Warning",
							JOptionPane.OK_CANCEL_OPTION);
					if (rep == JOptionPane.OK_OPTION) {
						if (srtFile.delete()) {
							makeFile(outString);
						} else {
							new JOptionPane().showMessageDialog(null, "Can't Overwrite. Sorry dude", "Warning",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					makeFile(outString);
				} 
			}
		}
	
	
	/**
	 * creates the String containing the subtitles
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	public static String makeOutStr() throws IOException {
		
		String inString = "";
		ArrayList<Sub> subArray = new ArrayList<Sub>();
		String outString = "";
		File file = getFile2Convert();

		if (file.exists()) {
			inString = file2String(file);
		} else {
			new JOptionPane().showMessageDialog(null, "File not Found", "Error", JOptionPane.ERROR_MESSAGE);
		}

		if (inString.length() != 0) {
			inString = addEndTags(inString);
			inString = addBreakTags(inString);

			String[] strTab = inString.split("<END>");

			for (String phr : strTab) {
				ArrayList<String> lineArray = new ArrayList<String>();
				String[] lineTab = phr.split("<BREAK>");
				for (String line : lineTab) {
					lineArray.add(line);
				}
				while (!lineArray.isEmpty()) {
					if (lineArray.get(0).endsWith(".")) {
						subArray.add(new Sub(lineArray.get(0)));
						lineArray.remove(0);
					} else {
						subArray.add(new Sub(lineArray.get(0), lineArray.get(1)));
						lineArray.remove(0);
						lineArray.remove(0);
					}
				}
			}

			for (Sub sub : subArray) {
				outString += sub.toString();
			}
		
	}
		return outString;
	}

	/**
	 * creates and writes the .srt file on disk
	 * 
	 * @param outString
	 * @throws IOException
	 */
	@SuppressWarnings("static-access")
	public static void makeFile(String outString) throws IOException {

		if (getDestination() != null) {

			File srtFile = new File(
					getDestination() + "/" + getFile2Convert().getName().replaceFirst(".txt", "") + ".srt");
			String separator = System.getProperty("line.separator");
			outString = outString.replace("\n", separator);
			FileWriter fOut = new FileWriter(srtFile, true);
			fOut.append(outString);
			fOut.flush();
			fOut.close();
			if (srtFile.exists()) {
				new JOptionPane().showMessageDialog(null, "Created " + srtFile.getName(), "Success",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}

	public static void openPreviewPnl() throws IOException {
		JFrame previewFrame = new JFrame();
		Preview previewPnl = new Preview();
		previewFrame.getContentPane().add(previewPnl);
		previewFrame.setTitle("Ben's Amazing SRT Maker - Preview");
		previewFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		previewFrame.setBounds(100, 100, 500, 500);
		previewFrame.setVisible(true);
		
		
	}
}
