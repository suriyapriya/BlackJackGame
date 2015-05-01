/*
 * Program     : BlackJackGame
 * Student     : Suriya priya Veluchamy
 * Course      : CS480C
 * Date        : 12/03/2013
 * Description : Play the game in graphics mode
 */

import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;

//Common class for card game
public class CardGame
{
    public static void main(String[] args)
    {
         BlackJackGame.getHintTable();          //Read from file
         BlackJackGame game = new BlackJackGame(); //create object
         game.setMinimumSize(new Dimension(850, 600));
         game.setTitle("BLACK JACK GAME");
         game.setLocationRelativeTo(null);
         game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);         
         game.setVisible(true);
    }
};

//Image panel Class
class ImagePanel extends JPanel
{
    private int count = 0;
    private boolean deduction = false; //To make the deck of cards in cluster form
    
    private Image[] image = new Image[Card.NO_OF_CARDS];
 
    //Constructor for image panel
    public ImagePanel(boolean deduction)
    {
        this.deduction = deduction;
        setPreferredSize(new Dimension(830 ,150));
    }
    
    //draw method
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        int xCoordinate = 5;
        int yCoordinate = 20;
        for(int i = 0; i < count; i++)
        {
            if(image[i] != null)
                g.drawImage(image[i], xCoordinate, yCoordinate, image[i].getWidth(null), image[i].getHeight(null), this);
    
            if(deduction)
            {
                if(i >= count/3)
                    xCoordinate = xCoordinate + count/3;
                else
                    xCoordinate = xCoordinate + (i);
            }
            else
                xCoordinate = xCoordinate + 30;            
        }
    }
    
    // To append a card
    public void addCard(Image img)
    {
        image[count++] = img;
        repaint();
    }
    
    //To remove a card
    public int removeCard()
    {
        if(count > 0)
            count--;
        repaint();
        return count;
    }
    
    // To remove all the cards
    public void removeAllCards()
    {
        count = 0;
        repaint();
    }
}


// Card class - To describe each card in the deck of cards
class Card 
{
    final static int NO_OF_CARDS = 52;
    final static int NO_OF_RANKS = 13;
    final static int NO_OF_SUITS = 4;
    final static String suits[]  = {"Spades", "Hearts", "Diamonds", "Clubs"};
    final static String ranks[]   = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    final static String path = "images_card";

    String rank;
    String suit;
    int cardno;
    Image img;
    
