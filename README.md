# Projet-POGL_Maxime_Ilyes

* Nom du binôme : <br/>
Ilyes Tebourski, Maxime Parizot

* Parties du sujet traitées : <br/>
10.1 : 1) 2) 3) ∞) <br/>
10.2 : 1) 2) 3) 4) 5) ∞) <br/>
10.3 : 1) 2) 3) ∞) <br/>
10.4 : échange de clé

* Choix d'architecture : <br/>
(à compléter)

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
