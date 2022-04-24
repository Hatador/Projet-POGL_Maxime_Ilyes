//Projet

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.lang.ProcessHandle.Info;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI.ActionScroller;


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
    public JPanel msge; 
    public JLabel message; 

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
        //creation de l'ile(=submergement des cotés)
        //outre le coté realiste d'une telle grille ceci evite que le joueur aille en dehors de la liste vu qu'il ne pêut pas aller sur des cases submérgés
        int a=1; 
        for(int i=0;i<=LARGEUR;i++){
            cellules[i][a].etat=2; 
            cellules[i][HAUTEUR-a+1].etat=2; 
        }

        for(int i=0;i<=HAUTEUR;i++){
            cellules[a][i].etat=2; 
            cellules[LARGEUR-a+1][i].etat=2; 
        }
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

        for(int i=2;i<=5;i++){
            cellules[i][i].j=Tjoueurs.get(i-2); 
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


        // placement du message de bienvenue et du message variable  :

        msge= new JPanel();                                        
        JLabel msg= new JLabel("BIENVENUE EXPLORATEURS !");      
        msg.setFont(new Font(" Serif",Font.BOLD,20));
        msg.setBounds(50,20,100,40);
        msge.add(msg); 
        message = new JLabel ("Prenez Garde cet ile semble pour le moins ... instable "); 
        message.setForeground(Color.RED);
        msge.add(message); 
        
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
        if (c1.etat != 2 && c1.etat != 3 && c1.getjoueur() == null){
        c1.ajoutejoueur(c.getjoueur()); 
        c.enlevejoueur();
    } else{
        this.message.setText("Case bloque");
        throw new IllegalStateException("Case bloquee "); 
    }
       notifyObservers();
    }

    public void seche(Joueur J){
        
        Cellule c = emplacementjoueur(J); 
        Cellule c1 = cellules[c.coordx()][c.coordy()-1];
        Cellule c2 = cellules[c.coordx()-1][c.coordy()];
        Cellule c3 = cellules[c.coordx()][c.coordy()+1];
        Cellule c4 = cellules[c.coordx()+1][c.coordy()];

        if (c.etat != 1 && c1.etat != 1 && c2.etat != 1 && c3.etat != 1 && c4.etat != 1){
            this.message.setText("Aucune case a secher ici ");
            throw new IllegalStateException("Aucune case inondee");}

        if (c1.etat == 1){c1.etat = 0;}
        if (c2.etat == 1){c2.etat = 0;}
        if (c3.etat == 1){c3.etat = 0;}
        if (c4.etat == 1){c4.etat = 0;}
        if (c.etat == 1){c.etat = 0;}

        

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
    public void recupereartefact(Joueur J, Cellule C){
        switch (C.etat) {
            case 4 : if (J.possede2cle("eau")){J.recoiteartefact("eau");C.etat=0;J.enleve2cle("eau");}else {
            this.message.setText("Clé manquante (eau) ");throw new IllegalStateException("C"); }; break ;
            case 5 :  if (J.possede2cle("terre")){J.recoiteartefact("terre");C.etat=0;J.enleve2cle("terre");}else {
                this.message.setText("Clé manquante (terre) ");throw new IllegalStateException("C"); }; break ;
            case 6 : if (J.possede2cle("air")){J.recoiteartefact("air");C.etat=0;J.enleve2cle("air");}else {
                this.message.setText("Clé manquante (air) ");throw new IllegalStateException("C"); }; break ;
            case 7 : if (J.possede2cle("feu")){J.recoiteartefact("feu");C.etat=0;J.enleve2cle("feu");} else {
                this.message.setText("Clé manquante (feu) ");throw new IllegalStateException("C"); }; break ;
            default : this.message.setText("Aucun artefact ici");  throw new IllegalStateException("C"); 
        };               
    }


    public Cellule getCellule(int x, int y) {
        return cellules[x][y];
    }
    public void lancer_de_des(Joueur j ){
        Random r = new Random();
        int low = 1;
        int high = 6;                   // on regle ici la "taille" du dés pour regler la difficulté du jeu 
        int i = r.nextInt(high-low) + low;

        switch(i){
            case 1 : j.recoitcle("feu"); this.message.setText("Vous avez obtenu une clef du feu ");break; 
            case 2 : j.recoitcle("eau"); this.message.setText("Vous avez obtenu une clef de l'eau");break;
            case 3 : j.recoitcle("air"); this.message.setText("Vous avez obtenu une clef de l'air");;break; 
            case 4 : j.recoitcle("terre"); this.message.setText("Vous avez obtenu une clef de la terre");;break;
            default : this.message.setText("Vous n'avez rien obtenu");break; 
        }; 
    }

    public void lancer_de_des2(Joueur j ){
        Random r = new Random();
        int low = 1;
        int high = 7;                   // on regle ici la "taille" du dés pour regler la difficulté du jeu 
        int i = r.nextInt(high-low) + low;

        switch(i){
            case 1 : j.recoitcle("feu"); this.message.setText("Vous avez obtenu une clef du feu ");break; 
            case 2 : j.recoitcle("eau"); this.message.setText("Vous avez obtenu une clef de l'eau");break;
            case 3 : j.recoitcle("air"); this.message.setText("Vous avez obtenu une clef de l'air");;break; 
            case 4 : j.recoitcle("terre"); this.message.setText("Vous avez obtenu une clef de la terre");;break;
            case 5 : avance();this.message.setText("Vous avez declencher une montee des eaux !");break;
            default :this.message.setText("...Mais rien ne se passe");break; 
        }; 
    }





    public Cellule emplacementHelico(){
        for(int i=0; i<LARGEUR+2; i++) {
            for(int j=0; j<HAUTEUR+2; j++) {
                if (cellules[i][j].etat == 3){
                    Cellule C= cellules[i][j] ;
                    return C ;   
                }
            }
        }
        return null;

}
    public void couleurbords(int c){
        int a=1; 
        for(int i=0;i<=LARGEUR;i++){
            cellules[i][a].etat=c; 
            cellules[i][HAUTEUR-a+1].etat=c; 
        }

        for(int i=0;i<=HAUTEUR;i++){
            cellules[a][i].etat=c; 
            cellules[LARGEUR-a+1][i].etat=c; 

    }
}

    public boolean conditionDefaite(Joueur J){
        
        Cellule c = emplacementHelico(); 
        Cellule c1 = cellules[c.coordx()][c.coordy()-1];
        Cellule c2 = cellules[c.coordx()-1][c.coordy()];
        Cellule c3 = cellules[c.coordx()][c.coordy()+1];
        Cellule c4 = cellules[c.coordx()+1][c.coordy()];

        if (c1.etat == 2 || c2.etat == 2 || c3.etat == 2 || c4.etat == 2){    
            couleurbords(8); 
            return true;     
        }

        Cellule j = emplacementjoueur(J); 
        Cellule j1 = cellules[j.coordx()][j.coordy()-1];
        Cellule j2 = cellules[j.coordx()-1][j.coordy()];
        Cellule j3 = cellules[j.coordx()][j.coordy()+1];
        Cellule j4 = cellules[j.coordx()+1][j.coordy()];

        if (j1.etat == 2 && j2.etat == 2 && j3.etat == 2 && j4.etat == 2){   
            couleurbords(8);  
            return true;     
        }




        return false;
    }

    public boolean conditionVictoire(){
        
        Cellule c = emplacementHelico(); 
        Cellule c1 = cellules[c.coordx()][c.coordy()-1];
        Cellule c2 = cellules[c.coordx()-1][c.coordy()];
        Cellule c3 = cellules[c.coordx()][c.coordy()+1];
        Cellule c4 = cellules[c.coordx()+1][c.coordy()];

        if (c1.getjoueur() != null && c2.getjoueur() != null && c3.getjoueur() != null && c4.getjoueur() != null){  
            for(int i=0; i<LARGEUR+2; i++) {
                for(int j=0; j<HAUTEUR+2; j++) {
                    if (cellules[i][j].etat>3){return false;}
                }
            }   
            couleurbords(9); 
            return true;     
        }
        return false;
    }

    public void echangecle(Joueur J, String cle){
        if(J.possedecle(cle)){


        Cellule j = emplacementjoueur(J); 
        Cellule j1 = cellules[j.coordx()][j.coordy()-1];
        Cellule j2 = cellules[j.coordx()-1][j.coordy()];
        Cellule j3 = cellules[j.coordx()][j.coordy()+1];
        Cellule j4 = cellules[j.coordx()+1][j.coordy()];

        if(j1.getjoueur() != null ^ j2.getjoueur() != null ^ j3.getjoueur() != null ^ j4.getjoueur() != null){
            J.enlevecle(cle);
            if(j1.getjoueur() != null){
                j1.getjoueur().recoitcle(cle);

            }
            if(j2.getjoueur() != null){
                j2.getjoueur().recoitcle(cle);
                
            }
            if(j3.getjoueur() != null){
                j3.getjoueur().recoitcle(cle);
                
            }
            if(j4.getjoueur() != null){
                j4.getjoueur().recoitcle(cle);
                
            }

        }
        else {
            this.message.setText("mauvaise position");
            throw new IllegalStateException("C"); }

    }
    else {
        this.message.setText("pas de cle ");
        throw new IllegalStateException("C"); }
}
}