    // Constructor for Card class
    Card(String suit_name, String rank_name, int cardno)
    {
        suit = suit_name;
        rank = rank_name;
        this.cardno = cardno;

        String filename = new Integer(cardno+1).toString() + ".png"; //get image file
        try {
            img = ImageIO.read(new File(Card.path + "\\" + filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    //card shuffling [without repetitive cards]
    public static Card[] shuffleCards()
    {
        Card cards[] = new Card[Card.NO_OF_CARDS];
        Random rand = new Random(System.currentTimeMillis());
        int array[] = new int [Card.NO_OF_CARDS];
        int rand_no;
        Arrays.fill(array, 0);
        int j = 0;
        while(j < Card.NO_OF_CARDS)
        {
            // To set the Card object values
            rand_no = rand.nextInt(Card.NO_OF_CARDS);
            if(array[rand_no] == 0)
            {
                cards[j] = new Card(Card.suits[rand_no / 13], Card.ranks[rand_no % 13], rand_no);                           
                array[rand_no] = 1;
                j++;
            }
        }
        return cards;
    }
    
    //To get the backside cards
    public static Image getCardBacksideImage()
    {
        String filename = Card.path + "\\b1fv.png"; // b1fv.png is backside card image file
        Image img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return img;
    }
};

//HintPanel class
class HintPanel extends JFrame {
    JPanel panel = new JPanel();
    JLabel label = new JLabel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    
    //Constructor for HintPanel
    public HintPanel(int suggestion)
    {
        panel.setLayout(gridbag);
        panel.setPreferredSize(new Dimension(300, 300));
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        makeLabel("Player", Color.WHITE);
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        makeLabel("Dealer's Card", Color.WHITE);
        c.weightx = 1.0;
        c.gridwidth = 1;        
        makeLabel("Hand", Color.WHITE, false, false, true, false);
        
        //To show [2 3 4 5 6 7 8 9 10 A]
        for(int k = 1; k < Card.ranks.length; k++)
        {
            if(Card.ranks[k] != "J" && Card.ranks[k] != "K" && Card.ranks[k] != "Q")
            {
                makeLabel(Card.ranks[k], Color.WHITE, true, false, true, false);
            }
        }
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        makeLabel(Card.ranks[0], Color.WHITE, true, false, true, false);
        
        // To show the player values[5-7, 8, 9, etc.] 
        for(int i = 7; i < BlackJackGame.hint_table.length; i++)
        {
            c.weightx   = 1.0;
            c.gridwidth = 1;
            
            if(i == 7)
            {
                makeLabel((i-2 + "-" + i)
                        , Color.WHITE, false, false, false, true);
            }
            else if(i == 17)
                makeLabel(("" + i + '+')
                        , Color.WHITE, false, false, false, true);
            else
                makeLabel(("" + i )
                        , Color.WHITE, false, false, false, true);
                
            
            // To show the H/D/S in hint table
            for(int j = 0; j < BlackJackGame.hint_table[i].length() ; j++)
            {
                Color color;
                if(BlackJackGame.hint_table[i].charAt(j) == 'H')
                    color = Color.RED;
                else if(BlackJackGame.hint_table[i].charAt(j) == 'D')
                    color = Color.BLUE;
                else if(BlackJackGame.hint_table[i].charAt(j) == 'S')
                    color = Color.YELLOW;
                else
                    color = Color.WHITE;

                if(j+1 == BlackJackGame.hint_table[i].length())
                    c.gridwidth = GridBagConstraints.REMAINDER; //end row

                makeLabel((BlackJackGame.hint_table[i].charAt(j) + "")                                
                        , color, false, false, i+1 == BlackJackGame.hint_table.length, j+1 == BlackJackGame.hint_table[i].length());
            }
        }
        makeLabel("Suggestion : " + BlackJackGame.hint[suggestion], Color.LIGHT_GRAY).setHorizontalAlignment(SwingConstants.RIGHT);        
        add(panel);
    }

    //To create the labels for showing hint elements
    protected JLabel makeLabel(String name, Color color)
    {        
        return this.makeLabel(name, color, false, false, false, false);        
    }
    
    // To show the labels for showing hints elements with borders
    protected JLabel makeLabel(String name, Color color, boolean top, boolean left, boolean bottom, boolean right)
    {
        JLabel label = new JLabel(name);
        int t = 0, l = 0, b = 0, r = 0;
        label.setHorizontalAlignment(SwingConstants.CENTER);
        gridbag.setConstraints(label, c);

        label.setOpaque(true);
        if(top)
            t = 1; //top border
        if(bottom)
            b = 1; //bottom border
        if(left)
            l = 1; //left border
        if(right)
            r = 1; //right border
        
        label.setBorder(BorderFactory.createMatteBorder(t, l, b, r, Color.BLACK));
        label.setBackground(color);
        panel.add(label);
        return label;
    }    
}

//OptionPanel class
class OptionPanel extends JPanel
{
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    BlackJackGame game;
    
    //Constructor for OptionPanel
    public OptionPanel(BlackJackGame newGame)
    {
        game = newGame;
        setLayout(gridbag);
        setPreferredSize(new Dimension(400, 50));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 10, 0, 0);

        JButton newHand = new JButton("New Hand");
        JButton hit = new JButton("   Hit  ");
        JButton stand = new JButton("  Stand ");
        JButton hint = new JButton("  Hint  ");
        add(newHand, c);
        add(hit, c);
        add(stand, c);
        add(hint, c);
        
        //New hand button listener
        newHand.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                game.makeNewHand();
            }
        });
        
        //hit button listener
        hit.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {    
                game.makeHit();
            }
        });
        
        // Stand button listener
        stand.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                game.makeStand();
            }
        });
        
        //hint button listener
        hint.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                game.showHint();
            }
        });
    }
}

//BlackJackGame class
class BlackJackGame extends JFrame
{
    final static int MAX       = 18;
    private int hidden_value   = 0;
    Card cards[]               = null;
    static String hint_table[] = new String [MAX];
    static String hint[] = {"", "", "HIT", "STAND"};
    enum winStrategy {WIN, PASS, LOSE, NIL};
    
    int dealer_val = 0,
        player_val = 0,
        dealer_card_position = 0,
        player_card_position = 0;
    
