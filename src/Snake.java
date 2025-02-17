import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Snake extends JFrame {
    private JLabel jscore;
    private JPanel gameOvers;
    private int taille;
    private JPanel[][] principal;
    private JPanel affiche;
    private JPanel tout;
    private int score;
    Case nourriture;
    private boolean haut;
    private boolean bas;
    private boolean gauche;
    private boolean droite;
    private boolean gameOver;
    private ArrayList<Case> serpent;
    private boolean pausePlay;
    private boolean quitterr;
    private boolean resumee;


    //------------------------------------------------------------------------------------
    private void initialiser() {
        setTitle(" SNAKE ");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        taille = 25;
        principal = new JPanel[taille][taille];

        //----------------JSCORE-------------------------------

        jscore = new JLabel(" Score :" + score, new ImageIcon("serpent.jpg"), SwingConstants.CENTER);
        jscore.setOpaque(true);// ceci permet d'activer la visibilité de la couleur du fond
        Font font = new Font("Arial", Font.BOLD, 20);
        jscore.setFont(font);
        jscore.setHorizontalAlignment(SwingConstants.CENTER);
        jscore.setBackground(Color.GREEN);
        jscore.setForeground(Color.red);
        jscore.setPreferredSize(new Dimension(50, 50));

        JPanel menu = new JPanel(new GridLayout(1, 3));
        JButton pause = new JButton("Pause/play");
        pause.addActionListener(new BoutonListiner());
        JButton resume = new JButton("Recommencer");
        resume.addActionListener(new BoutonListiner());
        JButton quitter = new JButton("Quitter");
        quitter.addActionListener(new BoutonListiner());
        menu.add(pause);
        menu.add(resume);
        menu.add(quitter);
        affiche = new JPanel(new GridLayout(taille, taille));
        tout = new JPanel(new BorderLayout());
        tout.add(jscore, BorderLayout.NORTH);
        tout.add(menu, BorderLayout.SOUTH);
        tout.add(affiche, BorderLayout.CENTER);
        add(tout);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                agire(e);
            }
        });
        quitterr = false;
        setVisible(true);
        requestFocusInWindow();
        intialiserLeSerpent();
        tirerCase();
        colorier();
    }

//-------------------------------------------------------------------------------------------------

    private void colorier() {
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                int finalI = i;
                int finalJ = j;
                principal[i][j] = new JPanel() {
                    @Override
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Case a=(new Case(finalI, finalJ));
                        if ( a.estOccupee()) {
                            Graphics2D graph = (Graphics2D) g;
                            GradientPaint gp1 = new GradientPaint(0, 0, Color.red, 5, 5, Color.green, true);
                            GradientPaint gp2 = new GradientPaint(0, 0, Color.PINK, 3, 3, Color.YELLOW, true);
                            if ((new Case(finalI, finalJ)).estLatete()) graph.setPaint(gp2);
                            else graph.setPaint(gp1);
                            graph.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
                        } else {
                            g.setColor(Color.white);
                            g.fillRect(0, 0, this.getWidth(), this.getHeight());
                        }
                        if (a.estLaNourriture()) {
                            Graphics2D graph = (Graphics2D) g;
                            GradientPaint gp1 = new GradientPaint(0, 0, Color.BLACK, 50, 50, Color.BLUE, true);
                            graph.setPaint(gp1);
                            graph.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
                        }
                    }
                };
                affiche.add(principal[i][j]);
            }
        }
    }


    //------------------------------------------------------------------------------
    public void agire(KeyEvent e) {
        if (!pausePlay) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN && !haut) {
                bas = true;
                gauche = droite = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP && !bas) {
                haut = true;
                gauche = droite = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT && !gauche) {
                droite = true;
                haut = bas = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT && !droite) {
                gauche = true;
                haut = bas = false;
            }
        }
    }

