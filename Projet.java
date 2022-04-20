//Projet

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


interface Observer {

    public void update();

}

abstract class Observable {

    private ArrayList<Observer> observers;
    public Observable() {
        this.observers = new ArrayList<Observer>();
    }
    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void notifyObservers() {
        for(Observer o : observers) {
            o.update();
        }
    }
}

public class Projet {

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            CModele modele = new CModele();
            CVue vue = new CVue(modele);
        });
    }
}

class CModele extends Observable {

    public static final int HAUTEUR=40, LARGEUR=40;

    private Cellule[][] cellules;
    public  ArrayList<Joueur> Tjoueurs ; 
    public Joueur Joueuractuel ; 

    public CModele() {

        cellules = new Cellule[LARGEUR+2][HAUTEUR+2];
        for(int i=0; i<LARGEUR+2; i++) {
            for(int j=0; j<HAUTEUR+2; j++) {
                cellules[i][j] = new Cellule(this,i, j, null);
            }
        }
        init();
    }

    public void init() {

        // placement des joueurs 
        this.Tjoueurs = new ArrayList<Joueur>(); 
        Joueur J1 = new Joueur(this, 1); 
        this.Tjoueurs.add(J1) ;  
        Joueur J2 = new Joueur(this, 2); 
        this.Tjoueurs.add(J2); 
        Joueur J3 = new Joueur(this, 3); 
        this.Tjoueurs.add(J3); 
        Joueur J4 = new Joueur(this, 4); 
        this.Tjoueurs.add(J4);
        
        this.Joueuractuel=J1; 

        for(int i=1;i<=4;i++){
            cellules[i][i].j=Tjoueurs.get(i-1); 
        }

        // placement de l'helico + artefacts 
        Random r = new Random();
        int low = 5;
        int high = LARGEUR-5;   // la fenetre de tirage a ete reduite pour ne pas placer un objet sur un coin 
        for (int k=3;k<=7;k++){
            int i = r.nextInt(high-low) + low;
            int j = r.nextInt(high-low) + low;
            while (cellules[i][j].etat >1 && cellules[i][j].j == null  ){    // on  verifie que la case tiré soit valide sinon on en tire une autre 
            i = r.nextInt(high-low) + low;
            j = r.nextInt(high-low) + low;
            }

            cellules[i][j].etat = k ;
        }
    }

    public CModele(ArrayList<Joueur> tjoueurs) {
        Tjoueurs = tjoueurs;
    }

    public void avance() { 
        Random r = new Random();
        int low = 0;
        int high = LARGEUR+1;

        for(int k=1; k <= 3; k++){
            int i = r.nextInt(high-low) + low;
            int j = r.nextInt(high-low) + low;
            while (cellules[i][j].etat >1){          // ici aussi on  verifie que la case tiré soit valide sinon on en tire une autre 
            i = r.nextInt(high-low) + low;
            j = r.nextInt(high-low) + low;
            }
            if (cellules[i][j].etat == 0) {
                 cellules[i][j].etat = 1; }

            else if (cellules[i][j].etat == 1) {
                 cellules[i][j].etat = 2; }        

        }

        notifyObservers();
    }
    public Cellule emplacementjoueur(Joueur J){
        for(int i=0; i<LARGEUR+2; i++) {
            for(int j=0; j<HAUTEUR+2; j++) {
                if (cellules[i][j].contientjoueur(J)){
                    Cellule C= cellules[i][j] ;
                    return C ;   
                }
            }
        }
        return null;
    }
    public void tour(String a,Joueur J){
        Cellule c = emplacementjoueur(J); 
        Cellule c1 = switch (a) {
            case "z" -> cellules[c.coordx()][c.coordy()-1];
            case "q" -> cellules[c.coordx()-1][c.coordy()];
            case "s" -> cellules[c.coordx()][c.coordy()+1];
            case "d" -> cellules[c.coordx()+1][c.coordy()];
            default -> throw new IllegalStateException("Invalid mouvement");
        };
        if (c1.etat == 0 && c1.getjoueur() == null){
        c1.ajoutejoueur(c.getjoueur()); 
        c.enlevejoueur();
    } else{
        return ; 
    }
       notifyObservers();
    }

