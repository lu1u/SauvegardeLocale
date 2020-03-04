package com.lpi.sauvegardelocale.utils;

/***
* Ensemble des commandes partagees entre le client et le adresse
*
* Format des commandes: COMMANDE sur la premiere ligne (println, readLine
* 1ere ligne
*
*******************************************************************************/

public class Protocole
{
	///////////////////////////////////////////////////////////////////////////////////////////////
	// Reponses du adresse
	public static final String REPONSE_OUI	= "O";
	public static final String REPONSE_NON = "N";


	///////////////////////////////////////////////////////////////////////////////////////////////
	// Test de l'adresse, juste pour verifier que ca marche:
	// Reponse du adresse: REPONSE_OUI (si ca marche pas, soit on ne recoit pas la requete, soit
	// on ne peut pas répondre)
	public static final String TEST_ADRESSE = "TESTADRESSE";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Demande la liste des caracteres invalides pour les noms de fichiers sur le systeme du serveur
	// Reponse du adresse: Caracteres invalides
	public static final String INVALIDES_CHEMIN = "INVALIDESCHEMIN";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Creation de repertoire: 
	// 2-parametre nom de repertoire sur la deuxieme ligne (readLine)
	// Reponse du adresse: REPONSE_OUI ou REPONSE_NON
	public static final String CREER_REPERTOIRE = "CREERREPERTOIRE";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Verifier qu'un fichier existe: 
	// 2-parametre nom de fichier sur la deuxieme ligne (readLine)
	// Reponse du adresse: REPONSE_OUI ou REPONSE_NON
	public static final String FICHIER_EXISTE = "FICHIEREXISTE";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Verifier qu'un repertoire existe: 
	// 2-parametre nom de repertoire sur la deuxieme ligne (readLine)
	// Reponse du adresse: REPONSE_OUI ou REPONSE_NON
	public static final String REPERTOIRE_EXISTE = "REPERTOIREEXISTE";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Transfert fichier: 
	// 2- parametre nom de fichier sur la deuxieme ligne (readLine)
	// 3- longueur du fichier (en mode textuel) sur la troisieme ligne
	// 4- contenu du fichier
	// Reponse du adresse: REPONSE_OUI ou REPONSE_NON (si pb pour creer le fichier)
	public static final String TRANSFERT_FICHIER = "TRANSFERTFICHIER";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Transfert un fichier et modifie sa date
	// 2- parametre nom de fichier sur la deuxieme ligne (readLine)
	// 3 -date (millisecondes java)
	// 4- longueur du fichier (en mode textuel) sur la troisieme ligne
	// 5- contenu du fichier
	// Reponse du adresse: REPONSE_OUI ou REPONSE_NON (si pb pour creer le fichier)
	public static final String TRANSFERT_FICHIER_DATE = "TRANSFERTFICHIERDATE";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Modifie la date d'un fichier:
	// 2- parametre nom de fichier sur la deuxieme ligne (readLine)
	// 3- date (millisecondes java)
	public static final String SET_DATE = "SETDATE";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Fermer la connexion
	public static final String FERMER = "FERMER";

	////////////////////////////////////////////////////////////////////////////////////////////////
	// Obtient la liste des fichiers d'un repertoire
	// 2 - repertoire
	// 3 - filtre (ex: *.txt)
	public static final String LISTE_FICHIERS = "LISTEFICHIERS";
	////////////////////////////////////////////////////////////////////////////////////////////////
	// Obtient la liste des sous repertoires d'un repertoire
	// 2 - repertoire
	public static final String LISTE_REPERTOIRES= "LISTEREPERTOIRES";

	///////////////////////////////////////////////////////////////////////////////////////////////
	// Download fichier:
	// 2- parametre nom de fichier sur la deuxieme ligne (readLine)
	// Reponse du serveur:
	//  longueur du fichier (en mode textuel) sur la troisieme ligne
	//  contenu du fichier
	public static final String DOWNLOAD_FICHIER = "DOWNLOADFICHIER";
}