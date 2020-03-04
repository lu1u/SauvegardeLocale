using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Serveur_Sauvegarde_Locale
{
    class RequestProcessor
    {
        public static int nbRequetes = 0;
        #region PROTOCOLE
        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Reponses du serveur
        public const string REPONSE_OUI = "O";
        public const string REPONSE_NON = "N";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Test de l'adresse, juste pour verifier que ca marche:
        // Reponse du serveur: REPONSE_OUI (si ca marche pas, soit on ne recoit pas la requete, soit
        // on ne peut pas répondre)
        public const string TEST_ADRESSE = "TESTADRESSE";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Demande la liste des caracteres invalides pour les noms de fichiers sur le systeme du serveur
        // Reponse du adresse: Caracteres invalides
        public const string INVALIDES_CHEMIN = "INVALIDESCHEMIN";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Creation de repertoire:
        // 2-parametre nom de repertoire sur la deuxieme ligne (readLine)
        // Reponse du serveur: REPONSE_OUI ou REPONSE_NON
        public const string CREER_REPERTOIRE = "CREERREPERTOIRE";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Verifier qu'un fichier existe:
        // 2-parametre nom de fichier sur la deuxieme ligne (readLine)
        // Reponse du serveur: REPONSE_OUI ou REPONSE_NON
        public const string FICHIER_EXISTE = "FICHIEREXISTE";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Verifier qu'un repertoire existe:
        // 2-parametre nom de repertoire sur la deuxieme ligne (readLine)
        // Reponse du serveur: REPONSE_OUI ou REPONSE_NON
        public const string REPERTOIRE_EXISTE = "REPERTOIREEXISTE";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Upload fichier:
        // 2- parametre nom de fichier sur la deuxieme ligne (readLine)
        // 3- longueur du fichier (en mode textuel) sur la troisieme ligne
        // 4- contenu du fichier
        // Reponse du serveur: REPONSE_OUI ou REPONSE_NON (si pb pour creer le fichier)
        public const string UPLOAD_FICHIER = "TRANSFERTFICHIER";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Transfert un fichier et modifie sa date
        // 2- parametre nom de fichier sur la deuxieme ligne (readLine)
        // 3 -date (millisecondes java)
        // 4- longueur du fichier (en mode textuel) sur la troisieme ligne
        // 5- contenu du fichier
        // Reponse du serveur: REPONSE_OUI ou REPONSE_NON (si pb pour creer le fichier)
        public const string TRANSFERT_FICHIER_DATE = "TRANSFERTFICHIERDATE";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Modifie la date d'un fichier:
        // 2- parametre nom de fichier sur la deuxieme ligne (readLine)
        // 3- date (millisecondes java)
        public const string CHANGE_DATE = "SETDATE";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Download fichier:
        // 2- parametre nom de fichier sur la deuxieme ligne (readLine)
        // Reponse du serveur: 
        //  longueur du fichier (en mode textuel) sur la troisieme ligne
        //  contenu du fichier
        public const string DOWNLOAD_FICHIER = "DOWNLOADFICHIER";

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Fermer la connexion
        public const string FERMER = "FERMER";

        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Obtient la liste des fichiers d'un repertoire
        // 2 - repertoire
        // 3 - filtre (ex: *.txt)
        public const string LISTE_FICHIERS = "LISTEFICHIERS";
        ////////////////////////////////////////////////////////////////////////////////////////////////
        // Obtient la liste des sous repertoires d'un repertoire
        // 2 - repertoire
        public const string LISTE_REPERTOIRES = "LISTEREPERTOIRES";
        #endregion


        ///////////////////////////////////////////////////////////////////////////////////////////////
        /// <summary>
        /// Traite une requete
        /// </summary>
        /// <param name="client"></param>
        ///////////////////////////////////////////////////////////////////////////////////////////////
        public static void traiteRequete(object client)
        {
            nbRequetes++;
            Mainform.UpdateNbRequetes();
            try
            {
                bool continuer = true;
                TcpClient tcpClient = client as TcpClient;
                NetworkStream clntStream = tcpClient.GetStream();
                SocketUtils clientStream = new SocketUtils(clntStream);
                //do
                //{
                string commande = clientStream.LireLigne();
                if (commande?.Length > 0)
                {
                    Mainform.WriteMessageToConsole($"Requête: {commande}");

                    switch (commande)
                    {
                        case TEST_ADRESSE: testAdresse(clientStream); break;
                        case CREER_REPERTOIRE: creerRepertoire(clientStream); break;
                        case FICHIER_EXISTE: fichierExiste(clientStream); break;
                        case UPLOAD_FICHIER: transferFichier(clientStream); break;
                        case TRANSFERT_FICHIER_DATE: transferFichierDate(clientStream); break;
                        case CHANGE_DATE: setDate(clientStream); break;
                        case INVALIDES_CHEMIN: caracteresInvalidesChemin(clientStream); break;
                        case FERMER: continuer = false; break;
                        case LISTE_FICHIERS: listeFichiers(clientStream); break;
                        case LISTE_REPERTOIRES: listeRepertoires(clientStream); break;
                        case DOWNLOAD_FICHIER: downloadFichier(clientStream); break;
                        default:
                            Mainform.WriteErrorToConsole($"Commande inconnue: '{commande}'"); break;
                    }
                }
                //}
                //while (continuer);
                Mainform.WriteMessageToConsole("Socket closed");
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans le traitement de la requête");
                Mainform.WriteExceptionToConsole(e);
            }

            nbRequetes--;
            Mainform.UpdateNbRequetes();
        }

        /// <summary>
        /// Download fichier
        /// </summary>
        /// <param name="clientStream"></param>
        private static void downloadFichier(SocketUtils clientStream)
        {
            string result = REPONSE_NON;
            string fichier = clientStream.LireChemin();
            string path = Path.Combine(Settings.Default.RepertoireDeBase, fichier);
            if (checkPath(ref path))
            {
                bool existe = File.Exists(path);
                if (existe)
                {
                    Mainform.WriteMessageToConsole($"{DOWNLOAD_FICHIER} Chemin : {path}");
                    clientStream.EcritLigne(REPONSE_OUI);
                    FileStream fileStream = new FileStream(path, FileMode.Open);
                    if (fileStream != null)
                    {
                        long taille = fileStream.Length;

                        // Ecrire la taille
                        clientStream.EcritLigne(taille.ToString());

                        // Copier le fichier
                        clientStream.EcritFichier(taille, fileStream);
                        fileStream.Close();
                        result = REPONSE_OUI;
                    }
                    else
                        Mainform.WriteMessageToConsole($"{DOWNLOAD_FICHIER} Chemin : {path} fichier non trouvé!");
                }
                else
                    Mainform.WriteMessageToConsole($"{DOWNLOAD_FICHIER} Chemin : {path} fichier non trouvé!");
            }

            clientStream.EcritLigne(result);
        }

        private static object FileInfo(string path)
        {
            throw new NotImplementedException();
        }

        private static bool isConnected(TcpClient tcpClient)
        {
            TcpState state = GetState(tcpClient);
            return !(state == TcpState.Closed || state == TcpState.CloseWait);
        }

        private static TcpState GetState(TcpClient tcpClient)
        {
            TcpConnectionInformation foo = IPGlobalProperties.GetIPGlobalProperties()
              .GetActiveTcpConnections()
              .SingleOrDefault(x => x.LocalEndPoint.Equals(tcpClient.Client.LocalEndPoint)
                                 && x.RemoteEndPoint.Equals(tcpClient.Client.RemoteEndPoint)
              );

            return foo != null ? foo.State : TcpState.Unknown;
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /// <summary>
        /// Repondre avec la liste des caracteres invalides pour les chemins sur ce systeme
        /// </summary>
        /// <param name="clientStream"></param>
        ///////////////////////////////////////////////////////////////////////////////////////////////
        private static void caracteresInvalidesChemin(SocketUtils clientStream)
        {
            char[] b = Path.GetInvalidPathChars();
            StringBuilder sb = new StringBuilder();
            foreach (char c in b)
                if (c != SocketUtils.TERMINAL) // Attention! mettre le caractere TERMINAL dans le buffer amenerai le client a ignorer les suivants
                    sb.Append(c);

            Mainform.WriteMessageToConsole($"{INVALIDES_CHEMIN}: {sb.ToString()}");
            clientStream.EcritLigne(sb.ToString());
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /// <summary>
        /// Obtient la liste des fichiers d'un repertoire
        /// </summary>
        /// <param name="clientStream"></param>
        ///////////////////////////////////////////////////////////////////////////////////////////////
        private static void listeFichiers(SocketUtils clientStream)
        {
            StringBuilder sb = new StringBuilder();
            try
            {
                string repertoire = clientStream.LireChemin();
                string filtre = clientStream.LireLigne();
                Mainform.WriteMessageToConsole($"{LISTE_FICHIERS} Chemin : {repertoire}, filtre {filtre}");
                string path = Path.Combine(Settings.Default.RepertoireDeBase, repertoire);

                string[] fileEntries = Directory.GetFiles(path, filtre);
                if (checkPath(ref path))
                {
                    if (fileEntries?.Length > 0)
                    {
                        sb.Append(Path.GetFileName(fileEntries[0]));
                        for (int i = 1; i < fileEntries.Length; i++)
                            sb.Append(SocketUtils.SEPARATEUR).Append(Path.GetFileName(fileEntries[i]));
                    }
                }
            }
            catch (Exception)
            {
            }
            clientStream.EcritLigne(sb.ToString());
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        /// <summary>
        /// Obtient la liste des sous repertoires d'un repertoire
        /// </summary>
        /// <param name="clientStream"></param>
        ///////////////////////////////////////////////////////////////////////////////////////////////
        private static void listeRepertoires(SocketUtils clientStream)
        {
            string repertoire = clientStream.LireChemin();
            Mainform.WriteMessageToConsole($"Contenu de: {repertoire}");
            string path = Path.Combine(Settings.Default.RepertoireDeBase, repertoire);

            string[] fileEntries = Directory.GetDirectories(path);
            StringBuilder sb = new StringBuilder();
            if (checkPath(ref path))
            {
                if (fileEntries?.Length > 0)
                {
                    sb.Append(Path.GetFileName(fileEntries[0]));
                    for (int i = 1; i < fileEntries.Length; i++)
                        sb.Append(SocketUtils.SEPARATEUR).Append(Path.GetFileName(fileEntries[i]));
                }
            }
            clientStream.EcritLigne(sb.ToString());
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////
        /// <summary>
        /// Modification de la date d'un fichier
        /// </summary>
        /// <param name="clientStream"></param>
        ///////////////////////////////////////////////////////////////////////////////////////////////
        private static void setDate(SocketUtils clientStream)
        {
            string fichier = clientStream.LireChemin();
            DateTime date = clientStream.LitDate();
            Mainform.WriteMessageToConsole($"Chemin du fichier: {fichier}, date {date}");
            string path = Path.Combine(Settings.Default.RepertoireDeBase, fichier);
            if (checkPath(ref path))
                setDate(path, date);
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        private static void setDate(string path, DateTime date)
        {
            try
            {
                if (date.Ticks != 0)
                {
                    File.SetCreationTime(path, date);
                }
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans transfertfichierdate");
                Mainform.WriteExceptionToConsole(e);
            }
        }

        /// <summary>
        /// Transfert d'un fichier, avec modification de la date
        /// </summary>
        /// <param name="clientStream"></param>
        private static void transferFichierDate(SocketUtils clientStream)
        {
            string result = REPONSE_NON;
            // Nom du fichier a recevoir
            string nom = clientStream.LireChemin();
            DateTime date = clientStream.LitDate();
            string sTaille = clientStream.LireLigne();
            try
            {
                Mainform.WriteMessageToConsole($"Chemin du fichier: {nom}, taille {sTaille}, date {date}");

                // Taille du fichier a recevoir
                long taille = long.Parse(sTaille);
                string path = Path.Combine(Settings.Default.RepertoireDeBase, nom);
                if (checkPath(ref path))
                {
                    // Creer le fichier et y verser le contenu de la socket
                    FileStream f = File.Create(path);
                    long nbLus = clientStream.LitFichier(taille, f);
                    f.Close();
                    setDate(path, date);
                    result = REPONSE_OUI;
                }
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans transfertfichierdate");
                Mainform.WriteExceptionToConsole(e);
            }

            Mainform.WriteMessageToConsole($"Réponse: {result}");
            clientStream.EcritLigne(result);
        }

        private static void transferFichier(SocketUtils clientStream)
        {
            string result = REPONSE_NON;
            try
            {
                // Nom du fichier a recevoir
                string nom = clientStream.LireChemin();
                string sTaille = clientStream.LireLigne();
                Mainform.WriteMessageToConsole($"Chemin du fichier: {nom}, taille {sTaille}");

                // Taille du fichier a recevoir
                long taille = long.Parse(sTaille);

                string path = Path.Combine(Settings.Default.RepertoireDeBase, nom);
                if (checkPath(ref path))
                {
                    FileStream f = File.Create(path);
                    long nbLus = clientStream.LitFichier(taille, f);
                    f.Close();
                    result = REPONSE_OUI;
                }
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans transfertfichier");
                Mainform.WriteExceptionToConsole(e);
            }

            Mainform.WriteMessageToConsole($"Réponse: {result}");
            clientStream.EcritLigne(result);
        }



        /// <summary>
        /// Verifie l'existence d'un fichier
        /// Reponse: REPONSE_OUI ou REPONSE_NON
        /// </summary>
        /// <param name="clientStream"></param>
        private static void fichierExiste(SocketUtils clientStream)
        {
            string result = REPONSE_NON;
            try
            {
                string fichier = clientStream.LireChemin();
                Mainform.WriteMessageToConsole($"Test du fichier: {fichier}");

                string path = Path.Combine(Settings.Default.RepertoireDeBase, fichier);
                if (checkPath(ref path))
                {
                    bool existe = File.Exists(path);
                    result = existe ? REPONSE_OUI : REPONSE_NON;
                }
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans lireLigne");
                Mainform.WriteExceptionToConsole(e);
            }

            Mainform.WriteMessageToConsole($"Réponse: {result}");
            clientStream.EcritLigne(result);
        }

        private static void creerRepertoire(SocketUtils clientStream)
        {
            string result = REPONSE_NON;
            try
            {
                string repertoire = clientStream.LireChemin();
                Mainform.WriteMessageToConsole($"Répertoire à créer: {repertoire}");
                string path = Path.Combine(Settings.Default.RepertoireDeBase, repertoire);
                if (checkPath(ref path))
                {
                    Directory.CreateDirectory(path);
                    result = REPONSE_OUI;
                }
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans creerRepertoire");
                Mainform.WriteExceptionToConsole(e);
            }

            Mainform.WriteMessageToConsole($"Réponse: {result}");
            clientStream.EcritLigne(result);
        }

        /// <summary>
        /// Requete juste pour tester l'adresse
        /// </summary>
        /// <param name="clientStream"></param>
        private static void testAdresse(SocketUtils clientStream)
        {
            clientStream.EcritLigne(REPONSE_OUI);
        }

        /// <summary>
        /// Verifie la validite d'un chemin:
        /// - caracteres incorrectes
        /// - le chemin doit absolument se trouver sous la racine principale (eviter les ../../)
        /// </summary>
        /// <param name="path"></param>
        /// <returns></returns>
        private static bool checkPath(ref string path)
        {
            String invalides = new String(Path.GetInvalidPathChars());
            StringBuilder p = new StringBuilder();
            for (int i = 0; i < path.Length; i++)
                if (invalides.IndexOf(path[i]) == -1)
                    p.Append(path[i]);

            if (p.Length == 0)
                return false;

            String sPath = p.ToString();

            // Combinaisons interdites:
            if (sPath.IndexOf(@"\\") != -1)
                return false;
            // Combinaisons interdites:
            if (sPath.IndexOf(@"\..\") != -1)
                return false;

            if (!isParentDirectory(Settings.Default.RepertoireDeBase, sPath))
                return false;

            path = sPath;
            return true;
        }

        private static bool isParentDirectory(string repertoireDeBase, string sPath)
        {
            if (repertoireDeBase.Equals(sPath))
                // Les deux repertoires sont identiques
                return true;

            try
            {
                // Repertoire au dessus?
                String parent = Directory.GetParent(sPath).FullName;
                if (parent.Length > 0)
                    return isParentDirectory(repertoireDeBase, parent);
            }
            catch (Exception)
            {

            }
            Mainform.WriteErrorToConsole("ERREUR: utilisation d'un chemin qui n'est pas sous le répertoire racine: " + sPath);
            return false;
        }
    }
}