    public void seche(Joueur J){
        
        Cellule c = emplacementjoueur(J); 
        Cellule c1 = cellules[c.coordx()][c.coordy()-1];
        Cellule c2 = cellules[c.coordx()-1][c.coordy()];
        Cellule c3 = cellules[c.coordx()][c.coordy()+1];
        Cellule c4 = cellules[c.coordx()+1][c.coordy()];

        if (c1.etat == 1){c1.etat = 0;}
        if (c2.etat == 1){c2.etat = 0;}
        if (c3.etat == 1){c3.etat = 0;}
        if (c4.etat == 1){c4.etat = 0;}
       

        

        
        
        notifyObservers();
    }

    

    public void Joueursuivant(Joueur j ){
        int i =0; 
        while (this.Tjoueurs.get(i)!=j){
            i++; 
        }
        if (i==this.Tjoueurs.size()-1 ){
            this.Joueuractuel=this.Tjoueurs.get(0); 
        }
        else{
            this.Joueuractuel=this.Tjoueurs.get(i+1); 
        }
    }


    protected int compteVoisines(int x, int y) {
    int res=0;

    for(int i=x-1; i<=x+1; i++) {
        for(int j=y-1; j<=y+1; j++) {
            if (cellules[i][j].etat == 1) { res++; }
        }
    }
    return (res - ((cellules[x][y].etat)));

    }


    public Cellule getCellule(int x, int y) {
        return cellules[x][y];
    }

}

class Joueur {

    private CModele modele; 
    private final int id ; 
    protected boolean EnVie; 

    public Joueur (CModele modele,int id){
        this.modele =modele; 
        this.id= id; 
        this.EnVie=true; 
    }
    public int getidjoueur(){
        return this.id; 
    } 
}

class Cellule {

    private CModele modele;

    protected int etat;

    private final int x, y;
    public Joueur j ; 
    public Cellule(CModele modele, int x, int y,Joueur j ) {
        this.modele = modele;
        this.etat = 0;
        this.x = x; this.y = y;
    }

    private int prochainEtat;
    protected void evalue() {
        switch (this.modele.compteVoisines(x, y)) {
            case 2: prochainEtat=etat; break;
            case 3: prochainEtat=1; break;
            default: prochainEtat=0;
        }
    }
    protected void evolue() {
        etat = prochainEtat;
    }

    public int estVivante() {
        return etat;
    }
    public boolean contientjoueur(Joueur J ){
         return this.j == J ; 
    }
   public int coordx(){
        return this.x; 
    } 
    public int coordy(){
        return this.y; 
    }
    public void enlevejoueur(){
        this.j=null;
    }
    public void ajoutejoueur(Joueur J){
        this.j=J  ; 
    }
    public Joueur getjoueur(){
        return this.j ; 
    }
}

class CVue {

    private JFrame frame;

    private VueGrille grille;
    private VueCommandes commandes;


    public CVue(CModele modele) {

        frame = new JFrame();
        frame.setTitle("Projet Java");

        frame.setLayout(new FlowLayout());


        grille = new VueGrille(modele);
        frame.add(grille);
        commandes = new VueCommandes(modele);
        frame.add(commandes);


        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}



class VueGrille extends JPanel implements Observer {

    private CModele modele;

    private final static int TAILLE = 15;


    public VueGrille(CModele modele) {
        this.modele = modele;

        modele.addObserver(this);

        Dimension dim = new Dimension(TAILLE*CModele.LARGEUR,
        TAILLE*CModele.HAUTEUR);
        this.setPreferredSize(dim);
}


    public void update() { repaint(); }


    public void paintComponent(Graphics g) {
        super.repaint();

        for(int i=1; i<=CModele.LARGEUR; i++) {
            for(int j=1; j<=CModele.HAUTEUR; j++) {

                paint(g, modele.getCellule(i, j), (i-1)*TAILLE, (j-1)*TAILLE);
            }
        }
    }