class Joueur {

    private CModele modele; 
    private final int id ; 
    protected boolean EnVie; 
    private ArrayList<String> cles ; 
    private ArrayList<String> artefacts ; 

    public Joueur (CModele modele,int id){
        this.modele =modele; 
        this.id= id; 
        this.EnVie=true; 
        this.cles= new ArrayList<String>(); 
        this.artefacts=new ArrayList<String>(); 
    }
    public int getidjoueur(){
        return this.id; 
    } 
    public ArrayList<String> getcles(){
        return this.cles ; 
    }
    public void recoitcle(String cle ){
        this.cles.add(cle); 
    }
    public void enlevecle(String cle){
        this.cles.remove(cle); 
    }
    public boolean possedecle(String cle){
        for(int i=0;i<this.cles.size();i++){
            if (this.cles.get(i)==cle){
                return true ; 
            }
        }return false ; 
    }

    public void enleve2cle(String cle){
        this.cles.remove(cle); 
        this.cles.remove(cle);
    }

    public boolean possede2cle(String cle){
        for(int i=0;i<this.cles.size();i++){
            if (this.cles.get(i)==cle){
                int k=i;
                if(k != this.cles.size()-1){
                    for(int j=k;j<this.cles.size();j++){
                        if (this.cles.get(j)==cle){
                            return true ; 
                        }

                        

                }
                

                }
                
            }
        }return false ; 
    }


