using System;
using System.Diagnostics;
using System.IO;
using System.Net.Sockets;
using System.Text;
/// <summary>
/// Classe utilitaire pour lire/ecrire les requetes en provenance de l'appli sauvegare
/// </summary>

namespace Serveur_Sauvegarde_Locale
{
    public class SocketUtils
    {
        public const char TERMINAL = '\0';
        public const char SEPARATEUR = '\n';
        public static readonly string INVALIDES = new String(Path.GetInvalidPathChars()); //= @"[^\w\.@-]";
        public static byte[] BUFFER_TERMINAL = { 0 };
        private static char[] SEPARATEUR_DATE = { '=' };
        static UTF8Encoding _encoder = new UTF8Encoding();  // Encodage utilisé entre le client Android et le serveur Windows
        private NetworkStream _clientStream;

        static SocketUtils()
        {
            INVALIDES = INVALIDES + new String(Path.GetInvalidFileNameChars());
        }


        public SocketUtils(NetworkStream clientStream)
        {
            _clientStream = clientStream;
        }

        /// <summary>
        /// Ecrit un fichier dans la socket
        /// </summary>
        /// <param name="taille">Nombre d'octets a ecrire</param>
        /// <param name="sInput">Stream dans lequel se trouvent les octets</param>
        public void EcritFichier(long taille, FileStream sInput)
        {
#if CHRONO
            Stopwatch stopWatch = new Stopwatch();
            stopWatch.Start();
#endif
            // Lecture des octets
            byte[] buffer = new byte[1024 * 32];
            long nbLus = 0;
            try
            {
                while (nbLus < taille)
                {
                    int nbAlire = (int)Math.Min(buffer.Length, taille - nbLus);
                    int nb = sInput.Read(buffer, 0, nbAlire);
                    _clientStream.Write(buffer, 0, nb);
                    nbLus += nb;
                }
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans EcritFichier");
                Mainform.WriteExceptionToConsole(e);
            }
#if CHRONO
            stopWatch.Stop();
            TimeSpan ts = stopWatch.Elapsed;
            double vitesse = nbLus / ts.TotalSeconds;
            String vit;
            if (vitesse > 1000)
                vit = (vitesse / 1000.0).ToString("F2") + " ko/s";
            else
                vit = vitesse.ToString("F2") + " o/s";
            Mainform.WriteMessageToConsole($"{nbLus} octets écrits en {ts.TotalSeconds.ToString("F3")} sec soit {vit}");
#endif
        }

        /// <summary>
        /// Lit un nombre donne d'octets dans la socket et les ecrit dans un fichier
        /// </summary>
        /// <param name="taille"></param>
        /// <param name="clientStream"></param>
        /// <param name="sOutput"></param>
        /// <returns></returns>
        public long LitFichier(long taille, FileStream sOutput)
        {
            Stopwatch stopWatch = new Stopwatch();
            stopWatch.Start();

            // Lecture des octets
            byte[] buffer = new byte[1024 * 32];
            long nbLus = 0;
            try
            {
                while (nbLus < taille)
                {
                    int nbAlire = (int)Math.Min(buffer.Length, taille - nbLus);
                    int nb = _clientStream.Read(buffer, 0, nbAlire);
                    sOutput.Write(buffer, 0, nb);
                    nbLus += nb;
                }
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans transfertfichier");
                Mainform.WriteExceptionToConsole(e);
            }

            stopWatch.Stop();
            TimeSpan ts = stopWatch.Elapsed;
            double vitesse = nbLus / ts.TotalSeconds;
            String vit;
            if (vitesse > 1000)
                vit = (vitesse / 1000.0).ToString("F2") + " ko/s";
            else
                vit = vitesse.ToString("F2") + " o/s";
            Mainform.WriteMessageToConsole($"{nbLus} octets lus en {ts.TotalSeconds.ToString("F3")} sec soit {vit}");
            return nbLus;
        }
        /// <summary>
        /// Ecrit une chaine de caracteres dans le socket
        /// </summary>
        /// <param name="clientStream"></param>
        /// <param name="ligne"></param>
        public void EcritLigne(string ligne)
        {
            byte[] buffer = _encoder.GetBytes(ligne);
            _clientStream.Write(buffer, 0, buffer.Length);
            _clientStream.Write(BUFFER_TERMINAL, 0, BUFFER_TERMINAL.Length);
            _clientStream.Flush();
        }


        /// <summary>
        /// Lit une ligne (terminee par \0 dans le stream d'entree
        /// </summary>
        /// <param name="clientStream"></param>
        /// <returns></returns>
        public string LireLigne()
        {
            while (!_clientStream.DataAvailable) ;
            byte[] b = new byte[4096];
            try
            {
                byte[] charCur = new byte[1];
                _clientStream.Read(charCur, 0, charCur.Length);
                int bytesRead = 0;
                while (charCur[0] != TERMINAL)
                {
                    b[bytesRead] = charCur[0];
                    _clientStream.Read(charCur, 0, charCur.Length);
                    bytesRead++;
                }

                return _encoder.GetString(b, 0, bytesRead);
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans lireLigne");
                Mainform.WriteExceptionToConsole(e);
            }

            return null;
        }

        public string LireChemin()
        {
            string ligne = LireLigne();
            return NettoieChemin(ligne);
        }

        private static string NettoieChemin(string ligne)
        {
            // Separer les elements du chemin en ses parties
            string[] morceaux = ligne.Split('\\');
            if (morceaux == null)
                return ligne;
            if (morceaux.Length == 0)
                return ligne;

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < morceaux.Length; i++)
            {
                if (sb.Length > 0)
                    sb.Append("\\");

                for (int j = 0; j < morceaux[i].Length; j++)
                    if (INVALIDES.IndexOf(morceaux[i][j]) == -1)
                        sb.Append(morceaux[i][j]);
            }

            return sb.ToString();
        }


        public DateTime LitDate()
        {
            string date;
            try
            {
                date = LireLigne();
                string[] morceaux = date.Split(SEPARATEUR_DATE);

                int annee = int.Parse(morceaux[0]);
                int mois = int.Parse(morceaux[1]);
                int jour = int.Parse(morceaux[2]);
                int heure = int.Parse(morceaux[3]);
                int minute = int.Parse(morceaux[4]);
                int seconde = int.Parse(morceaux[5]);


                return new DateTime(annee, mois, jour, heure, minute, seconde);
            }
            catch (Exception e)
            {
                return new DateTime(0);
            }
        }

    }
}
