package edu.gonzaga.Farkle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;


// import com.github.dnsev.identicon.Identicon;

// Java Swing based Farkle frontend
// Add/edit/change as you see fit to get it to play Yahtzee!
class Farkle {
    // If you have your HW3 code, you could include it like this, as needed
    // Player player;
    // Meld meld;
    // Hand hand;
    private Random random = new Random();
    // Main game GUI window and two main panels (left & right)
    JFrame mainWindowFrame;
    JPanel controlPanel;
    JPanel scorecardPanel;

    // Dice view, user input, reroll status, and reroll button
    JTextField diceValuesTextField;
    JTextField diceKeepStringTextField;
    JButton diceRerollBtn;
    JTextField rerollsLeftTextField;

    // Player name - set it to your choice
    JTextField playerNameTextField = new JTextField();

    // Buttons for showing dice and checkboxes for meld include/exclude
    ArrayList<JButton> diceButtons = new ArrayList<>();
    ArrayList<JCheckBox> meldCheckboxes = new ArrayList<>();
    JTextField diceDebugLabel = new JTextField();
    JLabel meldScoreTextLabel = new JLabel();
    JButton rollButton = new JButton();

    JPanel playerInfoPanel = new JPanel();
    JPanel diceControlPanel = new JPanel();
    JPanel meldControlPanel = new JPanel();
    JButton rerollButton = new JButton("Reroll");
    JButton bankButton = new JButton("Bank");
    JLabel totalScoreLabel = new JLabel("Total Score:");
    JLabel totalScoreTextLabel = new JLabel("Total score:");    

    DiceImages diceImages = new DiceImages("media/");


    private void rollDice() {
        Timer timer = new Timer(100, new ActionListener() {
            int count = 0;
    
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the icon on each dice button with a random dice face
                for (int i = 0; i < 6; i++) {
                    // Generate random value between 1 and 6 for each die
                    int diceValue = random.nextInt(6) + 1;
                    // Load the image
                    ImageIcon icon = new ImageIcon("media/D6-0" + diceValue + ".png");
                    Image image = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                    // Create a new ImageIcon with the scaled image
                    ImageIcon scaledIcon = new ImageIcon(image);
                    // Update the icon on the corresponding dice button
                    diceButtons.get(i).setIcon(scaledIcon);
                    diceButtons.get(i).setText(Integer.toString(diceValue));
                }
    
                // Stop timer after a certain duration
                count++;
                if (count >= 10) {
                    ((Timer) e.getSource()).stop();
                    // Display rolled dice values in the debug text field
                    updateDiceDebugLabel();
                    
                }
            }
            
        });
        // Start the timer
        timer.start();
        
    }
    

    private void updateDiceDebugLabel() {
        StringBuilder debugText = new StringBuilder();
        for (JButton button : diceButtons) {
            debugText.append(button.getText());
        }
        diceDebugLabel.setText(debugText.toString());
    }

    private void addRollDiceButtonListener() {
        rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Roll the dice when the button is clicked
                rollDice();
            }
        });
    }

 

    // Calculate Meld Score method
    private void calculateMeldScore() {
        int[] diceCounts = new int[7]; // Array to count occurrences of each dice face value (index 0 not used)
        int[] dicePoints = {0, 100, 0, 0, 0, 50, 0}; // Points for each dice face value (0-6)
    
        // Count occurrences of each dice face value
        for (int i = 0; i < meldCheckboxes.size(); i++) {
            if (meldCheckboxes.get(i).isSelected()) {
                int diceValue = Integer.parseInt(diceButtons.get(i).getText());
                diceCounts[diceValue]++;
            }
        }
    
        // Calculate meld score based on Farkle rules
        int meldScore = 0;
        meldScore += calculateStraightScore(diceCounts);
        meldScore += calculateThreePairsScore(diceCounts);
        meldScore += calculateTripleOnesScore(diceCounts); 
        meldScore += calculateBasicTriplesScore(diceCounts, dicePoints);
        meldScore += calculateExtraTriplesScore(diceCounts, dicePoints);
        meldScore += calculateSingleScore(diceCounts, 1, dicePoints);
        meldScore += calculateSingleScore(diceCounts, 5, dicePoints);
    
        // Update the meld score text label
        meldScoreTextLabel.setText(Integer.toString(meldScore));
    }
    
    // Calculate score for straight combo
    private int calculateStraightScore(int[] diceCounts) {
        for (int i = 1; i <= 6; i++) {
            if (diceCounts[i] != 1) {
                return 0;
            }
        }
        return 1000;
    }
    
    // Calculate score for three pairs combo
    private int calculateThreePairsScore(int[] diceCounts) {
        int pairCount = 0;
        for (int i = 1; i <= 6; i++) {
            if (diceCounts[i] == 2) {
                pairCount++;
            }
        }
        if (pairCount == 3) {
            return 750;
        }
        return 0;
    }
    
    // Calculate score for triple ones combo
    private int calculateTripleOnesScore(int[] diceCounts) {
        if (diceCounts[1] >= 3) {
            return 1000;
        }
        return 0;
    }
    
    // Calculate score for basic triples combo
    private int calculateBasicTriplesScore(int[] diceCounts, int[] dicePoints) {
        int score = 0;
        for (int i = 2; i <= 6; i++) { // Skip index 1 (for dice value 1)
            if (diceCounts[i] >= 3) {
                score += dicePoints[i] * 100;
            }
        }
        return score;
    }
    

    private int calculateTripleScore(int diceValue, int count, int points) {
        System.out.println("Calculating triple score for: " + diceValue + "s, count: " + count + ", points: " + points);
        if (diceValue == 1) {
            // Special case for triple 1s
            return 1000;
        } else {
            // General case for other triples
            return points * 100 * (count - 2);
        }
    }
    
    
    // Calculate score for extra triples combo
    private int calculateExtraTriplesScore(int[] diceCounts, int[] dicePoints) {
        int score = 0;
        for (int i = 2; i <= 6; i++) { // Skip index 1 (for dice value 1)
            if (diceCounts[i] >= 3) {
                score += dicePoints[i] * 100 * (diceCounts[i] - 2);
            }
        }
        return score;
    }
    
    // Calculate score for single dice combo (1 or 5)
    private int calculateSingleScore(int[] diceCounts, int diceValue, int[] dicePoints) {
        if (diceValue == 1 && diceCounts[1] < 3) { // Exclude single ones if triple ones are present
            return dicePoints[diceValue] * diceCounts[diceValue];
        }
        if (diceValue == 5) {
            return dicePoints[diceValue] * diceCounts[diceValue];
        }
        return 0;
    }
    



    public static void main(String [] args) {
        Farkle app = new Farkle();    // Create, then run GUI
        app.runGUI();
    }

    // Constructor for the actual Farkle object
    public Farkle() {
        // player = new Player();
        // Create any object you'll need for storing the game:
        // Player, Scorecard, Hand/Dice
    }

    // Sets up the full Swing GUI, but does not do any callback code
    void setupGUI() {
        // Make and configure the window itself
        this.mainWindowFrame = new JFrame("Sean Nickerson GUI Farkle");
        this.mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainWindowFrame.setLocation(100,100);
        
        // Player info and roll button panel
        this.playerInfoPanel = genPlayerInfoPanel();

        // Dice status and checkboxes to show the hand and which to include in the meld
        this.diceControlPanel = genDiceControlPanel();

        // The bottom Meld control panel
        this.meldControlPanel = genMeldControlPanel();

        mainWindowFrame.getContentPane().add(BorderLayout.NORTH, this.playerInfoPanel);
        mainWindowFrame.getContentPane().add(BorderLayout.CENTER, this.diceControlPanel);
        mainWindowFrame.getContentPane().add(BorderLayout.SOUTH, this.meldControlPanel);
        mainWindowFrame.pack();
    }
    private void clearCheckboxes() {
        for (JCheckBox checkbox : meldCheckboxes) {
            checkbox.setSelected(false); // Clear the selection of each checkbox
        }
    }
    
    /**
     * Generates and returns a JPanel containing components for meld control.
     *
     * This method creates a JPanel with a flow layout. It includes components such as a label
     * for meld score, a button to calculate meld, and a label to display the meld score. 
     *
     * @return A JPanel containing components for meld control.
     */
    // Generate and configure the meld control panel
