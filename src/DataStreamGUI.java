import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class DataStreamGUI extends JFrame {
    private JPanel searchPanel;
    private JPanel textAreaPanel;
    private JPanel buttonPanel;

    //TextFields and TextAreas
    private JTextField searchTF;
    private JTextArea originalFileTA;
    private JTextArea filteredFileTA;

    //Buttons
    private JButton btnSearchFile;

    //JFileChooser and Files
    private JFileChooser fileChooser;
    private File selectedFile;

    private String bookContent;


    public DataStreamGUI(){
        //Creating the main Panel that will contain 3 sections:
        // Textfield and Seach File Button Section
        // 2 TextAreas Section
        //2 Buttons Section

        JPanel mainPanel = new JPanel(new BorderLayout());

        //Adding TextField Panel to the main Panel
        createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        //Adding TextArea Panel to the main Panel
        createTextAreaPanel();
        mainPanel.add(textAreaPanel, BorderLayout.CENTER);

        //Adding Button Panel to the main Panel
        createButtonsPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        //Instantiating JFileChooser so we can use it in our methods (has a global scope since we declared it on top and
        //outside of the DataStreamGUI constructor.
        fileChooser = new JFileChooser();

        //Frame set up
        this.setTitle("Data Stream App");
        this.setSize(1210, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(mainPanel);
        this.setVisible(true);//Decided to setVisible here instead of doing it in the Main class.
    }

    private void createSearchPanel(){
         searchPanel = new JPanel();

         searchTF = new JTextField(20);

         btnSearchFile = new JButton("Search File");
         btnSearchFile.setFocusable(false);
         btnSearchFile.setEnabled(false);//We Want this enabled after the File is loaded to the left TextArea!
         btnSearchFile.addActionListener((ActionEvent ae) -> filterSelectedFile());

        searchPanel.add(searchTF);
        searchPanel.add(btnSearchFile);
    }



    private void createTextAreaPanel(){
        textAreaPanel = new JPanel(new GridLayout(1, 2));

        originalFileTA = new JTextArea(10, 10);
        filteredFileTA = new JTextArea(20, 15);

        JScrollPane scrollPane = new JScrollPane(originalFileTA);
        JScrollPane scrollPane2 = new JScrollPane(filteredFileTA);

        textAreaPanel.add(scrollPane);
        textAreaPanel.add(scrollPane2);
    }

    private void createButtonsPanel(){
        buttonPanel = new JPanel();

        JButton btnLoadFile = new JButton("Load File");
        btnLoadFile.setFocusable(false);
        btnLoadFile.addActionListener((ActionEvent) -> chooseFileDialogBox());

        JButton btnQuit = new JButton("Quit");
        btnQuit.setFocusable(false);
        btnQuit.addActionListener((ActionEvent) -> System.exit(0));

        buttonPanel.add(btnLoadFile);
        buttonPanel.add(btnQuit);

    }

    private void chooseFileDialogBox() {
        File homeDirectory = new File(System.getProperty("user.dir"));//Setting the directory to our intelliJ folder
        fileChooser.setCurrentDirectory(homeDirectory);
        fileChooser.setDialogTitle("Select a Text File");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            loadSelectedFile();
            originalFileTA.setText(bookContent);//This displays the book to the originalFileTA (bookContent variable value is set in the loadSelectedFile() method
            btnSearchFile.setEnabled(true);


            //Get the file read and displayed to the oringalTextArea.

        } else {
            JOptionPane.showMessageDialog(null, "No file selected!");
        }
    }

    private void loadSelectedFile() {
        StringBuilder content = new StringBuilder();// This does what String word += "Random Strings."

        try(BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) { //BufferReader loads the selected file
                                                                        //to reader variable.
            String line; //This String variable will hold the "reader's" content
            while((line = reader.readLine()) != null){// reader.readLine() reads one line at a time. This is why we need to a
                                                //while loop to traverse through the whole file until there nothing to read (null)
                content.append("\t" + line + "\n");// Adds every single line read to the String builder until the file is read completely.
                                                    //Added a tab "\t" at the beginning of every new line to readability
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bookContent = content.toString();//This String builder need to be set toString for it work. This "content" is
                                    // the entirety of the book file.

    }
    private void filterSelectedFile() {
        String searchTerm = searchTF.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.");
            return;
        }

        try {
            String filteredContent = Files.lines(Paths.get(selectedFile.getPath()))// lines() gets all of the content from the selectedFile as a Stream of Strings (each String is a line)
                    .filter(line -> line.toLowerCase().contains(searchTerm.toLowerCase()))// We turn each line to lowercase and check if it contains the lowercase searchTerm
                                                                                // If it does (contains = true), the line is kept in the stream; if not, it's filtered out

                    .collect(Collectors.joining("\n"));// collect() is a terminal operation that processes the stream and produces a result
                                                                // Collectors.joining("\n") does the following:
                                                                // 1. It takes all the filtered lines that passed the filter
                                                                // 2. It joins them together into a single String
                                                                // 3. It puts a newline character ("\n") between each line
                                                                // The result is a single String containing all matching lines, separated by newlines

            filteredFileTA.setText("These lines in the text file contain the searched word: \n\n" + filteredContent);// We take the resulting String (filteredContent) and set it as the text of the filteredFileTA

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

}
