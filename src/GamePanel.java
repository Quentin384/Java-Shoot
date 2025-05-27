import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel {

    // --- CONSTANTES ---
    private static final int WIDTH = 600;         // Largeur de la fenêtre de jeu
    private static final int HEIGHT = 600;        // Hauteur de la fenêtre de jeu
    private static final int CANNON_SIZE = 30;    // Taille carrée du canon (en pixels)
    private static final int TARGET_SIZE = 30;    // Taille carrée de la cible (en pixels)
    private static final double GRAVITY = 0.3;    // Gravité constante qui affecte la vitesse verticale

    // --- VARIABLES MEMBRES ---
    private final Random random = new Random();   // Générateur de nombres aléatoires

    // Champs de saisie pour angle et vitesse
    private JTextField angleField;
    private JTextField speedField;

    // Boutons pour lancer tir et repositionner canon/cible
    private JButton shootButton;
    private JButton newPositionButton;

    // Label pour afficher la vitesse du vent
    private JLabel windLabel;

    // Données du tir
    private int angle;   // Angle de tir saisi (de 0 à 90)
    private int speed;   // Vitesse initiale saisie (de 10 à 100)
    private int wind;    // Vent horizontal (aléatoire entre -5 et +5) généré une fois par repositionnement

    // Positions canon et cible
    private int cannonX, cannonY;
    private int targetX, targetY;

    // Position et vitesse du projectile
    private double projX, projY;
    private double vx, vy;

    // Trajectoire (liste des points)
    private final ArrayList<Point> trajectory = new ArrayList<>();

    // Score
    private int score = 0;

    // Timer Swing pour animer le projectile
    private Timer timer;

    // --- CONSTRUCTEUR ---
    public GamePanel() {
        setLayout(null);

        setupUI();
        placeCannonAndTarget();  // Position initiale + génération vent

        timer = new Timer(50, e -> updateProjectile());
    }

    // --- MÉTHODE POUR CONFIGURER L'INTERFACE ---
    private void setupUI() {
        // Angle
        JLabel angleLabel = new JLabel("Angle (0-90°) :");
        angleLabel.setBounds(10, 10, 120, 25);
        add(angleLabel);

        angleField = new JTextField();
        angleField.setBounds(130, 10, 50, 25);
        add(angleField);

        // Vitesse
        JLabel speedLabel = new JLabel("Vitesse (10-100) :");
        speedLabel.setBounds(10, 40, 120, 25);
        add(speedLabel);

        speedField = new JTextField();
        speedField.setBounds(130, 40, 50, 25);
        add(speedField);

        // Bouton Tirer
        shootButton = new JButton("Tirer");
        shootButton.setBounds(10, 80, 80, 30);
        add(shootButton);
        shootButton.addActionListener(e -> startShooting());

        // Bouton Nouvelle position
        newPositionButton = new JButton("Nouvelle position");
        newPositionButton.setBounds(100, 80, 150, 30);
        add(newPositionButton);
        newPositionButton.addActionListener(e -> {
            timer.stop();
            trajectory.clear();
            placeCannonAndTarget();  // repositionne canon/cible et génère un nouveau vent
            repaint();
        });

        // Label vent
        windLabel = new JLabel("Vent : 0");
        windLabel.setBounds(10, 120, 120, 25);
        add(windLabel);
    }

    // --- MÉTHODE POUR POSITIONNER CANON ET CIBLE + GÉNÉRER VENT ---
    private void placeCannonAndTarget() {
        // Position canon dans le quart gauche
        cannonX = random.nextInt(WIDTH / 4 - CANNON_SIZE);
        cannonY = random.nextInt(HEIGHT - CANNON_SIZE);

        // Position cible dans le quart droit
        targetX = WIDTH - TARGET_SIZE - random.nextInt(WIDTH / 4 - TARGET_SIZE);
        targetY = random.nextInt(HEIGHT - TARGET_SIZE);

        // Générer le vent (entre -5 et +5) une seule fois ici
        wind = random.nextInt(11) - 5;

        // Mettre à jour l'affichage du vent
        windLabel.setText("Vent : " + wind);
    }

    // --- MÉTHODE POUR LANCER LE TIR ---
    private void startShooting() {
        try {
            angle = Integer.parseInt(angleField.getText());
            speed = Integer.parseInt(speedField.getText());

            if (angle < 0 || angle > 90) {
                JOptionPane.showMessageDialog(this, "L'angle doit être entre 0 et 90 degrés.");
                return;
            }
            if (speed < 10 || speed > 100) {
                JOptionPane.showMessageDialog(this, "La vitesse doit être entre 10 et 100.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres valides.");
            return;
        }

        // Initialiser position projectile au centre du canon
        projX = cannonX + CANNON_SIZE / 2.0;
        projY = cannonY + CANNON_SIZE / 2.0;

        double radians = Math.toRadians(angle);
        vx = Math.cos(radians) * speed / 2.0;
        vy = -Math.sin(radians) * speed / 2.0;

        trajectory.clear();
        trajectory.add(new Point((int) projX, (int) projY));

        timer.start();
    }

    // --- MÉTHODE POUR METTRE À JOUR LE PROJECTILE ---
    private void updateProjectile() {
        projX += vx;
        vx += wind / 10.0;

        projY += vy;
        vy += GRAVITY;

        trajectory.add(new Point((int) projX, (int) projY));

        Rectangle targetRect = new Rectangle(targetX, targetY, TARGET_SIZE, TARGET_SIZE);
        Point projPoint = new Point((int) projX, (int) projY);

        // Si touche la cible
        if (targetRect.contains(projPoint)) {
            timer.stop();
            score++;
            JOptionPane.showMessageDialog(this, "Touché ! Score : " + score);
            trajectory.clear();
            repaint();
            return;
        }

        // Si sort de l'écran
        if (projX < 0 || projX > WIDTH || projY < 0 || projY > HEIGHT) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Le projectile est sorti de l'écran, essayez encore.");
            trajectory.clear();
            repaint();
            return;
        }

        repaint();
    }

    // --- DESSIN DU JEU ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Fond blanc
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Canon bleu
        g.setColor(Color.BLUE);
        g.fillRect(cannonX, cannonY, CANNON_SIZE, CANNON_SIZE);

        // Cible rouge
        g.setColor(Color.RED);
        g.fillRect(targetX, targetY, TARGET_SIZE, TARGET_SIZE);

        // Trajectoire (petits points noirs)
        g.setColor(Color.BLACK);
        for (Point p : trajectory) {
            g.fillOval(p.x, p.y, 4, 4);
        }

        // Projectile vert (dernier point)
        if (!trajectory.isEmpty()) {
            Point lastPoint = trajectory.get(trajectory.size() - 1);
            g.setColor(Color.GREEN);
            g.fillOval(lastPoint.x - 5, lastPoint.y - 5, 10, 10);
        }

        // Score en haut à droite
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score : " + score, WIDTH - 100, 20);
    }
}