private JPanel genMeldControlPanel() {
    JPanel meldControlPanel = new JPanel();
    meldControlPanel.setLayout(new FlowLayout());

    // Create and add the meld score label
    JLabel meldScoreLabel = new JLabel("Meld Score:");
    meldControlPanel.add(meldScoreLabel);
    meldControlPanel.add(meldScoreTextLabel);

    

    // Create and add the Reroll button
    JButton rerollButton = new JButton("Reroll");
    meldControlPanel.add(rerollButton);
    rerollButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Simulate rolling the dice again
            rollDice();
        }
    });
    

    // Create and add the Bank button
    JTextField totalScoreTextField = new JTextField(10);
    totalScoreTextField.setText("0"); // Set initial value to 0

    

// Create and add functionality to the "Bank" button
JButton bankButton = new JButton("Bank");
meldControlPanel.add(bankButton);

bankButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Calculate total score based on meld score and add it to the player's total score
        int currentMeldScore = Integer.parseInt(meldScoreTextLabel.getText());
        int totalScore = Integer.parseInt(totalScoreTextField.getText()) + currentMeldScore;

        // Update the total score label
        totalScoreTextField.setText(Integer.toString(totalScore));

        // Reset meld score to zero
        meldScoreTextLabel.setText("0");

        // Disable the checkbox for the used dice
        for (int i = 0; i < diceButtons.size(); i++) {
            if (meldCheckboxes.get(i).isSelected()) {
                meldCheckboxes.get(i).setEnabled(false); // Disable the checkbox for the used dice
            }
        }

        // Clear checkboxes for the next round
        clearCheckboxes();
    }
});