    public boolean possedeartefacts(String artefact){
        for(int i=0;i<this.artefacts.size();i++){
            if (this.artefacts.get(i)==artefact){
                return true ; 
            }
        }return false ; 
    }
    public ArrayList<String> getartefacts(){
        return this.artefacts  ; 
    }
    public void recoiteartefact(String artefact ){
            this.artefacts.add(artefact);
    }
    public void enleveartefact(String artefact){
        this.artefacts.remove(artefact); 
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

        frame.setLayout(new BorderLayout(50,50));
        grille = new VueGrille(modele);
        frame.getContentPane().add(grille, BorderLayout.WEST);
        commandes = new VueCommandes(modele);
        frame.getContentPane().add(commandes, BorderLayout.CENTER);

        frame.add(modele.msge, BorderLayout.NORTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
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
            g.setColor(new Color(50, 150, 255));   // case inoncé (1) en bleu clair 
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
        if (c.estVivante()==8){
            g.setColor(Color.RED);      // feu (7)   en orange 
        }
        if (c.estVivante()==9){
            g.setColor(Color.GREEN);      // feu (7)   en orange 
        }


        
        for (int i=0;i<modele.Tjoueurs.size();i++){
            if (c.contientjoueur(modele.Tjoueurs.get(i))){
                g.setColor(Color.BLACK);
                if(c.estVivante() == 1){
                    g.setColor(new Color(25,100,150));
                }
                if(c.estVivante() == 2){
                    g.setColor(new Color(0,50,100));
                }
                if (c.estVivante()==4){
                    g.setColor(new Color(0,204,204));                           
                }
                if (c.estVivante()==5){
                    g.setColor(new Color(54, 18, 0));         
                }
                if (c.estVivante()==6){
                    g.setColor(new Color(180, 120, 100));     
                }
                if (c.estVivante()==7){
                    g.setColor(new Color(200, 100, 0));      
                }



            }
        }
        if (c.contientjoueur(modele.Joueuractuel)){
            g.setColor(Color.RED);
            if(c.estVivante() == 1){
                g.setColor(new Color(175, 25, 25));
            }
            if(c.estVivante() == 2){
                g.setColor(new Color(100,25,25));
            }
            if(c.estVivante() >= 3){
                g.setColor(new Color(137,25,25));
            }
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
    public static JButton boutonRecup; 
    public static JButton boutonchercherCle; 

    public static JLabel actionsRestantes; 
    public static JLabel tourdujoueur; 
    public static JLabel inventaire; 

    public static JButton donneEau ;
    public static JButton donneFeu ;
    public static JButton donneAir ;
    public static JButton donneTerre ;

    public VueCommandes(CModele modele) {
        this.modele = modele;
        this.setLayout(new GridLayout(3,1,20,20) );  // tableau de commande en 1 colonnes et 3 lignes 

        JPanel Deplacements = new JPanel();  // En premier les commandes de deplacements et fin de tour (commandes primaires)
        Deplacements.setLayout(new BorderLayout(10,40));

        JButton boutonAvance = new JButton("Fin de tour");
        Deplacements.add(boutonAvance, BorderLayout.CENTER);
        this.boutonAvance= boutonAvance; 

        JButton boutonHaut = new JButton("Haut ");
        Deplacements.add(boutonHaut, BorderLayout.NORTH);
        this.boutonHaut= boutonHaut; 

        JButton boutonGauche = new JButton("Gauche ");
        Deplacements.add(boutonGauche, BorderLayout.WEST);
        this.boutonGauche= boutonGauche; 

        JButton boutonDroite = new JButton("Droite");
        Deplacements.add(boutonDroite, BorderLayout.EAST);
        this.boutonDroite= boutonDroite; 

        JButton boutonBas = new JButton("Bas");
        Deplacements.add(boutonBas, BorderLayout.SOUTH); 
        this.boutonBas= boutonBas; 

        this.add(Deplacements);   // on ajoute donc deplacement aux commandes 


        JPanel Actions = new JPanel();     // ensuite on introduis les commandes secondaire sous la forme d'un tableau 
        Actions.setLayout(new GridLayout(2,3,30,30)); 


        JButton boutonSeche = new JButton("Seche");
        Actions.add(boutonSeche); 
        this.boutonSeche= boutonSeche; 

        JButton boutonRecup = new JButton("Recuperer artefact");
        Actions.add(boutonRecup); 
        this.boutonRecup= boutonRecup ; 

        JButton boutonchercherCle = new JButton("Chercher clef");
        Actions.add(boutonchercherCle); 
        this.boutonchercherCle= boutonchercherCle ; 

        JButton donneTerre = new JButton("Donner clef terre");
        Actions.add(donneTerre);
        this.donneTerre= donneTerre; 

        JButton donneFeu = new JButton("Donner clef feu");
        Actions.add(donneFeu);
        this.donneFeu= donneFeu; 

        JButton donneAir = new JButton("Donner clef air");
        Actions.add(donneAir);
        this.donneAir= donneAir; 

        JButton donneEau = new JButton("Donner clef eau");
        Actions.add(donneEau);
        this.donneEau= donneEau; 

        JLabel actionsRestantes = new JLabel("Actions Restantes : 3");
        Actions.add(actionsRestantes); 
        this.actionsRestantes= actionsRestantes ; 

        this.add(Actions);   // et on ajoute les actions a l'interface 


        JPanel Info = new JPanel();
        Info.setLayout(new BorderLayout(30,30)); 

        JLabel tourdujoueur = new JLabel("C'est le tour du joueur 1");
        Info.add(tourdujoueur,BorderLayout.WEST); 
        this.tourdujoueur= tourdujoueur ; 



        JLabel inventaire = new JLabel("<html>" + "Listes des objets :<br/> "+
        "JOUEUR1 : " + "Clefs : " + "[]" + "  Artefacts : " + "[]"+"<br/>" + 
        "JOUEUR2 : " + "Clefs : " + "[]" + "  Artefacts : " + "[]"+"<br/>" + 
        "JOUEUR3 : " + "Clefs : " + "[]" + "  Artefacts : " + "[]"+"<br/>" + 
        "JOUEUR4 : " + "Clefs : " + "[]" + "  Artefacts : " + "[]" +"<html>");
        Info.add(inventaire,BorderLayout.CENTER); 
        this.inventaire= inventaire ; 
        //Info.add(Inventaire);
        this.add(Info); 


        Controleur ctrl = new Controleur(modele);
        Controleur haut= new Controleur(modele);
        Controleur Bas= new Controleur(modele);
        Controleur Droite= new Controleur(modele);
        Controleur Gauche= new Controleur(modele);
        Controleur Seche= new Controleur(modele);
        Controleur Recup= new Controleur(modele);
        Controleur Clef= new Controleur(modele);

        Controleur air= new Controleur(modele);
        Controleur feu= new Controleur(modele);
        Controleur eau= new Controleur(modele);
        Controleur terre= new Controleur(modele);

        boutonHaut.addActionListener(haut);
        boutonAvance.addActionListener(ctrl);
        boutonBas.addActionListener(Bas);
        boutonDroite.addActionListener(Droite);
        boutonGauche.addActionListener(Gauche);
        boutonSeche.addActionListener(Seche);
        boutonRecup.addActionListener(Recup);
        boutonchercherCle.addActionListener(Clef);

        donneTerre.addActionListener(terre);
        donneAir.addActionListener(air);
        donneEau.addActionListener(eau);
        donneFeu.addActionListener(feu);
    }
}

class Controleur implements ActionListener {

    CModele modele;
    private static int cpt = 3;        // pour limiter le nombre d'action on implemente un compteur 
    private static int numjoueur = 1;
    

    public Controleur(CModele modele) { this.modele = modele; }

    public void actionPerformed(ActionEvent e) {

        //modele.conditionDefaite();
        

        Joueur j =modele.Joueuractuel; 
        JButton actionSource = (JButton) e.getSource(); 
        
        
        if ( actionSource.equals(VueCommandes.boutonAvance )) {    // lequel se réinitialise a chaque fin de tour 
            modele.avance();
            modele.lancer_de_des(j);
            modele.Joueursuivant(j);
            cpt=3;
            numjoueur++;
            
            if (numjoueur>4){numjoueur=1;}
            VueCommandes.actionsRestantes.setText("Actions Restantes : " + cpt);
            VueCommandes.tourdujoueur.setText("C'est le tour du joueur " + numjoueur);
    }
        if (cpt<1){
            modele.message.setText("Votre tour est fini ");
            return ; 
        }
        else if (actionSource.equals(VueCommandes.boutonRecup)){
            try { modele.recupereartefact(j, modele.emplacementjoueur(j));} catch (IllegalStateException C){ return ; }
            cpt-=1; 
            
        }
        else if ( actionSource.equals(VueCommandes.boutonHaut)) {
            // si ce compteur est a 3 tout autre action que Fin de tour n'aura aucun effet 
            try {modele.tour("z",j);}catch(IllegalStateException c){return ; }
            cpt-=1; 
            
            
    }
        else if ( actionSource.equals(VueCommandes.boutonGauche)) {
            try {modele.tour("q",j);}catch(IllegalStateException c){return ; }
            cpt-=1; 
            
    }
        else if ( actionSource.equals(VueCommandes.boutonBas)) {
            try {modele.tour("s",j);}catch(IllegalStateException c){return ; }
            cpt-=1; 
            
    }
        else if ( actionSource.equals(VueCommandes.boutonDroite)) {
            try {modele.tour("d",j);}catch(IllegalStateException c){return ; }
            cpt-=1; 
            
    }
        else if ( actionSource.equals(VueCommandes.boutonSeche)) {
            try {modele.seche(j);}catch(IllegalStateException c){return ; } 
            cpt-=1; 
            
}
        else if ( actionSource.equals(VueCommandes.boutonchercherCle)) {
            try {modele.lancer_de_des2(j);}catch(IllegalStateException c){return ; }             
            cpt-=1; 
            
        }

        else if ( actionSource.equals(VueCommandes.donneEau)) {
            try {modele.echangecle(j,"eau");}catch(IllegalStateException c){return ; }             
            cpt-=1; 
            
        }

        else if ( actionSource.equals(VueCommandes.donneFeu)) {
            try {modele.echangecle(j,"feu");}catch(IllegalStateException c){return ; }             
            cpt-=1; 
            
        }

        else if ( actionSource.equals(VueCommandes.donneTerre)) {
            try {modele.echangecle(j,"terre");}catch(IllegalStateException c){return ; }             
            cpt-=1; 
            
        }

        else if ( actionSource.equals(VueCommandes.donneAir)) {
            try {modele.echangecle(j,"air");}catch(IllegalStateException c){return ; }             
            cpt-=1; 
            
        }

        VueCommandes.inventaire.setText("<html>" + "Listes des objets :<br/> "+
            "JOUEUR1 : " + "Clefs : " + modele.Tjoueurs.get(0).getcles() + "  Artefacts : " + modele.Tjoueurs.get(0).getartefacts()+"<br/>" +
            "JOUEUR2 : " + "Clefs : " + modele.Tjoueurs.get(1).getcles() + "  Artefacts : " + modele.Tjoueurs.get(1).getartefacts()+"<br/>" +
            "JOUEUR3 : " + "Clefs : " + modele.Tjoueurs.get(2).getcles() + "  Artefacts : " + modele.Tjoueurs.get(2).getartefacts()+"<br/>" +
            "JOUEUR4 : " + "Clefs : " + modele.Tjoueurs.get(3).getcles() + "  Artefacts : " + modele.Tjoueurs.get(3).getartefacts() +
            "</html>");
    VueCommandes.actionsRestantes.setText("Actions Restantes : " + cpt);

        if(modele.conditionDefaite(j)){
            VueCommandes.inventaire.setText("PARTIE PERDUE");
            VueCommandes.boutonAvance.setText("PARTIE PERDUE");
            VueCommandes.boutonAvance.setEnabled(false);
            VueCommandes.boutonHaut.setEnabled(false);
            VueCommandes.boutonDroite.setEnabled(false);
            VueCommandes.boutonGauche.setEnabled(false);
            VueCommandes.boutonBas.setEnabled(false);
            VueCommandes.boutonSeche.setEnabled(false);
            VueCommandes.boutonchercherCle.setEnabled(false);
            VueCommandes.boutonRecup.setEnabled(false);
            VueCommandes.actionsRestantes.setEnabled(false);
            VueCommandes.tourdujoueur.setEnabled(false);
            VueCommandes.inventaire.setEnabled(false);
            VueCommandes.donneEau.setEnabled(false);
            VueCommandes.donneFeu.setEnabled(false);
            VueCommandes.donneTerre.setEnabled(false);
            VueCommandes.donneAir.setEnabled(false);
    }
    if(modele.conditionVictoire()){
        VueCommandes.inventaire.setText("PARTIE GAGNE !!!!!!!!!!!!!!!!");
        VueCommandes.boutonAvance.setText("PARTIE GAGNE !!!!!!!!!!!!!!!!");
        VueCommandes.boutonAvance.setEnabled(false);
        VueCommandes.boutonHaut.setEnabled(false);
        VueCommandes.boutonDroite.setEnabled(false);
        VueCommandes.boutonGauche.setEnabled(false);
        VueCommandes.boutonBas.setEnabled(false);
        VueCommandes.boutonSeche.setEnabled(false);
        VueCommandes.boutonchercherCle.setEnabled(false);
        VueCommandes.boutonRecup.setEnabled(false);
        VueCommandes.actionsRestantes.setEnabled(false);
        VueCommandes.tourdujoueur.setEnabled(false);
        VueCommandes.inventaire.setEnabled(false);
        VueCommandes.donneEau.setEnabled(false);
        VueCommandes.donneFeu.setEnabled(false);
        VueCommandes.donneTerre.setEnabled(false);
        VueCommandes.donneAir.setEnabled(false);
}
    
        

    




    
}
}