    private void paint(Graphics g, Cellule c, int x, int y) {


        if (c.estVivante() == 1) {
            g.setColor(new Color(51, 153, 255));   // case inoncé (1) en bleu clair 
        } 
        if (c.estVivante() == 2) {
            g.setColor(Color.BLUE);                           // case submergé (2) en bleu 
        }
        if (c.estVivante() == 0) {
            g.setColor(Color.WHITE);                       // case basique (0) en blanc 
        }
        if (c.estVivante() == 3) {
            g.setColor(Color.darkGray);                    // helico (3) en gris foncé 
        }
        for (int i=0;i<modele.Tjoueurs.size();i++){
            if (c.contientjoueur(modele.Tjoueurs.get(i))){
                g.setColor(Color.BLACK);
            }
        }
        if (c.contientjoueur(modele.Joueuractuel)){
            g.setColor(Color.RED);
        }
        
        if (c.estVivante()==4){
            g.setColor(Color.CYAN);                           // eau (4)   en cyan 
        }
        if (c.estVivante()==5){
            g.setColor(new Color(84, 38, 9));         // terre  (5) en marron foncé 
        }
        if (c.estVivante()==6){
            g.setColor(new Color(250, 170, 142));     // air (6) en beige clair 
        }
        if (c.estVivante()==7){
            g.setColor(new Color(250, 122, 0));      // feu (7)   en orange 
        }
            g.fillRect(x, y, TAILLE, TAILLE);
        }
}



class VueCommandes extends JPanel {

    private CModele modele;
    public static JButton boutonAvance ;  
    public static JButton boutonHaut ;
    public static JButton boutonGauche ;
    public static JButton boutonDroite ;
    public static JButton boutonBas ;
    public static JButton boutonSeche ;

    public VueCommandes(CModele modele) {
        this.modele = modele;

        JButton boutonAvance = new JButton("Fin de tour");
        this.add(boutonAvance);
        this.boutonAvance= boutonAvance; 
        JButton boutonHaut = new JButton("Haut ");
        this.add(boutonHaut); 
        this.boutonHaut= boutonHaut; 

        JButton boutonGauche = new JButton("Gauche ");
        this.add(boutonGauche); 
        this.boutonGauche= boutonGauche; 

        JButton boutonDroite = new JButton("Droite");
        this.add(boutonDroite); 
        this.boutonDroite= boutonDroite; 

        JButton boutonBas = new JButton("Bas");
        this.add(boutonBas); 
        this.boutonBas= boutonBas; 

        JButton boutonSeche = new JButton("Seche");
        this.add(boutonSeche); 
        this.boutonSeche= boutonSeche; 

        Controleur ctrl = new Controleur(modele);
        Controleur haut= new Controleur(modele);
        Controleur Bas= new Controleur(modele);
        Controleur Droite= new Controleur(modele);
        Controleur Gauche= new Controleur(modele);
        Controleur Seche= new Controleur(modele);

        boutonHaut.addActionListener(haut);
        boutonAvance.addActionListener(ctrl);
        boutonBas.addActionListener(Bas);
        boutonDroite.addActionListener(Droite);
        boutonGauche.addActionListener(Gauche);
        boutonSeche.addActionListener(Seche);

    }
}

class Controleur implements ActionListener {

    CModele modele;
    private static int cpt;        // pour limiter le nombre d'action on implemente un compteur 

    public Controleur(CModele modele) { this.modele = modele; }

    public void actionPerformed(ActionEvent e) {
        Joueur j =modele.Joueuractuel; 
        JButton actionSource = (JButton) e.getSource(); 
        if ( actionSource.equals(VueCommandes.boutonAvance )) {    // lequel se réinitialise a chaque fin de tour 
            modele.avance();
            modele.Joueursuivant(j);
            cpt=0;
    }
        if (cpt>2){
            return ; 
        }
        else if ( actionSource.equals(VueCommandes.boutonHaut)) {   // si ce compteur est a 3 tout autre action que Fin de tour n'aura aucun effet 
            modele.tour("z",j);
            cpt+=1; 
    }
        else if ( actionSource.equals(VueCommandes.boutonGauche)) {
            modele.tour("q",j);
            cpt+=1; 
    }
        else if ( actionSource.equals(VueCommandes.boutonBas)) {
            modele.tour("s",j);
            cpt+=1; 
    }
        else if ( actionSource.equals(VueCommandes.boutonDroite)) {
            modele.tour("d",j);
            cpt+=1; 
    }
        else if ( actionSource.equals(VueCommandes.boutonSeche)) {
            modele.seche(j);
            cpt+=1; 
}
    
}
}