    //Image panels
    ImagePanel image1 = new ImagePanel(false);
    ImagePanel image2 = new ImagePanel(false);      
    ImagePanel image3 = new ImagePanel(true);
    
    //Option panel
    OptionPanel image4 = new OptionPanel(this);
    
    //constructor for Black jack game
    BlackJackGame()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        
        image3.setBorder(new TitledBorder("Deck of Cards"));
        add(image3, c);
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = 1;
        
        image1.setBorder(new TitledBorder("Dealer"));
        add(image1, c);
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = 2;
        
        image2.setBorder(new TitledBorder("Player"));
        add(image2, c);
        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.gridy = 3;
        
        image4.setBorder(new TitledBorder("Options"));        
        add(image4, c);
        pack();

        initiateGame();
    }
    
    //To initiate the game
    public void initiateGame()
    {
        cards = Card.shuffleCards();
        image1.removeAllCards();
        image2.removeAllCards();
        image3.removeAllCards();
        dealer_val = player_val = 0;
        dealer_card_position = player_card_position = 0;
        for(int i = 0; i < Card.NO_OF_CARDS; i++)
        {
            image3.addCard(Card.getCardBacksideImage());
        }
    }

    // To get the table top from the file
    public static void getHintTable()
    {
        Arrays.fill(hint_table, "H");
        FileInputStream file;
        try 
        {
            file = new FileInputStream("blackJack-play-suggestion.txt");
            Scanner input = new Scanner(file);
            int inc = 2;
            hint_table[inc] = input.nextLine();
            for(inc = 3; inc <= 7; inc++)
            {
                hint_table[inc] = hint_table[2];
            }
            while(input.hasNext())
            {
                hint_table[inc++] = input.nextLine();
            }
            input.close();
        }
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
         return;
    }
    
    // To get the specific card values
    public int getSpecificCardValue(int cards_inc)
    {
        if(        cards[cards_inc].rank == "J" 
                || cards[cards_inc].rank == "K" 
                || cards[cards_inc].rank == "Q")
            return 10;
        
        if(cards[cards_inc].rank == "A" && 
                (dealer_card_position < player_card_position || dealer_card_position == player_card_position))
        {
            if(player_val+11 <= 21)
                return 11;
            else            
                return 1;
        }
        
        if(cards[cards_inc].rank == "A" && 
                (dealer_card_position > player_card_position || dealer_card_position == player_card_position))
        {
            if(dealer_val+11 <= 21)
                return 11;
            else            
                return 1;
        }
        return Integer.parseInt(cards[cards_inc].rank);
    }
    
 // To get suggestion
    public int getSuggestion()
    {
        int rank = getSpecificCardValue(dealer_card_position);
        if(rank == 1) // For 'A'
            rank = hint_table[player_val].length()-1;
        else
            rank = rank - 2;
        if(         player_val < hint_table.length
                && (hint_table[player_val].charAt(rank) == 'H'
                || hint_table[player_val].charAt(rank)  == 'D'
                || hint_table[player_val].charAt(rank)  == 'h'))
            return 2; // HIT
        else
            return 3; //STAND
    }

    // New Hand
    public void makeNewHand()
    {        
        if(dealer_val != 0)
        {
            JOptionPane.showMessageDialog(null, "Game Restarts");
            initiateGame();
        }
        
        hidden_value = dealer_card_position = player_card_position;
        
        dealer_card_position++;
        System.out.printf("\nDealer picked the %s of %s\n", cards[dealer_card_position].rank, cards[dealer_card_position].suit);
        image1.addCard(Card.getCardBacksideImage());
        image1.addCard(cards[dealer_card_position].img);
        image3.removeCard();
        dealer_val += getSpecificCardValue(dealer_card_position);
        
        player_card_position = dealer_card_position + 1;
        System.out.printf("You picked the %s of %s\n", cards[player_card_position].rank, cards[player_card_position].suit);
        image2.addCard(cards[player_card_position].img);
        image3.removeCard();        
        player_val += getSpecificCardValue(player_card_position);        
    }
    
    //Hit
    public void makeHit()
    {
        if(dealer_card_position == 0 && player_card_position == 0)
        {
            JOptionPane.showMessageDialog(null, "DO NEW HAND FIRST", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        player_card_position++;
        System.out.printf("\nYou picked the %s of %s\n", cards[player_card_position].rank, cards[player_card_position].suit);
        image2.addCard(cards[player_card_position].img);
        image3.removeCard();
        player_val += getSpecificCardValue(player_card_position);
        dealer_card_position = player_card_position;
        if(winningStrategy() != winStrategy.NIL)
        {
            image1.removeAllCards();
            showMessage(winningStrategy(), hint[2]);
        }
        
    }
    
    public void makeStand()
    {
        if(dealer_card_position == 0 && player_card_position == 0)
        {
            JOptionPane.showMessageDialog(null, "DO NEW HAND FIRST", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        image1.removeAllCards();
        image1.addCard(cards[0].img);
        image1.addCard(cards[1].img);
        dealer_card_position = player_card_position;
        dealer_val += getSpecificCardValue(hidden_value);

        while(true)
        {
            dealer_card_position++;
            System.out.printf("\nDealer picked the %s of %s\n", cards[dealer_card_position].rank, cards[dealer_card_position].suit);
            image1.addCard(cards[dealer_card_position].img);
            image3.removeCard();
            dealer_val += getSpecificCardValue(dealer_card_position);
            if(winningStrategy() != winStrategy.NIL)
            {
                showMessage(winningStrategy(), hint[3]);
                break;
            }
        }
    }
    
    public void showHint()
    {
        if(dealer_val == 0 && player_val == 0)
        {
            JOptionPane.showMessageDialog(null, "No cards selected Yet", "Warning", JOptionPane.WARNING_MESSAGE);
            System.out.print("\nNo cards selected. Pick your cards first\n");
        }
        else
        {
            HintPanel hint = new HintPanel(getSuggestion());
            hint.setLocationRelativeTo(null);
            hint.pack();
            hint.setVisible(true);
        }
    }
   
    public void showMessage(winStrategy strategy, String s)
    {
        if(strategy == winStrategy.LOSE)
        {
            if(s == hint[2])
            {
                image1.addCard(cards[0].img);
                image1.addCard(cards[1].img);
            }
            JOptionPane.showMessageDialog(null, "You lose");
        }
        
        if(strategy == winStrategy.WIN)
        {
            if(s == hint[2])
            {
                image1.addCard(cards[0].img);
                image1.addCard(cards[1].img);
            }
            JOptionPane.showMessageDialog(null, "House Busts! you win!");
        }
        if(strategy == winStrategy.PASS)
        {
            if(s == hint[2])
            {
            image1.addCard(cards[0].img);
            image1.addCard(cards[1].img);
            }
            JOptionPane.showMessageDialog(null, "Game pass");
        }
        Object[] option = {"NEW GAME", "QUIT"};
        int retOption;
        retOption = JOptionPane.showOptionDialog( null, "Click your Game Option", 
                                                    "GAME ENDS",
                                                    JOptionPane.PLAIN_MESSAGE,
                                                    JOptionPane.QUESTION_MESSAGE,
                                                    null,
                                                    option,
                                                    option[0]);
        if(retOption != 0)
        {
            dispose();
            System.exit(0);
        }
        else if(option[retOption] == "NEW GAME")
            initiateGame();
        
    }
    
    // Check for winner or loser
    public winStrategy winningStrategy()
    {
        if(player_val > 21 || dealer_val == 21) // PLAYER LOSE OR DEALER WIN
        {
            System.out.print("You lose !\n");
            return winStrategy.LOSE;
        }
        if(dealer_val > 21 || player_val == 21) // DEALER LOSE OR PLAYER WIN
        {
            System.out.print("House Busts! you win!\n");
            return winStrategy.WIN;
        }
        if(dealer_val >= 17 && player_val >= 17) // DEALER AND PLAYER ABOVE 17
        {
            if(dealer_val > player_val)
            {
                System.out.print("You lose !\n");
                return winStrategy.LOSE;
            }
            else if(player_val > dealer_val)
            {
                System.out.print("House Busts! you win!\n");
                return winStrategy.WIN;
            }
            else
            {
                System.out.print("Game pass\n");
                return winStrategy.PASS;
            }
        }
        if(dealer_val >= 17) // DEALER ONLY ABOVE 17
        {
            if(player_val < dealer_val)
            {
                System.out.print("You lose !\n");
                return winStrategy.LOSE;

            }
        }        
        return winStrategy.NIL;
    }
 }