//---------------------------------------------------------------------

    public void avancer() {
        Case nouvelle = null;
        if (gauche) {
            Case Anciennetete = serpent.get(serpent.size() - 1);
            nouvelle = new Case(Anciennetete.x, Anciennetete.y - 1);
        }
        if (droite) {
            Case Anciennetete = serpent.get(serpent.size() - 1);
            nouvelle = new Case(Anciennetete.x, Anciennetete.y + 1);
        }
        if (haut) {
            Case Anciennetete = serpent.get(serpent.size() - 1);
            nouvelle = new Case(Anciennetete.x - 1, Anciennetete.y);
        }
        if (bas) {
            Case Anciennetete = serpent.get(serpent.size() - 1);
            nouvelle = new Case(Anciennetete.x + 1, Anciennetete.y);
        }

        if (nouvelle != null)
            gameOver = (nouvelle.x > taille - 1 || nouvelle.x < 0 || nouvelle.y > taille - 1 || nouvelle.y < 0 || nouvelle.contenueDansLaliste());
        serpent.add(nouvelle);

        if (nouvelle != null && nouvelle.eguale(nourriture)) {
            tirerCase();
            ++score;
            jscore.setText("Score :" + score);
        } else serpent.remove(0);

    }

    //-------------------------------------------------------------------
    public void tirerCase() {
        Case casee;
        do {
            Random random = new Random();
            int x = random.nextInt(0, taille);
            random = new Random();
            int y = random.nextInt(0, taille);
            casee = new Case(x, y);
        } while (casee.estOccupee());
        nourriture = casee;
    }

    //----------------------------------------------------

    public void intialiserLeSerpent() {
        serpent = new ArrayList<>();
        serpent.addAll(Arrays.asList(new Case(0, 0), new Case(0, 1), new Case(0, 2)));
        score = 0;
        bas = haut = gauche = false;
        droite = true;
        gameOver = false;
        jscore.repaint();
        gameOvers = new Fin();
        pausePlay = false;
    }


//------------------------------------------------------

    public class BoutonListiner implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (Objects.equals(e.getActionCommand(), "Pause/play")) pausePlay = !pausePlay;
            if (Objects.equals(e.getActionCommand(), "Recommencer")) resumee=true;
            if (Objects.equals(e.getActionCommand(), "Quitter")) quitterr = true;
        }
    }

    //-------------------------------------------------------
    public static class Fin extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image img = null;
            try {
                img = ImageIO.read(new File("game_over.jpg"));

            } catch (Exception ignored) {
            }
            g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);

        }
    }

    //-------------------------------------------------
    public void affichergameover() {
        tout.remove(affiche);
        tout.add(gameOvers, BorderLayout.CENTER);
        try {
            Thread.sleep(2000);
        } catch (Exception ignored) {
        }
        tout.revalidate();
    }
//---------------------------------------------------------------


    private class Case {
        private final int x;
        private final int y;

        public Case(int x, int y) {
            this.x = x;
            this.y = y;
        }

        //--------------
        public boolean estOccupee() {
            for (Case casee : serpent) {
                if (casee!=null && this.x == casee.x && this.y == casee.y) return true;
            }
            return false;
        }

        //--------------
        public boolean estLatete() {
            Case casee = serpent.get(serpent.size() - 1);
            return this.x == casee.x && this.y == casee.y;
        }

        //--------------

        public boolean estLaNourriture() {
            return (this.x == nourriture.x && this.y == nourriture.y);
        }

        //--------------

        public boolean eguale(Case casee) {
            return (casee.x == this.x && casee.y == this.y);
        }

        //--------------
        public boolean contenueDansLaliste() {
            for (Case casee : serpent) {
                if (this.eguale(casee)) return true;
            }
            return false;
        }
    }


    //-----------------------------------------------------------------------------

    public Snake() {
        initialiser();
        while (!quitterr) {
            if(resumee){
                initialiser();
                jscore.setText(" Score : 0");
            }
            setVisible(true);

            while (!gameOver) {
                if (quitterr) System.exit(0);
                tout.repaint();
                if (!pausePlay) avancer();
                requestFocusInWindow();
                try {
                    Thread.sleep(200);
                } catch (Exception ignored) {
                }

                if (gameOver) {
                    resumee=false;
                    affichergameover();

                }
            }
        }
        System.exit(0);
    }

    //----------------------------------------------------
    public static void main(String[] args) {
        new Snake();
    }
}