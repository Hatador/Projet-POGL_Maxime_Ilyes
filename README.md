# Projet-POGL_Maxime_Ilyes

* Nom du binôme : <br/>
Ilyes Tebourski, Maxime Parizot

* Parties du sujet traitées : <br/>
10.1 : 1) 2) 3) ∞) <br/>
10.2 : 1) 2) 3) 4) 5) ∞) <br/>
10.3 : 1) 2) 3) ∞) <br/>
10.4 : échange de clé

* Problèmes toujours présents : <br/>
x <br/>

* Instruction de lancement : <br/>
x <br/>

* Diagramme de classe : <br/>
voir fichier .pdf

* Documentation : <br/>
voir fichier .java

* Règles : <br/>
Une partie se joue à 4 joueurs en coopération <br/>
Chaque joueur peut effectuer 5 actions par tour <br/>
La partie est remportée quand les 4 joueurs se trouvent sur les 4 cases adjacentes à l'hélicoptère (case grise) et que tous les artefacts ont été récupérés <br/>
La partie est perdue quand les 4 cases adjacentes à n'importe quel joueur sont submergées ou que l'une des cases adjacentes à l'hélicoptère est submergée <br/>
La position de chaque artefact et de l'hélicoptère est aléatoire <br/>
L'artefact de feu est orange, de l'air est rose, de la terre est marron et de l'eau est cyan

* Actions : <br/>
Boutons directionnels "haut/bas/gauche/droite" <br/>
Bouton "fin de tour" qui fait passer au prochain joueur, inonde 3 cases au hasard et a une chance de donner une clé <br/>
Bouton "sèche" qui assèche la case sur laquelle le joueur se trouve et les 4 cases adjacentes <br/>
Bouton "récupérer artefact" qui récupère un artefact si le joueur se trouve sur la même case et qu'il a 2 clés du bon type <br/>
Bouton "chercher clé" qui peut donner une clé aléatoire, inonder 3 cases au hasard ou ne rien faire <br/>
Boutons de dons de clés "air/terre/feu/eau" qui transfère la clé choisie au joueur adjacent si strictement 2 joueurs sont côte à côte <br/>

* Choix d'architecture : <br/>

Dans un premier temps, nous avons étudié le code qui nous avait été donné en TP pour nous aider à construire la base de notre interface graphique qui est donc inspiré du fichier Conway.

Au début, nous n'avions donc qu'une grille et un bouton, nous avons choisi une grille plutôt grande avec des cases plutôt petites pour que notre jeu corresponde 
à un jeu lambda et ne soit pas trop facile. Par la suite, nous avons simplement suivit les étapes indiquées dans le sujet en travaillant séparément sur différentes questions mais également en se réunissant régulièrement afin de fusionner nos avancées et parfois se demander de l'aide entre nous pour résoudre des bugs ou des défauts de notre projet.

La plupart des classes sont données via le fichier Conway donc nous n'en avons créé qu'une seul supplémentaire : la classe Joueur dotée des attributs suivant : 

« id » un entier correspondant au numéro du joueur (initialement 1 puis passé de 1 a 4 lorsque nous avons introduit la notion de multijoueur dans notre projet), un tableau de chaîne de caractères correspondant aux clés que possèdent le joueur et un autre tableau de même type correspondant aux artefacts du joueur. 

Nous n'avons pas fusionné les 2 derniers attributs en un "Inventaire" afin de vérifier simplement lors de la récupération d'un artefacts que le joueurs a bien le nombre de clés correspondante nécessaire (pour notre cas, 2 (au lieu de 4) car nous avons voulu ajuster la difficulté du jeu). En effet, si un joueur a le mot clé dans son tableau de clé cela signifie qu'il détient une clé du feu , et s'il a le mot clé feu dans son tableau d'artefacts cela signifie qu'il possède l'artefact du feu. Par 
conséquent, au moment de la remise de l'artefact du feu par exemple il nous suffit de vérifier que le nom de celui ci apparaissent 2 fois dans le tableau de clé du joueur.

Bien que les autre classes nous ait été données, nous les avons énormément modifiées et surtout enrichies, on a par exemple rajouté la classe modèle les attributs Tjoueurs qui est un tableau de joueurs contenant tous les joueurs de la partie , ainsi que Joueuractuel qui correspond au joueur dont c'est le tour et également des méthodes telles que Joueursuivant qui modifie le joueur actuel du modèle pour qu'il laisse la place au suivant ou encore la méthode tour qui permet au joueur dont c'est le tour de se déplacer sur une des 4 cases directement adjacentes à sa position. Nous avons également pourvu toutes les classes de getters et de setters si on en a besoin. 


Fonctionnement général du projet : 

On construit une grille remplie de cellules, lesquelles sont identifiées par les coordonnées x et y. Chaque cellule a un "état" différent représenté par un entier de 0 a 9, 0 voulant dire que la case n'a rien de spécial, 1 qu'elle est inondé , 2 submergée, de 3 a 7 ce sont les case spéciales, elles sont uniques on y retrouve la case hélicoptère ainsi que les 4 case artefacts. 8 et 9 sont des états spéciaux implémentés afin de facilité l'affichage des écrans de victoire et défaite. 

Une case peut également contenir un joueur, et dans notre projet un seul car nous avons choisi des cases de petites tailles (et une grille vaste) donc l'affichage de plusieurs joueurs dans la même case serait illisible et nécessiterait une refonte complète l'affichage de notre jeu.

Suivant ces informations, on attribut à la case une couleur et parfois des inscriptions selon son état et le joueur qu'elle contient grâce a la fonction paint. 

Un joueur n'a pas d'attributs position mais il est initialisé à une case dans la fonction init appelé par le constructeur de modèle donc il y a en permanence une et une seul case qui contient ce joueur. Une fois que les joueurs ont été placés, on place aléatoirement les cases spéciales en vérifiant qu'elles ne tombe pas sur des cases occupé par un joueur ou déjà submergée car nous avons fait le choix de submerger toute la périphérie de notre grille afin de rappeler le fait que les joueurs sont sur une île ainsi que d’éviter les erreur de dépassement lors des déplacements des joueurs. 

L'interface joueur ne change jamais mais lorsque qu'un joueur fait une action non valide un message d'erreur apparaît lui expliquant, par exemple, si un joueur veut se déplacer alors que son tour est fini le message "Votre tour est fini" s'affiche et l'action n'a aucun effet.
Cette interface est composée de plusieurs boutons correspondant a toutes les actions possibles ainsi que des informations dont le joueur a besoin, comme son inventaire. Pour créer cette interface, nous avons utilisé des JPanels ainsi que des JLabels que nous avons a chaque fois disposée de manière différentes grâce a la méthode set Layout de la classe JPanel. 
