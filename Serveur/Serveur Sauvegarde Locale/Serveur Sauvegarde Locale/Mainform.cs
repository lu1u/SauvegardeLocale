using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Serveur_Sauvegarde_Locale
{
    public partial class Mainform : Form
    {
        private static Mainform _instance;
        private Serveur _serveur;

        public Mainform()
        {
            _instance = this;

            InitializeComponent();
        }

        private void onMainFormResize(object sender, EventArgs e)
        {
            if (this.WindowState == FormWindowState.Minimized)
            {
                Hide();
                notifyIcon.Visible = true;
            }
        }

        private void onNotifyIconDoubleClic(object sender, EventArgs e)
        {
            Show();
            WindowState = FormWindowState.Normal;
            notifyIcon.Visible = false;
        }

        private void onBrowseFolderButtonClic(object sender, EventArgs e)
        {
            folderBrowserDialog.SelectedPath = textBoxRepertoire.Text;
            if (folderBrowserDialog.ShowDialog() == DialogResult.OK)
            {
                textBoxRepertoire.Text = folderBrowserDialog.SelectedPath;
                Settings.Default.RepertoireDeBase = folderBrowserDialog.SelectedPath;
                Settings.Default.Save();
                notifyIcon.Text = NetworkUtils.LocalIPAddress().ToString() + " (" + Dns.GetHostName() + ")" + ":" + Settings.Default.Port;
            }
        }


        private void EnableParametres(bool enabled)
        {
            notifyIcon.Text = NetworkUtils.LocalIPAddress().ToString() + " (" + Dns.GetHostName() + ")" + ":" + Settings.Default.Port ;
            numericUpDownPort.Enabled = enabled;
            textBoxRepertoire.Enabled = enabled;
            buttonOpenDlg.Enabled = enabled;
        }

        async private void onMainFormLoad(object sender, EventArgs e)
        {
            textBoxRepertoire.Text = Settings.Default.RepertoireDeBase;
            int port = Settings.Default.Port;
            numericUpDownPort.Text = port.ToString();

            IPAddress local = NetworkUtils.LocalIPAddress();
            textBoxAdresse.Text = local.ToString() + " (" + Dns.GetHostName() + ")"; 
            _serveur = new Serveur(local, port);
            if (await _serveur.demarre())
                EnableParametres(false);
        }

        private void onPortChanged(object sender, EventArgs e)
        {
            Settings.Default.Port = (int)numericUpDownPort.Value;
            Settings.Default.Save();
            notifyIcon.Text = NetworkUtils.LocalIPAddress().ToString() + " (" + Dns.GetHostName() + ")" + ":" + Settings.Default.Port;
        }

        private void onTextboxRepertoireChanged(object sender, EventArgs e)
        {
            Settings.Default.RepertoireDeBase = textBoxRepertoire.Text;
            Settings.Default.Save();
            notifyIcon.Text = NetworkUtils.LocalIPAddress().ToString() + " (" + Dns.GetHostName() + ")" + ":" + Settings.Default.Port;
        }



        private void onMainformClosing(object sender, FormClosingEventArgs e)
        {
            try
            {
                _serveur?.arrete();
            }
            catch (Exception ex)
            {

            }
        }


        async private void ButtonStartStop_Click(object sender, EventArgs e)
        {
            if (_serveur.isDemarre())
            {
                if (await _serveur.arrete())
                // Arreter le serveur
                {
                    buttonStartStop.Text = "Serveur arrêté, cliquez pour le démarrer";
                    EnableParametres(true);
                }
            }
            else
            {
                if (await _serveur.demarre())
                {
                    buttonStartStop.Text = "Serveur démarré, cliquez pour l'arrêter";
                    EnableParametres(false);
                }
            }
        }

    }
}
