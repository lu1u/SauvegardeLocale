using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace Serveur_Sauvegarde_Locale
{
    class Serveur
    {
        private Thread _serveurThread;
        IPAddress _adresse;

        int _port;
        TcpListener _tcpListener;
        private bool _enroute;

        public Serveur(IPAddress adresse, int port)
        {
            _adresse = adresse;
            _port = port;
        }


        internal bool isDemarre()
        {
            return _enroute;
        }

        internal async Task<bool> demarre()
        {
            _tcpListener = null;

            try
            {
                // Set the listener on the local IP address 
                // and specify the port.
                _tcpListener = new TcpListener(_adresse, _port);
                _enroute = true;
                _tcpListener.Start();

                Mainform.WriteMessageToConsole("#######################################################################");
                Mainform.WriteMessageToConsole("# Sauvegarde Locale, serveur                                          #");
                Mainform.WriteMessageToConsole("# Lucien Pilloni 2019                                                 #");
                Mainform.WriteMessageToConsole("#######################################################################");
                Directory.CreateDirectory(Settings.Default.RepertoireDeBase);
                Mainform.WriteMessageToConsole("Répertoire de base  " + Settings.Default.RepertoireDeBase);
                Mainform.WriteMessageToConsole("Adresse du serveur  " + _tcpListener.LocalEndpoint);
                Mainform.WriteMessageToConsole("En attente de connexion...");
                _serveurThread = new Thread(serveurThread);
                _serveurThread.Start();
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: " + e.ToString());
                _enroute = false;
            }

            return true;
        }

        private void serveurThread()
        {
            while (_enroute)
            {
                if (_tcpListener != null)
                {
                    try
                    {
                        TcpClient client = _tcpListener.AcceptTcpClient();
                        Thread clientThread = new Thread(new ParameterizedThreadStart(RequestProcessor.traiteRequete));
                        clientThread.Start(client);
                    }
                    catch (ThreadInterruptedException e)
                    {
                        _enroute = false;
                    }
                    catch (Exception e)
                    {
                        //Mainform.WriteErrorToConsole("Erreur dans serveurThread");
                        //Mainform.WriteExceptionToConsole(e);
                    }
                }
            }

            _tcpListener?.Stop();
        }

        internal async Task<bool> arrete()
        {
            try
            {
                _enroute = false;
                _tcpListener?.Stop();
                _serveurThread?.Abort();
                _tcpListener = null;
                Mainform.WriteMessageToConsole("Serveur arrêté");
            }
            catch (Exception e)
            {
                Mainform.WriteErrorToConsole("Erreur dans serveurThread");
                Mainform.WriteExceptionToConsole(e);
            }
            return true;
        }
    }
}
