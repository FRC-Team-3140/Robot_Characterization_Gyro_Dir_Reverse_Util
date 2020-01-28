import java.io.*;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import parserJSON.JSONArray;
import parserJSON.JSONObject;
import parserJSON.JSONParser;
import parserJSON.ParseException;

public class GyroReverser extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	static private final String newline = "\n";
	JButton openButton, saveButton;
	JTextArea log;
	JFileChooser fcLoader;
	JFileChooser fcSaver;
	File file;
	JSONObject jsonObject;

	public GyroReverser() {
		super(new BorderLayout());

		// Create the log first, because the action listeners
		// need to refer to it.
		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		// Create a file chooser
		fcLoader = new JFileChooser();
		fcSaver = new JFileChooser();

		// Uncomment one of the following lines to try a different
		// file selection mode. The first allows just directories
		// to be selected (and, at least in the Java look and feel,
		// shown). The second allows both files and directories
		// to be selected. If you leave these lines commented out,
		// then the default mode (FILES_ONLY) will be used.
		//
		// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		fcLoader.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fcLoader.setFileFilter(new FileFilter() {
            public boolean accept(File file) {
               if (file.getName().endsWith(".json")) {
                  return true;
               }
               return false;
            }

			@Override
			public String getDescription() {
				return null;
			}
         });
		fcSaver.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		// Create the open button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).
		openButton = new JButton("Open a File...");
		openButton.addActionListener(this);

		// Create the save button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).
		saveButton = new JButton("Save a File...");
		saveButton.addActionListener(this);

		// For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(openButton);
		buttonPanel.add(saveButton);

		// Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {

		// Handle open button action.
		if (e.getSource() == openButton) {
			int returnVal = fcLoader.showOpenDialog(GyroReverser.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fcLoader.getSelectedFile();
				log.append("Opening: " + file.getName() + "." + newline);
				loadFile(file);
				log.append("File Opened.");
				log.append("Modifying: " + file.getName() + "." + newline);
				modifyFile();
				log.append("Modification Complete.");
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

			// Handle save button action.
		} else if (e.getSource() == saveButton) {
			int returnVal = fcSaver.showSaveDialog(GyroReverser.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String fileName = file.getName();
				int pos = fileName.lastIndexOf(".");
				if (pos > 0) {
					fileName = fileName.substring(0, pos);
				}
				
				log.append("Saving: " + fileName.concat("_GyroReversed.json") + "." + newline);
				saveFile(fcSaver.getSelectedFile());
				log.append("File Saved.");
			} else {
				log.append("Save command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = GyroReverser.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be invoked
	 * from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("FileChooserDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new GyroReverser());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}

	public void loadFile(File file) {
		JSONParser parser = new JSONParser();

		try (Reader reader = new FileReader(file)) {
			jsonObject = (JSONObject) parser.parse(reader);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void modifyFile() {
		if (jsonObject != null) {

			// loop array
			JSONArray slowForwards = (JSONArray) jsonObject.get("slow-forward");
			JSONArray slowBackwards = (JSONArray) jsonObject.get("slow-backwards");
			JSONArray fastForwards = (JSONArray) jsonObject.get("fast-forwards");
			JSONArray fastBackwards = (JSONArray) jsonObject.get("fast-backwards");
			JSONArray trackWidth = (JSONArray) jsonObject.get("track-width");

			//Iterator<String> iterator = msg.iterator();
			//while (iterator.hasNext()) {
				//System.out.println(iterator.next());
			//}

			//JSONObject objNew = new JSONObject();
			//objNew.put("name", name);
			//objNew.put("age", age);
			//objNew.put("messages", msg);
			
			//jsonObject = objNew;
		} else
			log.append("Error: Went to modify file, but file is null.");

	}

	public void saveFile(File directory) {
		String fileName = file.getName();
		int pos = fileName.lastIndexOf(".");
		if (pos > 0) {
			fileName = fileName.substring(0, pos);
		}
		
		String newName = directory + "\\" + fileName.concat("_GyroReversed.json");
		
		try (FileWriter file = new FileWriter(newName)) {
			file.write(jsonObject.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}