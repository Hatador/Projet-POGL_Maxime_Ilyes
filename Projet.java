//Projet

import java.util.*;
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

    public static final int HAUTEUR=40, LARGEUR=40;     // on choisit ici la taille de la grille 

    private Cellule[][] cellules; // contient toutes les cellules 
    public  ArrayList<Joueur> Tjoueurs ; // tableau contenant tout les joueurs 
    public Joueur Joueuractuel ; 
    public JPanel msge; 
    public JLabel message; // message afficher variable (en haut a gauche)

    public CModele() {   // constructeur du modele 

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
        
        this.Joueuractuel=J1; // On commmence par le joueur 1 
        for(int i=2;i<=5;i++){
            cellules[i][i].j=Tjoueurs.get(i-2);    // on place les jouuers en diagonale dans le coin en haut a gauche 
        }

        // placement de l'helico + artefacts 
        Random r = new Random();
        int low = 5;
        int high = LARGEUR-5;   // la fenetre de tirage a ete reduite pour ne pas placer un objet sur un coin 
        for (int k=3;k<=7;k++){
            int i = r.nextInt(high-low) + low;
            int j = r.nextInt(high-low) + low;
            while (cellules[i][j].etat >1 && cellules[i][j].j != null  ){    // on  verifie que la case tiré soit valide sinon on en tire une autre 
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
        message = new JLabel ("Prenez garde cette ile semble pour le moins ... instable "); 
        message.setForeground(Color.RED);
        msge.add(message); 
        
    }

    public void avance() { 
        Random r = new Random();
        int low = 1; //(commit du 25/04: j'ai juste fait passer cette valeur de 0 à 1 car j'avais oublié de la re-modifer, j'ai rien modifié d'autre)
        int high = LARGEUR;

        for(int k=1; k <= 3; k++){          // on tire une cellule au hasard 
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
    public Cellule emplacementjoueur(Joueur J){    // on parcourt la grille et on renvoit la cellule ou se trouve le joueur J
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
    public void tour(String a,Joueur J){               // on deplace le joueur J sur une des 4 directions dans une case libre
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
        throw new IllegalStateException("Case bloquee ");  // si la case n'est pas valide on leve une erreur 
    }
       notifyObservers();
    }

    public void seche(Joueur J){
        
        Cellule c = emplacementjoueur(J);                                               // on asseche la case du jouer + les 4 cases adjacentes 
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

        notifyObservers();                                                  // on oublie pas d'informer les observateurs a chaque mosifications 
    }

    public void Joueursuivant(Joueur j ){               // methode pour passer au joueur suivant 
        int i =0; 
        while (this.Tjoueurs.get(i)!=j){                        // on parcourt le tableaud de joueurs pour trouver l'emplacement du joueur en argument 
            i++;  
        }
        if (i==this.Tjoueurs.size()-1 ){                      // si le joueurs est le dernier on repasse au premier du tableau 
            this.Joueuractuel=this.Tjoueurs.get(0); 
        }
        else{
            this.Joueuractuel=this.Tjoueurs.get(i+1);          // sinon on passe au suivant 
        }
    }



    public void recupereartefact(Joueur J, Cellule C){
        switch (C.etat) {   
        //pour chaque artefact on verifie que le joueur possede bien les clés sinon on leve un erreur 
            case 4 : if (J.possede2cle("eau")){J.recoiteartefact("eau");C.etat=0;J.enleve2cle("eau");this.message.setText("Vous avez obtenu l'artefact de l'eau");}
            else {this.message.setText("Clef manquante (eau) ");throw new IllegalStateException("C"); }; break ;
            case 5 :  if (J.possede2cle("terre")){J.recoiteartefact("terre");C.etat=0;J.enleve2cle("terre");this.message.setText("Vous avez obtenu l'artefact de la terre");}else {
                this.message.setText("Clef manquante (terre) ");throw new IllegalStateException("C"); }; break ;
            case 6 : if (J.possede2cle("air")){J.recoiteartefact("air");C.etat=0;J.enleve2cle("air");this.message.setText("Vous avez obtenu l'artefact de l'air");}else {
                this.message.setText("Clef manquante (air) ");throw new IllegalStateException("C"); }; break ;
            case 7 : if (J.possede2cle("feu")){J.recoiteartefact("feu");C.etat=0;J.enleve2cle("feu");this.message.setText("Vous avez obtenu l'artefact du feu");} else {
                this.message.setText("Clef manquante (feu) ");throw new IllegalStateException("C"); }; break ;
            default : this.message.setText("Aucun artefact ici");  throw new IllegalStateException("C"); 
        };               
    }


    public Cellule getCellule(int x, int y) {   //permet de retrouver une cellulles grace a sa position 
        return cellules[x][y];
    }
    public void lancer_de_des(Joueur j ){
        Random r = new Random();
        int low = 1;
        int high = 6;                   // on regle ici la "taille" du dés pour regler la difficulté du jeu 
        int i = r.nextInt(high-low) + low;  // on tire un entier unetre 1 et 6 

        switch(i){
            case 1 : j.recoitcle("feu"); this.message.setText("Vous avez obtenu une clef du feu ");break; 
            case 2 : j.recoitcle("eau"); this.message.setText("Vous avez obtenu une clef de l'eau");break;
            case 3 : j.recoitcle("air"); this.message.setText("Vous avez obtenu une clef de l'air");;break; 
            case 4 : j.recoitcle("terre"); this.message.setText("Vous avez obtenu une clef de la terre");;break;
            default : this.message.setText("Vous n'avez rien obtenu");break;      // et selon l'entier obtenu ou effectue l'action correspondante 
        }; 
    }

    public void lancer_de_des2(Joueur j ){
        Random r = new Random();
        int low = 1;
        int high = 7;                   //meme principe ici que pour la premiere fonction 
        int i = r.nextInt(high-low) + low;

        switch(i){
            case 1 : j.recoitcle("feu"); this.message.setText("Vous avez obtenu une clef du feu ");break; 
            case 2 : j.recoitcle("eau"); this.message.setText("Vous avez obtenu une clef de l'eau");break;
            case 3 : j.recoitcle("air"); this.message.setText("Vous avez obtenu une clef de l'air");;break; 
            case 4 : j.recoitcle("terre"); this.message.setText("Vous avez obtenu une clef de la terre");;break;
            case 5 : avance();this.message.setText("Vous avez declenche une montee des eaux !");break;
            default :this.message.setText("...Mais rien ne se passe");break; 
        }; 
    }


    public Cellule emplacementHelico(){           //methode qui permet de retrouver la place de l'Helico 
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
    public void couleurbords(int c){       // methode permanter de coloriers les bord (pour les ercan de victoire/défaite)
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
    public boolean conditionDefaite(Joueur J){   // methode permettant de voir si la partie est perdu 

        
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

    public boolean conditionVictoire(){   // methode permettant de voir si la partie est gagné 
        
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

    public void echangecle(Joueur J, String cle){    // on introduis ici l'echange de clé entre joueur 
        if(J.possedecle(cle)){

                                                            // a condition qu'il n'y ai qu'un seul joueur dans les case adjacentes 
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
            this.message.setText("mauvaise position"); // s'il ya a plusieur joueurs ou aucun on renvoie un message d'erreur 
            throw new IllegalStateException("C"); }

    }
    else {
        this.message.setText("pas de cle ");
        throw new IllegalStateException("C"); }
}
}

class Joueur {

    private final int id ; 
    private ArrayList<String> cles ; 
    private ArrayList<String> artefacts ; 

    public Joueur (CModele modele,int id){   // constructeur de la classe 
        this.id= id; 
        this.cles= new ArrayList<String>(); 
        this.artefacts=new ArrayList<String>(); 
    }
    public int getidjoueur(){       // getters 
        return this.id; 
    } 
    public ArrayList<String> getcles(){
        return this.cles ; 
    }
    public void recoitcle(String cle ){  // setters 
        this.cles.add(cle); 
    }
    public void enlevecle(String cle){
        this.cles.remove(cle); 
    }
    public boolean possedecle(String cle){  // methode pour savoir si le joueur possede une clé 
        for(int i=0;i<this.cles.size();i++){
            if (this.cles.get(i)==cle){
                return true ; 
            }
        }return false ; 
    }

    public void enleve2cle(String cle){           // version améliorer des fonctions précedente pour verifier que le jour a bien 2 clés du type voulu  
        this.cles.remove(cle); 
        this.cles.remove(cle);
    }

    public boolean possede2cle(String cle){
        for(int i=0;i<this.cles.size();i++){
            if (this.cles.get(i)==cle){
                this.cles.remove(cle); 
                for(int j=0;j<this.cles.size();j++){
                    if (this.cles.get(j)==cle){
                        this.cles.add(cle);
                        return true;
                        }
                }
                this.cles.add(cle);
                
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
    public ArrayList<String> getartefacts(){      // encore des getters / setters
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

    protected int etat;                 /// variant entre 0 et 9 il coorespond a l'état de la case (submergé , vide, helicoptere etc......)
    private final int x, y;
    public Joueur j ; 
    public Cellule(CModele modele, int x, int y,Joueur j ) {  // constructeur 
        this.modele = modele;
        this.etat = 0;
        this.x = x; this.y = y;
    }




    public int donneEtat() {     // getters/setters 
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
                                            // construction de la fentetre 
        frame = new JFrame();
        frame.setTitle("Projet Java"); 

        frame.setLayout(new BorderLayout(50,50));  // disposition des élements 
        grille = new VueGrille(modele);                       // création de la grille 
        frame.getContentPane().add(grille, BorderLayout.WEST);
        commandes = new VueCommandes(modele);                  // création de l'interface joueur 
        frame.getContentPane().add(commandes, BorderLayout.CENTER);

        frame.setResizable(false);
        frame.add(modele.msge, BorderLayout.NORTH);
        frame.pack();
        frame.setLocationRelativeTo(null);                         
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    
}



class VueGrille extends JPanel implements Observer {

    private CModele modele;

    private final static int TAILLE = 15;     // taille d'une cellule 


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

    private void paint(Graphics g, Cellule c, int x, int y) {    // colorie les cases selont sont état si un joueur s'y trouve 


        if (c.donneEtat() == 1) {
            g.setColor(new Color(50, 150, 255));   // case inoncé (1) en bleu clair 
            g.fillRect(x, y, TAILLE, TAILLE);
        } 
        if (c.donneEtat() == 2) {
            g.setColor(Color.BLUE);                           // case submergé (2) en bleu 
            g.fillRect(x, y, TAILLE, TAILLE);
        }
        if (c.donneEtat() == 0) {
            g.setColor(Color.WHITE);                       // case basique (0) en blanc 
            g.fillRect(x, y, TAILLE, TAILLE);
        }
        if (c.donneEtat() == 3) {                     // helico (3) en gris foncé 
            g.setColor(Color.darkGray);
            g.fillRect(x, y, TAILLE, TAILLE);
            g.setColor(Color.RED);                                      
            g.drawString("H",x+TAILLE*1/3,y+TAILLE*2/3); 
        }

        if (c.donneEtat()==4){// eau (4)   en cyan 
            g.setColor(Color.CYAN);
            g.fillRect(x, y, TAILLE, TAILLE);
            g.setColor(Color.BLUE);                                      
            g.drawString("E",x+TAILLE*1/3,y+TAILLE*2/3); 
        }
        if (c.donneEtat()==5){// terre  (5) en marron  

            g.setColor(new Color(160, 82, 45));
            g.fillRect(x, y, TAILLE, TAILLE);
            g.setColor(Color.BLACK);                                      
            g.drawString("T",x+TAILLE*1/3,y+TAILLE*2/3); 
        }
        if (c.donneEtat()==6){
            g.setColor(new Color(250, 170, 142));     // air (6) en beige clair 
            g.fillRect(x, y, TAILLE, TAILLE);
            g.setColor(Color.WHITE);                                      
            g.drawString("A",x+TAILLE*1/3,y+TAILLE*2/3); 
        }
        if (c.donneEtat()==7){
            g.setColor(new Color(250, 122, 0));      // feu (7)   en orange 
            g.fillRect(x, y, TAILLE, TAILLE);
            g.setColor(Color.RED);                                      
            g.drawString("F",x+TAILLE*1/3,y+TAILLE*2/3); 
        }
        if (c.donneEtat()==8){       // alteration des cellules quand la partie est perdu 
            g.setColor(Color.RED);      
            g.fillRect(x, y, TAILLE, TAILLE);
        }
        if (c.donneEtat()==9){   // alteration des cellules quand la partie est gagné 
            g.setColor(Color.GREEN);      
            g.fillRect(x, y, TAILLE, TAILLE);
        }

        for (int i=0;i<modele.Tjoueurs.size();i++){
            if (c.contientjoueur(modele.Tjoueurs.get(i)) && modele.Tjoueurs.get(i)!=modele.Joueuractuel){
                g.setColor(Color.BLACK);
                g.fillRect(x, y, TAILLE, TAILLE);                          // alteration des cellules dans lesquelles se trouve les jouueurs autre que le joueurs actuels 
                if(c.donneEtat() == 1){
                    g.setColor(new Color(25,100,150));
                    g.fillRect(x, y, TAILLE, TAILLE);
                }
                if(c.donneEtat() == 2){
                    g.setColor(new Color(0,50,100));
                    g.fillRect(x, y, TAILLE, TAILLE);
                }
                if (c.donneEtat()==4){
                    g.setColor(new Color(0,204,204));    
                    g.fillRect(x, y, TAILLE, TAILLE);                       
                }
                if (c.donneEtat()==5){
                    g.setColor(new Color(54, 18, 0));   
                    g.fillRect(x, y, TAILLE, TAILLE);      
                }
                if (c.donneEtat()==6){
                    g.setColor(new Color(180, 120, 100));
                    g.fillRect(x, y, TAILLE, TAILLE);     
                }
                if (c.donneEtat()==7){
                    g.setColor(new Color(200, 100, 0));  
                    g.fillRect(x, y, TAILLE, TAILLE);    
                }
                switch(i+1){
                    case 1 : g.setColor(Color.YELLOW); g.drawString("J1",x+TAILLE*1/4,y+TAILLE*2/3);break;
                    case 2 : g.setColor(Color.GREEN); g.drawString("J2",x+TAILLE*1/4,y+TAILLE*2/3);break;
                    case 3 : g.setColor(Color.RED); g.drawString("J3",x+TAILLE*1/4,y+TAILLE*2/3);break;
                    case 4 : g.setColor(Color.WHITE); g.drawString("J4",x+TAILLE*1/4,y+TAILLE*2/3);break;
                };

            }
        }
        if (c.contientjoueur(modele.Joueuractuel)){   // alteration de la cellule ou se trouve le jouueur actuelle 
            if (c.donneEtat()==0){
            g.setColor(Color.RED);
            g.fillRect(x, y, TAILLE, TAILLE);
            }

            if(c.donneEtat() == 1){
                g.setColor(new Color(175, 25, 25));
                g.fillRect(x, y, TAILLE, TAILLE);
            }
            if(c.donneEtat() == 2){
                g.setColor(new Color(100,25,25));
                g.fillRect(x, y, TAILLE, TAILLE);
            }
            
            switch(modele.Joueuractuel.getidjoueur()){
                case 1 : g.setColor(Color.PINK); g.drawString("J1",x+TAILLE*1/4,y+TAILLE*2/3);break;
                case 2 : g.setColor(Color.PINK); g.drawString("J2",x+TAILLE*1/4,y+TAILLE*2/3);break;
                case 3 : g.setColor(Color.PINK); g.drawString("J3",x+TAILLE*1/4,y+TAILLE*2/3);break;
                case 4 : g.setColor(Color.PINK); g.drawString("J4",x+TAILLE*1/4,y+TAILLE*2/3);break;
            }
        }
        
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

    public VueCommandes(CModele modele) {             // creation du tablau de commande 
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

        JLabel actionsRestantes = new JLabel("Actions Restantes : 5");
        Actions.add(actionsRestantes); 
        this.actionsRestantes= actionsRestantes ; 


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

        this.add(Actions);   // et on ajoute les actions a l'interface 


        JPanel Info = new JPanel();   // et enfin on ajoute les inforamtions necessaires aux joueurs 
        Info.setLayout(new BorderLayout(30,30)); 

        JLabel tourdujoueur = new JLabel("C'est le tour du joueur 1");
        Info.add(tourdujoueur,BorderLayout.WEST); 
        this.tourdujoueur= tourdujoueur ; 


        JLabel inventaire = new JLabel("<html>" + "Listes des objets :<br/> "+
        "JOUEUR1 : " + "Clefs : " + "[]" + "  Artefacts : " + "[]"+"<br/>" +"<br/>"+
        "JOUEUR2 : " + "Clefs : " + "[]" + "  Artefacts : " + "[]"+"<br/>" +"<br/>" +
        "JOUEUR3 : " + "Clefs : " + "[]" + "  Artefacts : " + "[]"+"<br/>" +"<br/>" +
        "JOUEUR4 : " + "Clefs : " + "[]" + "  Artefacts : " + "[]" +"<html>");
        Info.add(inventaire,BorderLayout.CENTER); 
        this.inventaire= inventaire ; 
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
    private static int cpt = 5;        // pour limiter le nombre d'action on implemente un compteur 
    private static int numjoueur = 1;
    

    public Controleur(CModele modele) { this.modele = modele; }

    public void actionPerformed(ActionEvent e) {

        Joueur j =modele.Joueuractuel; 
        JButton actionSource = (JButton) e.getSource(); 
        
        if ( actionSource.equals(VueCommandes.boutonAvance )) {    // lequel se réinitialise a chaque fin de tour 
            modele.avance();
            modele.lancer_de_des(j);
            modele.Joueursuivant(j);
            cpt=5;
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
                                                                                       // pour chaque action invalide on prend bien soin de ne pas décrémenter le compteur 
            
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

        VueCommandes.inventaire.setText("<html>" + "Listes des objets :<br/> "+   // on met a jour l'inventaire des joueurs affiché a chaque action 
            "JOUEUR1 : " + "Clefs : " + modele.Tjoueurs.get(0).getcles() + "  Artefacts : " + modele.Tjoueurs.get(0).getartefacts()+"<br/>" +"<br/>" +
            "JOUEUR2 : " + "Clefs : " + modele.Tjoueurs.get(1).getcles() + "  Artefacts : " + modele.Tjoueurs.get(1).getartefacts()+"<br/>" +"<br/>" +
            "JOUEUR3 : " + "Clefs : " + modele.Tjoueurs.get(2).getcles() + "  Artefacts : " + modele.Tjoueurs.get(2).getartefacts()+"<br/>" +"<br/>" +
            "JOUEUR4 : " + "Clefs : " + modele.Tjoueurs.get(3).getcles() + "  Artefacts : " + modele.Tjoueurs.get(3).getartefacts() +
            "</html>");
    VueCommandes.actionsRestantes.setText("Actions Restantes : " + cpt);

        if(modele.conditionDefaite(j)){
                                  
            VueCommandes.boutonAvance.setText("PARTIE PERDUE"); // a la fin de la partie on désactive tout les boutons 
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
            modele.message.setText("PARTIE PERDUE !");



    }
    if(modele.conditionVictoire()){
        VueCommandes.boutonAvance.setText("PARTIE GAGNE !!!!!");
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
        modele.message.setText("PARTIE GAGNE !");
        
}

}
}
