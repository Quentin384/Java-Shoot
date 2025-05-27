import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater garantit que le GUI est créé dans le thread approprié (le thread Swing)
        SwingUtilities.invokeLater(() -> {
            // Création de la fenêtre principale
            JFrame frame = new JFrame("T.A.R.G.E.T.");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Ferme l’application à la fermeture de la fenêtre
            frame.setSize(600, 600);  // Taille fixe de la fenêtre
            frame.setResizable(false); // Empêche le redimensionnement

            // Création du panneau de jeu où tout sera dessiné et géré
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel); // Ajout du panneau de jeu à la fenêtre

            frame.setVisible(true); // Affiche la fenêtre à l’écran
        });
    }
}