// Add Total Score Components to Bottom Panel
meldControlPanel.add(totalScoreLabel);
meldControlPanel.add(totalScoreTextField);

    return meldControlPanel;
}




    /**
     * Generates and returns a JPanel containing components for dice control.
     *
     * This method creates a JPanel with a black border and a grid layout (3 rows, 7 columns).
     * It includes components such as labels for dice values and meld options, buttons for each
     * dice, and checkboxes for melding. The dice buttons and meld checkboxes are added to
     * corresponding lists for further manipulation.
     *
     * @return A JPanel containing components for dice control.
     */
    private JPanel genDiceControlPanel() {
        JPanel newPanel = new JPanel();
        newPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        newPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1); // Add some padding
    
        JLabel diceLabel = new JLabel("Dice Vals:");
        JLabel meldBoxesLabel = new JLabel("Meld 'em:");
    
        // Add dice buttons
        for(int index = 0; index < 6; index++) {
            JButton diceStatusButton = new JButton();
            ImageIcon icon = new ImageIcon("media/D6-01.png");
            Image image = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(image);
            diceStatusButton.setIcon(scaledIcon);
            diceStatusButton.setPreferredSize(new Dimension(40, 40));
            diceStatusButton.setBorder(null);
    
            gbc.gridx = index + 1; // Add 1 to skip the first column
            gbc.gridy = 0;
            newPanel.add(diceStatusButton, gbc);
            this.diceButtons.add(diceStatusButton);
        }
    
        // Add checkboxes
        for(int index = 0; index < 6; index++) {
            JCheckBox meldCheckbox = new JCheckBox();
            meldCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridx = index + 1; // Add 1 to skip the first column
            gbc.gridy = 1;
            newPanel.add(meldCheckbox, gbc);
            this.meldCheckboxes.add(meldCheckbox);
        }
    
        gbc.gridx = 0; // Reset gridx for labels
        gbc.gridy = 0;
        newPanel.add(diceLabel, gbc);
        gbc.gridy = 1;
        newPanel.add(meldBoxesLabel, gbc);

        meldControlPanel.add(rerollButton);
        meldControlPanel.add(bankButton);
    
        return newPanel;
    }

    /**
     * Generates and returns a JPanel containing player information components.
     *
     * This method creates a JPanel with a black border and a horizontal flow layout.
     * It includes components such as a JLabel for player name, a JTextField for entering
     * the player name, a JButton for rolling dice, and a debug label for dice information.
     * The player name text field, dice debug label, and roll button are added to the panel
     * with appropriate configurations.
     *
     * @return A JPanel containing components for player information.
     */
    private JPanel genPlayerInfoPanel() {
        JPanel newPanel = new JPanel();
        newPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        newPanel.setLayout(new FlowLayout());    // Left to right

        JLabel playerNameLabel = new JLabel("Your");
        playerNameTextField.setColumns(20);
        diceDebugLabel.setColumns(6);
        diceDebugLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        rollButton.setText("Roll Dice");


        newPanel.add(playerNameLabel);   // Add our player label
        newPanel.add(playerNameTextField); // Add our player text field
        newPanel.add(rollButton);        // Put the roll button on there
        newPanel.add(this.diceDebugLabel);

        return newPanel;
    }


    /*
     *  This is a method to show you how you can set/read the visible values
     *   in the various text widgets.
     */
    private void putDemoDefaultValuesInGUI() {
        // Example setting of player name
        this.playerNameTextField.setText("Player One");

        // Example Dice debug output
        this.diceDebugLabel.setText("");
    }

    /*
     * This is a demo of how to add callbacks to the buttons
     *  These callbacks can access the class member variables this way
     */
    private void addDemoButtonCallbackHandlers() {
        // Example of a button callback - just prints when clicked
        this.rollButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("They clicked the roll button!");
                diceButtons.get(2).setText("");
                diceDebugLabel.setText("67321");
            }
        });

        // Example of another button callback
        // Reads the dice checkboxes and counts how many are checked (selected)
        // Sets the meldScoreTextLabel to how many of the checkboxes are checked


        // Example of a checkbox handling events when checked/unchecked
        JCheckBox boxWithEvent = this.meldCheckboxes.get(1);
        boxWithEvent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(boxWithEvent.isSelected()) {
                    System.out.println("Checkbox is checked");
                } else {
                    System.out.println("Checkbox is unchecked");
                }
            }
        });
    }

    /*
     *  Builds the GUI frontend and allows you to hook up the callbacks/data for game
     */
    void runGUI() {
        System.out.println("Starting GUI app");
        setupGUI();

        // These methods are to show you how it works
        // Once you get started working, you can comment them out so they don't
        //  mess up your own code.

        addRollDiceButtonListener();
        putDemoDefaultValuesInGUI();
        addDemoButtonCallbackHandlers();

        for (JCheckBox checkBox : meldCheckboxes) {
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Recalculate the meld score when any checkbox is checked or unchecked
                    calculateMeldScore();
                }
            });
        }

        // Right here is where you could methods to add your own callbacks
        // so that you can make the game actually work.

        // Run the main window - begins GUI activity
        mainWindowFrame.setVisible(true);
        System.out.println("Done in GUI app");
    }

}
