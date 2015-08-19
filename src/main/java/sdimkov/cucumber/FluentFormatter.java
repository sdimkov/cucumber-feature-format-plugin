package sdimkov.cucumber;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluentFormatter
{
	private int blankLinesCount;
	private List<String> output;

	private final List<String> input;
	private final Path inputPath;

	private final Map<String, Integer> blankLineRules = new HashMap<>();
	private final Map<String, Integer> indentRules    = new HashMap<>();

	public FluentFormatter(File file) throws IOException {
		inputPath = file.toPath();
		input = Files.readAllLines(inputPath, Charset.defaultCharset());
	}

	public FluentFormatter setBlankLinesBefore(String firstWord, int lines) {
		if (firstWord.length() == 0)
			throw new IllegalArgumentException("firstWord can't be blank");
		blankLineRules.put(firstWord, lines);
		return this;
	}

	public FluentFormatter setIndent(String firstWord, int indent) {
		if (firstWord.length() == 0)
			throw new IllegalArgumentException("firstWord can't be blank");
		indentRules.put(firstWord, indent);
		return this;
	}

	public FluentFormatter format() {
		output = new ArrayList<>(input.size());

		for (String line : input) {
			// Extract first word of line
			String word = getFirstWord(line);

			if (blankLineRules.containsKey(word)) {
				applyBlankLines(blankLineRules.get(word));
			}
			if (indentRules.containsKey(word)) {
				applyIndent(line, indentRules.get(word));
			}
			else if ("".equals(word)) {
				blankLinesCount++;
				output.add("");
			}
			else {
				output.add(line);
			}
		}

		return this;
	}

	public FluentFormatter print(PrintStream stream) {
		for (String line : output)
			stream.println(line);
		return this;
	}

	public FluentFormatter print() {
		return print(System.out);
	}

	public FluentFormatter saveTo(Path filePath) throws IOException {
		Files.write(filePath, output, Charset.defaultCharset());
		return this;
	}

	public FluentFormatter save() throws IOException {
		return saveTo(inputPath);
	}

	private void applyIndent(String line, int indent) {
		String indentStr = new String(new char[indent]).replace('\0', ' ');
		output.add(indentStr + line.trim());
		blankLinesCount = 0;
	}

	private void applyBlankLines(int blankLines) {
		if (blankLines == -1) return;

		int diff = blankLines - this.blankLinesCount;

		if (diff > 0) {
			for (int i = 0; i < diff; i++)
				output.add("");
		}
		else if (diff < 0) {
			for (int i = diff; i < 0; i++)
				output.remove(output.size() - 1);
		}
	}

	private String getFirstWord(String line) {
		return line.trim().split(" ")[0];
	}
}
